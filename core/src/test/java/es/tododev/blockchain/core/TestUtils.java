package es.tododev.blockchain.core;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class TestUtils {

	public static int random(int min, int max) {
		return (int)(Math.random() * max + min);
	}

	public static KeyPair generate() throws NoSuchAlgorithmException, NoSuchProviderException {
		SecureRandom secureRandom = new SecureRandom();
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048, secureRandom);
		KeyPair pair = keyPairGenerator.generateKeyPair();
		return pair;
	}
	
}
