package es.tododev.blockchain.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import es.tododev.blockchain.core.Block.Transaction;

public class MinerManagerTest {

	private static final BlockValidator validator = new BlockValidatorDefault();
	private static final int USERS = 10;
	private static final int TRANSACTIONS = 100;
	private static final List<KeyPair> users = TestUtils.generate(USERS);

	@Test
	public void normal() throws BlockChainException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		BlockChainStorage storage = new BlockChainStorageDefault(6);
		MinerManager manager = new MinerManagerImpl(null, storage, validator, new ProofOfWorkImpl());
		Block b1 = TestUtils.createBlock(manager.previousHash(), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b1);
		Block b2 = TestUtils.createBlock(manager.previousHash(), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b2);
		Block b3 = TestUtils.createBlock(manager.previousHash(), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b3);
		Block b4 = TestUtils.createBlock(manager.previousHash(), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b4);
		assertEquals(Arrays.asList(b1, b2, b3, b4), storage.blockChain());
	}
	
	@Test
	public void multipleBlocks() throws BlockChainException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		BlockChainStorage storage = new BlockChainStorageDefault(6);
		MinerManager manager = new MinerManagerImpl(null, storage, validator, new ProofOfWorkImpl());
		byte[] previousHash = new byte[0];
		Block b1 = TestUtils.createBlock(previousHash, TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b1);
		Block b2 = TestUtils.createBlock(BlockChainUtils.sha256(b1), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b2);
		Block fork1 = TestUtils.createBlock(BlockChainUtils.sha256(b1), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(fork1);
		Block b3 = TestUtils.createBlock(BlockChainUtils.sha256(b2), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b3);
		Block b4 = TestUtils.createBlock(BlockChainUtils.sha256(b3), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b4);
		Block b5 = TestUtils.createBlock(BlockChainUtils.sha256(b4), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b5);
		Block b6 = TestUtils.createBlock(BlockChainUtils.sha256(b5), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b6);
		Block b7 = TestUtils.createBlock(BlockChainUtils.sha256(b6), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b7);
		Block b8 = TestUtils.createBlock(BlockChainUtils.sha256(b7), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b8);
		Block b9 = TestUtils.createBlock(BlockChainUtils.sha256(b8), TestUtils.createTransactions(users, TRANSACTIONS));
		manager.add(b9);
		assertEquals(Arrays.asList(b1, b2, b3, b4, b5, b6, b7, b8, b9), storage.blockChain());
	}
	
	@Test
	public void transaction() throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Transaction transaction = TestUtils.createTransaction(users);
		PublicKey publicKey = SignatureManager.publicKey(transaction.getFrom());
		boolean verified = SignatureManager.verify(transaction.content(), publicKey, transaction.getSignaure());
		assertTrue(verified);
	}
	
	@Test
	public void block() {
		BlockChainStorage storage = new BlockChainStorageDefault(6);
		Block initial = TestUtils.createBlock(new byte[0], Arrays.asList(TestUtils.createTransaction(users)));
		Block next = TestUtils.createBlock(BlockChainUtils.sha256(initial), Arrays.asList(TestUtils.createTransaction(users)));
		long proofOfWork = new ProofOfWorkImpl().calculate(next);
		next.setProofOfWork(proofOfWork);
		new BlockValidatorDefault().validate(storage, next);
	}
	

}
