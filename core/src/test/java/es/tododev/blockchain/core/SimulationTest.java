package es.tododev.blockchain.core;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.tododev.blockchain.core.Block.Transaction;

public class SimulationTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimulationTest.class);
	private static final int MINERS = 5;
	private static final int USERS = 10;
	private static final int TRANSACTIONS = 1000;
	private static final int GENERATED_TRANSACTIONS = 100;

	@Test
	public void simulate() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(MINERS);
		List<KeyPair> users = TestUtils.generate(USERS);
		List<MinerManager> miners = createMiners(MINERS);
		List<MiningJob> jobs = miners.stream().map(miner -> new MiningJob(miner, latch)).peek(job -> job.start()).collect(Collectors.toList());
		int pendingTransactions = TRANSACTIONS;
		while (pendingTransactions > 0) {
			int transactionsToEmit = pendingTransactions < GENERATED_TRANSACTIONS ? pendingTransactions : TestUtils.random(0, GENERATED_TRANSACTIONS);
			pendingTransactions = pendingTransactions - transactionsToEmit;
			List<Transaction> transactions = TestUtils.createTransactions(users, transactionsToEmit);
			transactions.parallelStream().forEach(tx -> miners.stream().forEach(miner -> miner.pick(tx)));
			LOGGER.debug("Sending " + transactionsToEmit + " transactions. Pending " + pendingTransactions);
			Thread.sleep(10000);
		}
		LOGGER.debug("Closing miners");
		jobs.stream().forEach(job -> job.close());
		latch.await();
	}

	public List<MinerManager> createMiners(int amount) {
		List<MinerManager> miners = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			MinerManager miner = new MinerManagerImpl(TestUtils.generate().getPublic(), new BlockChainStorageDefault(6), new BlockValidatorDefault(), new ProofOfWorkImpl());
			miners.add(miner);
		}
		miners.stream().forEach(m1 -> miners.stream().filter(m2 -> m1 != m2).forEach(m2 -> m1.addListener(m2)));
		return miners;
	}
}
