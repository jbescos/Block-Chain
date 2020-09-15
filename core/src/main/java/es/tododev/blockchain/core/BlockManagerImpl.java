package es.tododev.blockchain.core;

public class BlockManagerImpl implements BlockManager {

	private final BlockChainStorage storage;
	private final BlockValidator validator;
	private byte[] previousHash = new byte[0];

	public BlockManagerImpl(BlockChainStorage storage, BlockValidator validator) {
		this.storage = storage;
		this.validator = validator;
	}

	@Override
	public void add(Block block) throws BlockChainException {
		if (validator.isValid(block)) {
			previousHash = storage.add(block);
			if (previousHash == null) {
				throw BlockChainException.blockNotFound(block.getPreviousHash());
			}
		} else {
			System.out.println("Block is not valid");
		}
	}

	@Override
	public byte[] previousHash() throws BlockChainException {
		return previousHash;
	}

}
