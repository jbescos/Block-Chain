package es.tododev.blockchain.core;

public interface ProofOfWork<T> {
	
	T calculate(Block block);

}
