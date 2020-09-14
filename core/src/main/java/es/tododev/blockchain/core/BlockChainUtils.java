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
	
	public static byte[] toBytes(Block block) throws BlockChainException {
		StringBuilder builder = new StringBuilder();
		builder.append(block.getPreviousHash());
		for (Transaction transaction : block.getTransactions()) {
			builder.append(transaction.getSignaure());
		}
		builder.append(block.getProofOfWork());
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			return md.digest(builder.toString().getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw BlockChainException.errorCannotHash(block, e);
		}
	}

	public static byte[] toBytes(Transaction transaction) throws BlockChainException {
		StringBuilder builder = new StringBuilder();
		builder.append(transaction.getIndex());
		builder.append(transaction.getFrom());
		builder.append(transaction.getTo());
		builder.append(transaction.getAmount());
		builder.append(transaction.getType());
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			return md.digest(builder.toString().getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw BlockChainException.errorCannotHash(transaction, e);
		}
	}
	
	public static boolean isHashValid(byte[] sha256) {
		String binary = BlockChainUtils.toBinary(sha256);
		boolean valid = binary.startsWith("000000000");
		return valid;
	}
	
	public static boolean test(String base, Long proofOfWork) {
		String compose = base + proofOfWork;
		try {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			byte[] digest = md.digest(compose.getBytes());
			byte[] sha256 = BlockChainUtils.sha256(digest);
			return isHashValid(sha256);
		} catch (NoSuchAlgorithmException | BlockChainException e) {
			return false;
		}
	}

}
