package es.tododev.blockchain.core;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import es.tododev.blockchain.core.Block.Transaction;

public class BlockManagerTest {

	private static final BlockValidator validator = new BlockValidatorDefault();
	private static final List<KeyPair> users = new ArrayList<>();
	private static final int USERS = 10;
	private static final int TRANSACTIONS = 100;
	private long transactionIndex = 0;
	private long blockIndex = 0;
	private byte[] previousHash = new byte[0];

	@BeforeClass
	public static void beforeClass() throws NoSuchAlgorithmException, NoSuchProviderException {
		for (int i = 0; i < USERS; i++) {
			users.add(TestUtils.generate());
		}
	}

	@Test
	public void normal() throws BlockChainException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		BlockChainStorage storage = new BlockChainStorageDefault();
		BlockManager manager = new BlockManagerImpl(storage, validator);
		Block block = createBlock();
		manager.add(block);
		manager.add(createBlock());
		manager.add(createBlock());
		manager.add(createBlock());
	}

	private Transaction createTransaction() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, BlockChainException {
		KeyPair sender = users.get(TestUtils.random(0, users.size() - 1));
		KeyPair receiver = users.get(TestUtils.random(0, users.size() - 1));
		Transaction transaction = new Transaction(transactionIndex, sender.getPublic().getEncoded(), receiver.getPublic().getEncoded(), new BigDecimal("100"), "anyType");
		byte[] senderSignaure = SignatureManager.sign(BlockChainUtils.toBytes(transaction), sender.getPrivate());
		transaction.setSenderSignaure(senderSignaure);
		transactionIndex++;
		return transaction;
	}
	
	private Block createBlock() throws BlockChainException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		List<Transaction> transactions = new ArrayList<>(TRANSACTIONS);
		for (int i = 0; i < TRANSACTIONS; i++) {
			transactions.add(createTransaction());
		}
		long proofOfWork = 0;
		boolean valid = false;
		Block block = null;
		while (!valid) {
			block = new Block(blockIndex, transactions, proofOfWork, previousHash);
			valid = validator.isValid(block);
			proofOfWork++;
			if (valid) {
				previousHash = BlockChainUtils.toBytes(block);
				System.out.println("Book " + block.getIndex() + " hash is " + Base64.getEncoder().encodeToString(previousHash));
			}
		}
		blockIndex++;
		return block;
	}

}
