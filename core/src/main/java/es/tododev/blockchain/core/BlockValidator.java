package es.tododev.blockchain.core;

public interface BlockValidator {

	boolean isValid(Block block) throws BlockChainException;
	
}
