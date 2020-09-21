package es.tododev.blockchain.core;

import java.util.OptionalLong;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProofOfWorkImpl implements ProofOfWork<Long> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProofOfWorkImpl.class);
	private static final int CHUNK = 10000;

	public Long calculate(Block block) {
		String base = BlockChainUtils.base(block);
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
