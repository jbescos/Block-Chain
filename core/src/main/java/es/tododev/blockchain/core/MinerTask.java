package es.tododev.blockchain.core;

import java.util.OptionalLong;
import java.util.stream.LongStream;

import es.tododev.blockchain.core.Block.Transaction;

public class MinerTask {

	private static final int CHUNK = 10000;
	private final String base;
	
	public MinerTask(Block block) {
		StringBuilder builder = new StringBuilder();
		builder.append(block.getPreviousHash());
		for (Transaction transaction : block.getTransactions()) {
			builder.append(transaction.getSignaure());
		}
		this.base = builder.toString();
	}
	
	public long calculateProofOfWork() {
		long start = 0;
		long end = start + CHUNK;
		while (true) {
			OptionalLong optional = LongStream.rangeClosed(start, end).parallel().filter(l -> BlockChainUtils.test(base, l)).findAny();
			if (optional.isPresent()) {
				return optional.getAsLong();
			}
			start = end + 1;
			end = start + CHUNK;
		}
		
	}
	
}
