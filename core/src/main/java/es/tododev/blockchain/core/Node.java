package es.tododev.blockchain.core;

public class Node<T> {

	private final Node<T> previous;
	private final T content;
	private final long index;
	
	public Node(Node<T> previous, T content, long index) {
		this.previous = previous;
		this.content = content;
		this.index = index;
	}

	public Node<T> getPrevious() {
		return previous;
	}

	public T getContent() {
		return content;
	}

	public long getIndex() {
		return index;
	}
	
}
