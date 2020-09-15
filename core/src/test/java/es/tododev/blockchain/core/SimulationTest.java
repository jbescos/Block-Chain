package es.tododev.blockchain.core;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import es.tododev.blockchain.core.Block.Transaction;

public class SimulationTest {

	private static final int USERS = 10;
	private static int pendingTransactions = 10000;
	private final List<KeyPair> users = TestUtils.generate(USERS);
	private final List<Miner> miners = new ArrayList<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

	@Test
	@Ignore
	public void run() {
		for (int i = 0; i < 10; i++) {
			Miner miner = new Miner();
			miners.add(miner);
			scheduler.scheduleWithFixedDelay(miner, 0, 10, TimeUnit.MILLISECONDS);
		}

		while (pendingTransactions > 0) {
			int generatedTransactions = TestUtils.random(0, 50);
			pendingTransactions = pendingTransactions - generatedTransactions;
			List<Transaction> transactions = TestUtils.createTransactions(users, generatedTransactions);
			int notifiedMiners = TestUtils.random(5, miners.size());
			for (int i = 0; i < notifiedMiners; i++) {
				miners.get(i).listen(transactions);
			}
		}
	}

	private class Miner implements Runnable {

		private final BlockValidator validator = new BlockValidatorDefault();
		private final BlockChainStorage storage = new BlockChainStorageDefault(6);
		private final BlockManager manager = new BlockManagerImpl(storage, validator);
		private final BlockingDeque<Transaction> queue = new LinkedBlockingDeque<>();

		@Override
		public void run() {
			Transaction transaction = null;
			int limit = 15;
			List<Transaction> transactions = new ArrayList<>(limit);
			while ((transaction = queue.poll()) != null || transactions.size() < limit) {
				transactions.add(transaction);
			}
			Block block = TestUtils.createBlock(manager.previousHash(), transactions);
			try {
				manager.add(block);
				// TODO
			} catch (BlockChainException e) {
				
			}
		}

		public void listen(List<Transaction> transactions) {
			queue.addAll(transactions);
		}

	}

}
