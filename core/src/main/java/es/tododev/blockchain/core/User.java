package es.tododev.blockchain.core;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import es.tododev.blockchain.core.Block.Transaction;

public class User {

	private final PrivateKey privateKey;
	private final PublicKey publicKey;
	private final BigDecimal minerFee;
	private final List<MinerManager> miners = new ArrayList<>();

	public User(PrivateKey privateKey, PublicKey publicKey, BigDecimal minerFee) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.minerFee = minerFee;
	}
	
	public void sendTransaction(BigDecimal amount, String type, byte[] to) throws BlockChainException {
		Transaction transaction = new Transaction(publicKey.getEncoded(), to, amount, type);
		try {
			transaction.setSignaure(SignatureManager.sign(transaction.content(), privateKey));
			String id = UUID.randomUUID().toString();
			for (MinerManager miner : miners) {
				Transaction minerFee = new Transaction(id, publicKey.getEncoded(), miner.minerId().getEncoded(), this.minerFee, type);
				minerFee.setSignaure(SignatureManager.sign(minerFee.content(), privateKey));
				transaction = transaction.clone();
				transaction.setMinerFee(minerFee);
				miner.pick(transaction);
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			throw BlockChainException.transactionCannotSign(transaction, e);
		}
	}
	
}
