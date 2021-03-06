package es.tododev.blockchain.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class Block implements Serializable {

	private static final long serialVersionUID = 1L;
	private final List<Transaction> transactions;
	private long proofOfWork;
	private final byte[] previousHash;
	
	public Block(List<Transaction> transactions, byte[] previousHash) {
		this.transactions = transactions;
		this.previousHash = previousHash;
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
		return "Block [transactions=" + transactions.size() + ", proofOfWork=" + proofOfWork
				+ ", previousHash=" + Base64.getEncoder().encodeToString(previousHash) + "]";
	}

	public static class Transaction implements Serializable, Cloneable {

		private static final long serialVersionUID = 1L;
		private final String id;
		private final byte[] from;
		private final byte[] to;
		private final BigDecimal amount;
		// It could be bitcoins, liters, KWH, etc
		private final String type;
		private byte[] signature;
		private Transaction minerFee;
		
		public Transaction(byte[] from, byte[] to, BigDecimal amount, String type) {
			this(UUID.randomUUID().toString(), from, to, amount, type);
		}
		
		public Transaction(String id, byte[] from, byte[] to, BigDecimal amount, String type) {
			this.id = id;
			this.from = from;
			this.to = to;
			this.amount = amount;
			this.type = type;
		}

		public byte[] getFrom() {
			return from;
		}
		public byte[] getTo() {
			return to;
		}
		public BigDecimal getAmount() {
			return amount;
		}
		public String getType() {
			return type;
		}
		public byte[] getSignaure() {
			return signature;
		}
		public String getId() {
			return id;
		}
		public void setSignaure(byte[] signature) {
			this.signature = signature;
		}
		public Transaction getMinerFee() {
			return minerFee;
		}
		public void setMinerFee(Transaction minerFee) {
			this.minerFee = minerFee;
		}
		@Override
		protected Transaction clone() {
			Transaction clone = new Transaction(id, from, to, amount, type);
			clone.setSignaure(signature);
			return clone;
		}

		public byte[] content() {
			StringBuilder builder = new StringBuilder();
			builder.append(id);
			builder.append(from);
			builder.append(to);
			builder.append(amount);
			builder.append(type);
			return builder.toString().getBytes();
		}
		@Override
		public String toString() {
			return "Transaction [id=" + id + ", amount=" + amount + ", type=" + type + "]";
		}
	}
	
}
