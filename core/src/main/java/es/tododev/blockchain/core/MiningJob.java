package es.tododev.blockchain.core;

import java.io.Closeable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiningJob implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MiningJob.class);
	private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
	private final MinerManager minerManager;
	private final CountDownLatch latch;
	
	public MiningJob(MinerManager minerManager, CountDownLatch latch) {
		this.minerManager = minerManager;
		this.latch = latch;
	}

	@Override
	public void close() {
		try {
			scheduled.shutdown();
			scheduled.awaitTermination(10, TimeUnit.SECONDS);
			latch.countDown();
			LOGGER.debug("Terminated: " + minerManager.blockChainStorage().blockChain());
		} catch (InterruptedException e) {
			LOGGER.error("Job interrupted", e);
		}
	}
	
	public void start() {
		this.scheduled.scheduleWithFixedDelay(() -> {
			minerManager.mine();
		}, 0, 1, TimeUnit.SECONDS);
	}

}
