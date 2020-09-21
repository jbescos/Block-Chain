package es.tododev.blockchain.core;

import java.security.PublicKey;

import es.tododev.blockchain.core.Block.Transaction;

public interface MinerManager {
	
	void add(Block block) throws BlockChainException;
	
	byte[] previousHash() throws BlockChainException;
	
	void addListener(MinerManager listener) throws BlockChainException;
	
	void offer(Block block);
	
	PublicKey minerId();
	
	void pick(Transaction transaction) throws BlockChainException;
	
	void mine() throws BlockChainException;

	BlockChainStorage blockChainStorage();
}
