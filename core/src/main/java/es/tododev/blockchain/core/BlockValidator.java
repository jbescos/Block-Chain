package es.tododev.blockchain.core;

public interface BlockValidator {

	void validate(BlockChainStorage blockChainStorage, Block block) throws BlockChainException;
	
}
