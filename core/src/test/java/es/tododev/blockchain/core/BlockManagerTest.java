package es.tododev.blockchain.core;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
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
		byte[] previousHash = new byte[0];
		Block b1 = createBlock(previousHash);
		manager.add(b1);
		Block b2 = createBlock(BlockChainUtils.toBytes(b1));
		manager.add(b2);
		Block b3 = createBlock(BlockChainUtils.toBytes(b2));
		manager.add(b3);
		Block b4 = createBlock(BlockChainUtils.toBytes(b3));
		manager.add(b4);
		assertEquals(Arrays.asList(b1, b2, b3, b4), storage.blockChain());
	}
	
	@Test
	public void multipleBlocks() throws BlockChainException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		BlockChainStorage storage = new BlockChainStorageDefault();
		BlockManager manager = new BlockManagerImpl(storage, validator);
		byte[] previousHash = new byte[0];
		Block b1 = createBlock(previousHash);
		manager.add(b1);
		Block b2 = createBlock(BlockChainUtils.toBytes(b1));
		manager.add(b2);
		Block fork1 = createBlock(BlockChainUtils.toBytes(b1));
		manager.add(fork1);
		Block b3 = createBlock(BlockChainUtils.toBytes(b2));
		manager.add(b3);
		Block b4 = createBlock(BlockChainUtils.toBytes(b3));
		manager.add(b4);
		Block b5 = createBlock(BlockChainUtils.toBytes(b4));
		manager.add(b5);
		Block b6 = createBlock(BlockChainUtils.toBytes(b5));
		manager.add(b6);
		Block b7 = createBlock(BlockChainUtils.toBytes(b6));
		manager.add(b7);
		Block b8 = createBlock(BlockChainUtils.toBytes(b7));
		manager.add(b8);
		Block b9 = createBlock(BlockChainUtils.toBytes(b8));
		manager.add(b9);
		assertEquals(Arrays.asList(b1, b2, b3, b4, b5, b6, b7, b8, b9), storage.blockChain());
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
	
	private Block createBlock(byte[] previousHash) throws BlockChainException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		List<Transaction> transactions = new ArrayList<>(TRANSACTIONS);
		for (int i = 0; i < TRANSACTIONS; i++) {
			transactions.add(createTransaction());
		}
		Block block = new Block(transactions, previousHash);
		long proofOfWork = new MinerTask(block).calculateProofOfWork();
		System.out.println("Proof of work is: " + proofOfWork);
		block.setProofOfWork(proofOfWork);
		return block;
	}

}
