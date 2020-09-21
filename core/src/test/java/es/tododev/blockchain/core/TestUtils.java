package es.tododev.blockchain.core;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import es.tododev.blockchain.core.Block.Transaction;

public class TestUtils {

	public static int random(int min, int max) {
		return (int)(Math.random() * max + min);
	}

	public static KeyPair generate() {
		try {
			SecureRandom secureRandom = new SecureRandom();
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048, secureRandom);
			KeyPair pair = keyPairGenerator.generateKeyPair();
			return pair;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Cannot generate keypair", e);
		}
		
	}
	
	public static List<KeyPair> generate(int size) {
		List<KeyPair> users = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			users.add(TestUtils.generate());
		}
		return users;
	}
	
	public static Transaction createTransaction(List<KeyPair> users) {
		try {
			KeyPair from = users.get(TestUtils.random(0, users.size() - 1));
			KeyPair to = users.get(TestUtils.random(0, users.size() - 1));
			Transaction transaction = new Transaction(from.getPublic().getEncoded(), to.getPublic().getEncoded(), new BigDecimal("100"), "anyType");
			byte[] signature = SignatureManager.sign(transaction.content(), from.getPrivate());
			transaction.setSignaure(signature);
			return transaction;
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | BlockChainException e) {
			throw new RuntimeException("Cannot generate transaction", e);
		}
	}
	
	public static List<Transaction> createTransactions(List<KeyPair> users, int size) {
		List<Transaction> transactions = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			transactions.add(createTransaction(users));
		}
		return transactions;
	}
	
	public static Block createBlock(byte[] previousHash, List<Transaction> transactions) {
		Block block = new Block(transactions, previousHash);
		long proofOfWork = new ProofOfWorkImpl().calculate(block);
		block.setProofOfWork(proofOfWork);
		return block;
	}
	
}
