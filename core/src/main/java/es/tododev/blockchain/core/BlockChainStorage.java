package es.tododev.blockchain.core;

public interface BlockChainStorage {
	
	boolean add(Block block) throws BlockChainException;
	
}
