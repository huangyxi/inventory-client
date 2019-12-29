package client;

/**
 * Main
 */
public class Main {
	public static void main(String[] args) {
		String address = "vm-ubuntu.local";
		int port = 1234;
		var c = new Client(address, port);
		c.start();
	}
}