package es.tododev.blockchain.core;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.tododev.blockchain.core.Block.Transaction;

public class MinerManagerImpl implements MinerManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(MinerManagerImpl.class);
	private final List<MinerManager> listeners = Collections.synchronizedList(new ArrayList<>());
	private final BlockingQueue<Transaction> pending = new LinkedBlockingQueue<>();
	private final BlockingQueue<Block> notifiedBlocks = new LinkedBlockingQueue<>();
	private final BlockChainStorage storage;
	private final BlockValidator validator;
	private final PublicKey minerId;
	private final ProofOfWork<Long> proofOfWork;
	private volatile byte[] previousHash = new byte[0];

	public MinerManagerImpl(PublicKey minerId, BlockChainStorage storage, BlockValidator validator, ProofOfWork<Long> proofOfWork) {
		this.storage = storage;
		this.validator = validator;
		this.minerId = minerId;
		this.proofOfWork = proofOfWork;
	}

	@Override
	public void add(Block block) throws BlockChainException {
		validator.validate(storage, block);
		previousHash = storage.add(block);
		if (previousHash == null) {
			throw BlockChainException.blockNotFound(block.getPreviousHash());
		}
	}

	@Override
	public byte[] previousHash() throws BlockChainException {
		return previousHash;
	}

	@Override
	public void addListener(MinerManager listener) throws BlockChainException {
		listeners.add(listener);
	}

	@Override
	public PublicKey minerId() {
		return minerId;
	}

	@Override
	public void pick(Transaction transaction) throws BlockChainException {
		try {
			pending.put(transaction);
		} catch (InterruptedException e) {
			throw BlockChainException.errorInterrupted(e);
		}
	}

	@Override
	public void mine() throws BlockChainException {
		synchronizeOffered();
		Block block = null;
		try {
			List<Transaction> process = new ArrayList<>();
			pending.drainTo(process);
			if (!process.isEmpty()) {
				List<Transaction> uniqueTx = process.stream().filter(tx -> !storage.exists(tx)).collect(Collectors.toList());
				List<Transaction> minerFees = uniqueTx.stream().filter(tx -> tx.getMinerFee() != null).map(tx -> tx.getMinerFee()).collect(Collectors.toList());
				uniqueTx.addAll(minerFees);
				LOGGER.debug("Miner is processing " + uniqueTx.size() + " transactions");
				block = new Block(uniqueTx, previousHash);
				long result = proofOfWork.calculate(block);
				block.setProofOfWork(result);
				add(block);
				LOGGER.info("Miner added a valid block " + block);
			}
		} catch (BlockChainException e) {
			LOGGER.warn("Block was not added in the block chain, reason: ", e.getMessage());
		}
		if (block != null) {
			List<MinerManager> copy = new ArrayList<>(listeners);
			for (MinerManager listener : copy) {
				try {
					listener.offer(block);
				} catch (BlockChainException e) {
					LOGGER.warn("Listener didn't accept block, reason: " + e.getMessage());
				}
			}
		}
	}

	@Override
	public BlockChainStorage blockChainStorage() {
		return storage;
	}

	@Override
	public void offer(Block block) {
		notifiedBlocks.add(block);
	}

	private void synchronizeOffered() {
		List<Block> process = new ArrayList<>();
		notifiedBlocks.drainTo(process);
		if (!process.isEmpty()) {
			try {
				process.stream().forEach(block -> add(block));
			} catch (BlockChainException e) {
				LOGGER.warn("Offered block was not added in the block chain, reason: " + e.getMessage());
			}
		}
	}

}
