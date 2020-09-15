package es.tododev.blockchain.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import es.tododev.blockchain.core.Block.Transaction;

public class BlockChainUtils {

	private static final String ALGORITHM = "SHA-256";
	
	public static byte[] sha256(byte[] serialized) throws BlockChainException {
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			return md.digest(serialized);
		} catch (NoSuchAlgorithmException e) {
			throw BlockChainException.errorCannotHash(serialized, e);
		}
	}
	
	public static String toBinary(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
		}
		return builder.toString();
	}
	
	public static byte[] sha256(Block block) throws BlockChainException {
		StringBuilder builder = new StringBuilder(base(block));
		builder.append(block.getProofOfWork());
		return sha256(builder.toString().getBytes());
	}
	
	public static String base(Block block) {
		StringBuilder builder = new StringBuilder();
		builder.append(block.getPreviousHash());
		for (Transaction transaction : block.getTransactions()) {
			builder.append(transaction.getSignaure());
		}
		return builder.toString();
	}
	
	public static boolean isHashValid(byte[] sha256) {
		String binary = BlockChainUtils.toBinary(sha256);
		boolean valid = binary.startsWith("000000000");
		return valid;
	}
	
	public static boolean test(String base, Long proofOfWork) {
		StringBuilder builder = new StringBuilder(base).append(proofOfWork);
		byte[] sha256 = sha256(builder.toString().getBytes());
		return isHashValid(sha256);
	}

}
