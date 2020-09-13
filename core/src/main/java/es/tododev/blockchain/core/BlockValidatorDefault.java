package es.tododev.blockchain.core;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import es.tododev.blockchain.core.Block.Transaction;

public class BlockValidatorDefault implements BlockValidator {

	@Override
	public boolean isValid(Block block) throws BlockChainException {
		for (Transaction transaction : block.getTransactions()) {
			try {
				PublicKey publicKey = SignatureManager.publicKey(transaction.getSenderId());
				boolean verified = SignatureManager.verify(BlockChainUtils.toBytes(transaction), publicKey, transaction.getSenderSignaure());
				if (!verified) {
					throw BlockChainException.transactionInvalid(transaction);
				}
				// TODO verify the user has enough amount
			} catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
				throw BlockChainException.errorSecurityTransaction(transaction, e);
			}
		}
		byte[] sha256 = BlockChainUtils.sha256(BlockChainUtils.toBytes(block));
		return BlockChainUtils.isHashValid(sha256);
	}

}
