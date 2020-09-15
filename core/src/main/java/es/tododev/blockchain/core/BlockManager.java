package es.tododev.blockchain.core;

public interface BlockManager {
	
	void add(Block block) throws BlockChainException;
	
	byte[] previousHash() throws BlockChainException;
	
}
