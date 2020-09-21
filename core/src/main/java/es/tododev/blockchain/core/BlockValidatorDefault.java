package es.tododev.blockchain.core;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.tododev.blockchain.core.Block.Transaction;

public class BlockValidatorDefault implements BlockValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlockValidatorDefault.class);

	@Override
	public void validate(BlockChainStorage storage, Block block) throws BlockChainException {
		byte[] sha256 = BlockChainUtils.sha256(block);
		boolean blockValid = BlockChainUtils.isHashValid(sha256);
		if (!blockValid) {
			throw BlockChainException.errorBlockInvalid(sha256);
		}
		for (Transaction transaction : block.getTransactions()) {
			try {
				PublicKey publicKey = SignatureManager.publicKey(transaction.getFrom());
				boolean verified = SignatureManager.verify(transaction.content(), publicKey, transaction.getSignaure());
				if (!verified) {
					throw BlockChainException.transactionInvalid(transaction);
				}
				if (storage.exists(transaction)) {
					throw BlockChainException.transactionDuplicated(transaction);
				}
				// TODO verify the user has enough amount
			} catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
				throw BlockChainException.errorSecurityTransaction(transaction, e);
			}
		}
	}

}
