package es.tododev.blockchain.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

public class Block implements Serializable {

	private static final long serialVersionUID = 1L;
	private final long index;
	private final List<Transaction> transactions;
	private long proofOfWork;
	private final byte[] previousHash;
	
	public Block(long index, List<Transaction> transactions, long proofOfWork, byte[] previousHash) {
		this.index = index;
		this.transactions = transactions;
		this.proofOfWork = proofOfWork;
		this.previousHash = previousHash;
	}
	
	public long getIndex() {
		return index;
	}
	public long getProofOfWork() {
		return proofOfWork;
	}
	public byte[] getPreviousHash() {
		return previousHash;
	}
	public List<Transaction> getTransactions() {
		return transactions;
	}
	public void setProofOfWork(long proofOfWork) {
		this.proofOfWork = proofOfWork;
	}

	@Override
	public String toString() {
		return "Block [index=" + index + ", transactions=" + transactions.size() + ", proofOfWork=" + proofOfWork
				+ ", previousHash=" + Base64.getEncoder().encodeToString(previousHash) + "]";
	}

	public static class Transaction implements Serializable {

		private static final long serialVersionUID = 1L;
		private final long index;
		private final byte[] senderId;
		private final byte[] receiverId;
		private final BigDecimal amount;
		// It could be bitcoins, liters, KWH, etc
		private final String type;
		private byte[] senderSignaure;
		
		public Transaction(long index, byte[] senderId, byte[] receiverId, BigDecimal amount, String type) {
			this.index = index;
			this.senderId = senderId;
			this.receiverId = receiverId;
			this.amount = amount;
			this.type = type;
		}

		public byte[] getSenderId() {
			return senderId;
		}
		public byte[] getReceiverId() {
			return receiverId;
		}
		public BigDecimal getAmount() {
			return amount;
		}
		public String getType() {
			return type;
		}
		public byte[] getSenderSignaure() {
			return senderSignaure;
		}
		public long getIndex() {
			return index;
		}
		public void setSenderSignaure(byte[] senderSignaure) {
			this.senderSignaure = senderSignaure;
		}
	}
	
}
