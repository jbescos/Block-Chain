package es.tododev.blockchain.core;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalLong;

public class BlockChainStorageDefault implements BlockChainStorage {

	private static final int TRUSTED_SIZE = 6;
	private final Map<Long, List<Block>> blockChain = new HashMap<>();

	private long purgeTree() throws BlockChainException {
		OptionalLong highest = blockChain.keySet().stream().mapToLong(id -> id).max();
		if (highest.isPresent()) {
			long max = highest.getAsLong();
			int currentTrustedSize = 0;
			Block trustedBlock = null;
			long trustedIndex = 0;
			for (long i = max - 1; i >= max - TRUSTED_SIZE; i--) {
				List<Block> currentIndex = blockChain.get(i);
				if (currentIndex != null && currentIndex.size() == 1) {
					currentTrustedSize++;
					trustedBlock = currentIndex.get(0);
					trustedIndex = i;
				} else {
					break;
				}
			}
			if (currentTrustedSize == TRUSTED_SIZE) {
				for (long i = trustedIndex - 1;; i--) {
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
	
	private Entry<Long, Block> findPreviousBlock(byte[] previousHash) throws BlockChainException {
		List<Long> indexes = new ArrayList<>(blockChain.keySet());
		Collections.reverse(indexes);
		for (long index : indexes) {
			for (Block previous : blockChain.get(index)) {
				if (Arrays.equals(BlockChainUtils.toBytes(previous), previousHash)) {
					return new AbstractMap.SimpleEntry<>(index, previous);
				}
			}
		}
		return null;
	}

	@Override
	public boolean add(Block block) throws BlockChainException {
		if (blockChain.isEmpty()) {
			List<Block> blocksById = new LinkedList<>();
			blockChain.put(0L, blocksById);
			blocksById.add(block);
			return true;
		}
		Entry<Long, Block> entry = findPreviousBlock(block.getPreviousHash());
		if (entry != null) {
			long current = entry.getKey() + 1;
			List<Block> blocksById = blockChain.get(current);
			if (blocksById == null) {
				blocksById = new LinkedList<>();
				blockChain.put(current, blocksById);
			}
			blocksById.add(block);
			long validatedIndex = purgeTree();
			if ( validatedIndex != -1) {
				persistBlockChain(validatedIndex);
			}
			return true;
		}
		return false;
	}

	private void persistBlockChain(long validatedIndex) {
		// TODO
	}

	@Override
	public List<Block> blockChain() {
		List<Block> trustedChain = new LinkedList<>();
		for (List<Block> blocks : blockChain.values()) {
			if (blocks.size() == 1) {
				trustedChain.add(blocks.get(0));
			} else {
				break;
			}
		}
		return trustedChain;
	}

}
