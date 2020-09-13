package es.tododev.blockchain.core;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class SignatureManager {

	public static final String SIGNATURE = "SHA256withRSA";

	public static byte[] sign(byte[] data, PrivateKey privateKey)
			throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		Signature signature = Signature.getInstance(SIGNATURE);
		signature.initSign(privateKey);
		signature.update(data);
		byte[] signed = signature.sign();
		return signed;
	}

	public static boolean verify(byte[] data, PublicKey publicKey, byte[] signed)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance(SIGNATURE);
		signature.initVerify(publicKey);
		signature.update(data);
		return signature.verify(signed);
	}
	
	public static PublicKey publicKey(byte[] publicKeyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
		return publicKey;
	}

}
