package es.tododev.blockchain.core;

public class BlockManagerImpl implements BlockManager {

	private final BlockChainStorage storage;
	private final BlockValidator validator;

	public BlockManagerImpl(BlockChainStorage storage, BlockValidator validator) {
		this.storage = storage;
		this.validator = validator;
	}

	@Override
	public synchronized void add(Block block) throws BlockChainException {
		if (validator.isValid(block)) {
			boolean added = storage.add(block);
			if (!added) {
				throw BlockChainException.blockNotFound(block.getPreviousHash());
			}
		}
	}

}
