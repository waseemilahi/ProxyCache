/***************************************************************
 *                                                              *
 *                            PROPRIETARY                       *
 *                                                              *
 *         THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE          *
 *         AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN         *
 *            ACCORDANCE WITH APPLICABLE AGREEMENTS.            *
 *                                                              *
 *                Copyright (c) 2015 - 2016 Waseem ilahi        *
 *              Unpublished & Not for Publication               *
 *                     All Rights Reserved                      *
 *                                                              *
 *       The copyright notice above does not evidence any       *
 *      actual or intended publication of such source code.     *
 *                                                              *
 *                   Author: Waseem Ilahi                       *
 *                                                              *
 ***************************************************************/
package tremorvideo.utils.nbqueue;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The Class ConcurrentQueue.
 *
 * @author Waseem Ilahi
 * @param <T> the generic type
 */
public class ConcurrentQueue<T> implements Queue<T> {

	/** The ref tail. */
	private AtomicReference<Node<T>> refHead, refTail;

	/**
	 * Instantiates a new concurrent queue.
	 */
	public ConcurrentQueue() {

		Node<T> dummy = new Node<T>(null);

		refHead = new AtomicReference<Node<T>>(dummy);
		refTail = new AtomicReference<Node<T>>(dummy);

	}

	/**
	 * Puts an object at the end of the queue.
	 *
	 * @param value the value
	 */	
	@Override
	public void enQueue( T value ) {
		// null objects are not allowed
		if (value == null) throw new NullPointerException();

		Node<T> newNode = new Node<T>(value);

		Node<T> tail, next;

		do {

			tail = refTail.get();
			next = tail.next;

		} while( next != null );


		Node<T> prevTail = refTail.getAndSet(newNode);
		prevTail.next = newNode;
	}

	/**
	 * Puts an object at the end of the queue.
	 *
	 * @param value the value
	 */
	@Override
	public void add(T value) {
		enQueue( value );
	}


	/**
	 * Gets an object from the beginning of the queue. The object is removed
	 * from the queue. If the queue is empty, throws UnderflowException
	 *
	 * @return the t
	 * @throws EmptyQueueException the empty queue exception
	 */
	@Override
	public T deQueue() throws EmptyQueueException {

		Node<T> head, next;

		// move head node to the next node using atomic semantics
		// as long as next node is not null
		do {
			head = refHead.get();
			next = head.next;

			// empty queue
			if( next == null ) {
				throw new EmptyQueueException("Empty Queue");
			}

			// try until the whole loop executes pseudo-atomically
			// (i.e. unaffected by modifications done by other threads)
		} while( !refHead.compareAndSet(head, next));

		T value = next.value;

		// release the value pointed to by head, keeping the head node dummy
		next.value = null;

		return value;

	}


	/**
	 * Gets an object from the beginning of the queue. The object is removed
	 * from the queue. If the queue is empty, return null
	 *
	 * @return the t
	 */
	@Override
	public T poll() {
		Node<T> head, next;

		// move head node to the next node using atomic semantics
		// as long as next node is not null
		do {
			head = refHead.get();
			next = head.next;

			// empty queue
			if( next == null ) {
				return null;
			}

			// try until the whole loop executes pseudo-atomically
			// (i.e. unaffected by modifications done by other threads)
		} while( !refHead.compareAndSet(head, next));

		T value = next.value;

		// release the value pointed to by head, keeping the head node dummy
		next.value = null;

		return value;
	}

	/* (non-Javadoc)
	 * @see babelfish.utils.FastQueue.Queue#getFront()
	 */
	@Override
	public T getFront() throws EmptyQueueException {
		Node<T> head, next;

		// move head node to the next node using atomic semantics
		// as long as next node is not null
		do {
			head = refHead.get();
			next = head.next;

			// empty queue
			if( next == null ) {
				throw new EmptyQueueException("Empty Queue");
			}

			// try until the whole loop executes pseudo-atomically
			// (i.e. unaffected by modifications done by other threads)
		} while( !refHead.compareAndSet(head, head));

		return next.value;
	}

	/* (non-Javadoc)
	 * @see babelfish.utils.FastQueue.Queue#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return refHead.get().next == null;
	}

	/* (non-Javadoc)
	 * @see babelfish.utils.FastQueue.Queue#clear()
	 */
	@Override
	public void clear() {

		try {
			while( !isEmpty()) {
				deQueue();
			}
		} catch (EmptyQueueException e) {
			System.out.println("Error removing item from the queue");
		}

	}

	/* (non-Javadoc)
	 * @see babelfish.utils.FastQueue.Queue#print()
	 */
	@Override
	public void print() {

		if( isEmpty()) {
			System.out.println("Queue is empty");
			return;
		}

		System.out.print("Queue: ");

		Node<T> curr = refHead.get().next;

		while( curr != null ) {

			System.out.print(curr.value + " ");
			curr = curr.next;
		}

		System.out.println();
	}


	/**
	 * The Class Node.
	 *
	 * @param <T> the generic type
	 */
	@SuppressWarnings("hiding")
	private class Node<T> {

		/** The value. */
		private T value;
		
		/** The next. */
		private volatile Node<T> next;

		/**
		 * Instantiates a new node.
		 *
		 * @param value the value
		 */
		Node(T value) {
			this.value = value;
		}
	}
}