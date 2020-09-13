package es.tododev.blockchain.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

public class BlockChainStorageDefault implements BlockChainStorage {

	private static final int TRUSTED_SIZE = 6;
	private final Map<Long, List<Block>> blockChain = new HashMap<>();

	private long purgeTree(Block block) throws BlockChainException {
		OptionalLong highest = blockChain.keySet().stream().mapToLong(id -> id).max();
		if (highest.isPresent()) {
			long max = highest.getAsLong();
			int currentTrustedSize = 0;
			Block trustedBlock = null;
			for (long i = max - 1; i >= max - TRUSTED_SIZE; i--) {
				List<Block> currentIndex = blockChain.get(i);
				if (currentIndex != null && currentIndex.size() == 1) {
					currentTrustedSize++;
					trustedBlock = currentIndex.get(0);
				} else {
					break;
				}
			}
			if (currentTrustedSize == TRUSTED_SIZE) {
				long trustedIndex = trustedBlock.getIndex();
				for (long i = trustedIndex - 1; i >= 0; i--) {
					List<Block> currentIndex = blockChain.get(i);
					if (currentIndex == null || currentIndex.size() == 1) {
						break;
					} else {
						trustedIndex = i;
						byte[] previousHash = trustedBlock.getPreviousHash();
						for (Block candidate : currentIndex) {
							if (Arrays.equals(BlockChainUtils.toBytes(candidate), previousHash)) {
								trustedBlock = candidate;
								blockChain.put(i, Arrays.asList(trustedBlock));
								break;
							}
						}
					}
				}
				return trustedIndex;
			}
		}
		return -1;
	}

	@Override
	public boolean add(Block block) throws BlockChainException {
		if (blockChain.isEmpty()) {
			List<Block> blocksById = new LinkedList<>();
			blockChain.put(block.getIndex(), blocksById);
			blocksById.add(block);
			return true;
		}
		List<Block> previousCandidates = blockChain.get(block.getIndex() - 1);
		System.out.println("Candidates " + previousCandidates);
		if (previousCandidates != null) {
			for (Block previous : previousCandidates) {
				if (Arrays.equals(BlockChainUtils.toBytes(previous), block.getPreviousHash())) {
					List<Block> blocksById = blockChain.get(block.getIndex());
					if (blocksById == null) {
						blocksById = new LinkedList<>();
						blockChain.put(block.getIndex(), blocksById);
					}
					blocksById.add(block);
					long validatedIndex = purgeTree(block);
					if ( validatedIndex != -1) {
						persistBlockChain(validatedIndex);
					}
					return true;
				}
			}
		}
		return false;
	}

	private void persistBlockChain(long validatedIndex) {

	}

}
