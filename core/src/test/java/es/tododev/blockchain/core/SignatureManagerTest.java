package es.tododev.blockchain.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.junit.Test;

public class SignatureManagerTest {

	@Test
	public void success()
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		byte[] data = "this is a content example".getBytes();
		KeyPair pair = TestUtils.generate();
		byte[] signed = SignatureManager.sign(data, pair.getPrivate());
		assertTrue(SignatureManager.verify(data, pair.getPublic(), signed));
	}
	
	@Test
	public void failure()
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		byte[] data = "this is a content example".getBytes();
		KeyPair pair = TestUtils.generate();
		byte[] signed = SignatureManager.sign(data, pair.getPrivate());
		data = "modified message".getBytes();
		assertFalse(SignatureManager.verify(data, pair.getPublic(), signed));
	}
	
	@Test
	public void createPublicKeyFromBytes() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, InvalidKeySpecException {
		byte[] data = "this is a content example".getBytes();
		KeyPair pair = TestUtils.generate();
		byte[] signed = SignatureManager.sign(data, pair.getPrivate());
		PublicKey publicKey = SignatureManager.publicKey(pair.getPublic().getEncoded());
		assertTrue(SignatureManager.verify(data, publicKey, signed));
	}
}
