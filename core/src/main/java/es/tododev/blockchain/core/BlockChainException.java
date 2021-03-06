package es.tododev.blockchain.core;

import java.util.Base64;

import es.tododev.blockchain.core.Block.Transaction;

public class BlockChainException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private static final int FAILURE_BLOCK_NOT_FOUND = 100;
	private static final int FAILURE_TRANSACTION_INVALID = 101;
	private static final int FAILURE_TRANSACTION_DUPLICATED = 102;
	private static final int FAILURE_BLOCK_INVALID = 103;
	private static final int FAILURE_TRANSACTION_CANNOT_SIGN = 104;
	private static final int ERROR_SECURITY_TRANSACTION = 500;
	private static final int ERROR_DIGGEST_ALGORITHM = 501;
	private static final int ERROR_SERIALIZE_OBJECT = 502;
	private static final int ERROR_INTERRUPTED = 503;
	
	
	private final int code;
	private final String message;

	private BlockChainException(String message, Throwable cause, int code) {
		super(message, cause);
		this.code = code;
		this.message = message;
	}

	private BlockChainException(String message, int code) {
		super(message);
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public static BlockChainException blockNotFound(byte[] previousHash) {
		String content = "null";
		if (previousHash != null) {
			content = Base64.getEncoder().encodeToString(previousHash);
		}
		return new BlockChainException("Block not found, previousHash = " + content, FAILURE_BLOCK_NOT_FOUND);
	}
	
	public static BlockChainException errorSecurityTransaction(Transaction transaction, Exception cause) {
		return new BlockChainException("Cannot validate transaction in block " + transaction , cause, ERROR_SECURITY_TRANSACTION);
	}
	
	public static BlockChainException transactionInvalid(Transaction transaction) {
		return new BlockChainException("Transaction invalid signature " + transaction , FAILURE_TRANSACTION_INVALID);
	}
	
	public static BlockChainException errorCannotHash(Object object, Exception cause) {
		return new BlockChainException("Cannot hash " + object , cause, ERROR_DIGGEST_ALGORITHM);
	}

	public static BlockChainException errorCannotSerializeToBytes(Object object, Exception cause) {
		return new BlockChainException("Cannot serialize to bytes " + object , cause, ERROR_SERIALIZE_OBJECT);
	}

	public static BlockChainException transactionDuplicated(Transaction transaction) {
		return new BlockChainException("Transaction already exists " + transaction , FAILURE_TRANSACTION_DUPLICATED);
	}

	public static BlockChainException errorBlockInvalid(byte[] sha256) {
		return new BlockChainException("Block is invalid " + sha256 , FAILURE_BLOCK_INVALID);
	}
	
	public static BlockChainException transactionCannotSign(Transaction transaction, Exception cause) {
		return new BlockChainException("Transaction cannot be signed " + transaction, cause, FAILURE_TRANSACTION_CANNOT_SIGN);
	}
	
	public static BlockChainException errorInterrupted(Exception cause) {
		return new BlockChainException("Operation has been interrupted ", cause, ERROR_INTERRUPTED);
	}

}
