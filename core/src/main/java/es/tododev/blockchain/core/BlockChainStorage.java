package es.tododev.blockchain.core;

import java.util.List;

public interface BlockChainStorage {
	
	boolean add(Block block) throws BlockChainException;
	
	List<Block> blockChain();
	
}
