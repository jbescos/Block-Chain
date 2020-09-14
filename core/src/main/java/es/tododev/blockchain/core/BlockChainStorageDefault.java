package es.tododev.blockchain.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlockChainStorageDefault implements BlockChainStorage {

	private static final Node<Block> INITIAL = new Node<>(null, new Block(Collections.emptyList(), new byte[0]), 0L);
	public static final byte[] INITIAL_HASH;
	// There could be sometimes more than one block on the top
	private List<Node<Block>> top = new LinkedList<>();
	private final long trustedSize;
	
	static {
		INITIAL_HASH = BlockChainUtils.toBytes(INITIAL.getContent());
	}

	public BlockChainStorageDefault(long trustedSize) {
		this.trustedSize = trustedSize;
		top.add(INITIAL);
	}

	@Override
	public boolean add(Block block) throws BlockChainException {
		Node<Block> newForTop = null;
		Node<Block> previous = null;
		boolean added = false;
		for (Node<Block> candidate : top) {
			previous = find(b -> Arrays.equals(BlockChainUtils.toBytes(b), block.getPreviousHash()), candidate, b -> {});
			if (previous != null) {
				newForTop = new Node<>(previous, block, previous.getIndex() + 1);
				break;
			}
		}
		if (newForTop != null) {
			// Only need replace first, but that method doesn't exist
			Collections.replaceAll(top, previous, newForTop);
			added = true;
		}
		removeForks();
		return added;
	}

	@Override
	public List<Block> blockChain() {
		Collections.sort(top, (n1, n2) -> Long.compare(n1.getIndex(), n2.getIndex()));
		Node<Block> highest = top.get(0);
		List<Block> blockChain = new ArrayList<>(((int)highest.getIndex() + 1));
		while (highest != null) {
			blockChain.add(highest.getContent());
			highest = highest.getPrevious();
		}
		blockChain.remove(INITIAL.getContent());
		Collections.reverse(blockChain);
		return blockChain;
	}

	private Node<Block> find(Predicate<Block> predicate, Node<Block> node, Consumer<Block> consumer) {
		Node<Block> current = node;
		while (current != null && !predicate.test(current.getContent())) {
			current = current.getPrevious();
		}
		return current;
	}

	private void removeForks() {
		if (top.size() > 1) {
			Collections.sort(top, (n1, n2) -> Long.compare(n1.getIndex(), n2.getIndex()));
			List<Node<Block>> updated = new LinkedList<>();
			Node<Block> highest = top.get(0);
			updated.add(highest);
			for (int i = 1; i < top.size(); i++) {
				Node<Block> candidate = top.get(i);
				if (highest.getIndex() - candidate.getIndex() >= trustedSize) {
					break;
				} else {
					updated.add(candidate);
				}
			}
			top = updated;
		}
	}

}
