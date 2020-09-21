package es.tododev.blockchain.core;

import java.util.List;

import es.tododev.blockchain.core.Block.Transaction;

public interface BlockChainStorage {
	
	byte[] add(Block block) throws BlockChainException;
	
	List<Block> blockChain();
	
	boolean exists(Transaction transaction);
	
}
