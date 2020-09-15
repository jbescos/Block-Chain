package es.tododev.blockchain.core;

import java.util.OptionalLong;
import java.util.stream.LongStream;

public class MinerTask {

	private static final int CHUNK = 10000;
	private final String base;
	
	public MinerTask(Block block) {
		this.base = BlockChainUtils.base(block);
	}
	
	public long calculateProofOfWork() {
		long start = 0;
		long end = start + CHUNK;
		while (true) {
			OptionalLong optional = LongStream.rangeClosed(start, end).parallel().filter(l -> BlockChainUtils.test(base, l)).findAny();
			if (optional.isPresent()) {
				System.out.println("ProofOfWork = " + optional.getAsLong());
				return optional.getAsLong();
			}
			start = end + 1;
			end = start + CHUNK;
		}
		
	}
	
}
