package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private String key = null;
	private String secret;
	private static final String EXIT = "exit";
	private int nEncrypt = 0;
	private int nDecrypt = 0;

	public static void main(String[] args) {
		String address = "vm-ubuntu.local";
		int port = 1234;// Integer.parseInt(args[1]);
		var c = new Client(address, port);
		c.start();
		// Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		// c.close();
		// }));
	}

	public Client(String address, int port) {
		try {
			socket = new Socket(address, port);
			this.in = socket.getInputStream();
			this.out = socket.getOutputStream();
		} catch (UnknownHostException e) {
			Log.info("Host unknown");
			Log.warning(e);
			System.exit(1);
		} catch (IOException e) {
			Log.info("Server doesn't start or port wrong");
			Log.warning(e);
			System.exit(1);
		}
		Log.block("Client started");
	}

	public void start() {
		Reader reader = new Reader();
		Writer writer = new Writer();
		reader.start();
		writer.start();
	}

	public void close() {
		try {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			if (socket != null) {
				socket.close();
			}
			System.exit(0);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	private class Reader extends Thread {
		private InputStreamReader streamReader = new InputStreamReader(in);
		private BufferedReader reader = new BufferedReader(streamReader);

		@Override
		public void run() {
			try {
				key = reader.readLine();
				String line = "";
				line = reader.readLine();
				if (null == line || EXIT.equals(line)) {
					Log.block("Server closed");
					return;
				}
				System.out.println(line);
				System.out.print("> ");
				while (!socket.isClosed()) {
					line = reader.readLine();
					if (null == line) {
						continue;
					}
					if (EXIT.equals(line)) {
						break;
					}
					System.out.println("[received]: " + line);
					line = decrypt(line);
					System.out.println(line.replaceAll("\\\\\\\\", "\\\\").replaceAll("\\\\n", "\n"));
					System.out.print("> ");
				}
				Log.block("Server closed");
			} catch (IOException e) {
				Log.block("Disconnected abnormally");
			} finally {
				try {
					if (streamReader != null) {
						streamReader.close();
					}
					if (reader != null) {
						reader.close();
					}
					close();
				} catch (IOException e) {
					Log.error(e.getMessage());
				}
			}
		}
	}

	private class Writer extends Thread {
		private PrintWriter writer = new PrintWriter(out);
		private BufferedReader reader = null;

		@Override
		public void run() {
			try {
				String line = "";
				String idUsername = null;
				do {
					System.out.print("ID/Username: ");
					idUsername = System.console().readLine();
					if (null == idUsername) {
						return;
					}
				} while (0 == idUsername.length());
				String password = null;
				do {
					System.out.print("Password: ");
					password = String.valueOf(System.console().readPassword());
					if (null == password) {
						return;
					}
				} while (0 == idUsername.length());
				System.out.println("[  key  ]: " + key);
				var encoded = Secure.encode(password, key);
				System.out.println("[encoded]: " + encoded);
				var login = idUsername + " " + encoded;
				writer.println(login);
				writer.flush();
				secret = Secure.encode(key, password);
				System.out.println("[ secret]: " + secret);
				reader = new BufferedReader(new InputStreamReader(System.in));
				while (!socket.isClosed() && !EXIT.equals(line)) {
					line = reader.readLine();
					if (line == null) {
						break;
					}
					if ("".equals(line)) {
						System.out.print("> ");
						continue;
					}
					var encrypted = encrypt(line);
					System.out.println("[  sent  ]: " + encrypted);
					writer.println(encrypted);
					writer.flush();
				}
				Log.block("Client existed");
				close();
			} catch (IOException e) {
				Log.block("Connection closed");
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
					if (reader != null) {
						reader.close();
					}
					close();
				} catch (IOException e) {
					Log.error(e);
				}
			}
		}
	}

	private String encrypt(String strToEncrypt) {
		return Secure.encrypt(strToEncrypt, secret + nEncrypt++);
	}

	private String decrypt(String strToDecrypt) {
		return Secure.decrypt(strToDecrypt, secret + nDecrypt++);
	}

}