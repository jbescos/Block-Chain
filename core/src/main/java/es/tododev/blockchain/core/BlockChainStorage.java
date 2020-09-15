package es.tododev.blockchain.core;

import java.util.List;

public interface BlockChainStorage {
	
	byte[] add(Block block) throws BlockChainException;
	
	List<Block> blockChain();
	
}
