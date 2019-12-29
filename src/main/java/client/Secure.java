package client;

import java.security.MessageDigest;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Secure
 */
public class Secure {
	private static MessageDigest sha256 = null;
	private static SecretKeySpec secretKey;
	private static byte[] key;
	static {
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (Exception e) {
			Log.error(e);
		}
	}

	public static String encode(String password, String key) {
		String catPassword = password + key;
		if (null == sha256) {
			return null;
		}
		byte[] encodedHash = sha256.digest(catPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < encodedHash.length; i++) {
			String hex = Integer.toHexString(0xff & encodedHash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		String encoded = hexString.toString();
		return encoded;
	}

	public static String encrypt(String strToEncrypt, String secret) {
		try {
			Cipher aes = Cipher.getInstance("AES");
			key = sha256.digest(secret.getBytes("UTF-8"));
			secretKey = new SecretKeySpec(key, "AES");
			aes.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(aes.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, String secret) {
		try {
			Cipher aes = Cipher.getInstance("AES");
			key = sha256.digest(secret.getBytes("UTF-8"));
			secretKey = new SecretKeySpec(key, "AES");
			aes.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(aes.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}
}