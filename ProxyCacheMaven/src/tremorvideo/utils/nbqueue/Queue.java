      /***************************************************************
      *                                                              *
      *                            PROPRIETARY                       *
      *                                                              *
      *         THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE          *
      *         AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN         *
      *            ACCORDANCE WITH APPLICABLE AGREEMENTS.            *
      *                                                              *
      *                Copyright (c) 2015 - 2016 Waseem Ilahi        *
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

/**
 * The Interface Queue.
 *
 * @author Waseem Ilahi
 * @param <T> the generic type
 */
public interface Queue<T> {
	
	/**
	 * En queue.
	 *
	 * @param data the data
	 */
	// ******************PUBLIC OPERATIONS*********************
	void enQueue( T data );					//  Insert item into a queue
	
	/**
	 * Adds the.
	 *
	 * @param data the data
	 */
	void add( T data );						//  Insert item into a queue
	
	/**
	 * Gets the front.
	 *
	 * @return the front
	 * @throws EmptyQueueException the empty queue exception
	 */
	T getFront() throws EmptyQueueException;	//	Return least recently inserted item
	
	/**
	 * De queue.
	 *
	 * @return the t
	 * @throws EmptyQueueException the empty queue exception
	 */
	T deQueue()  throws EmptyQueueException; //	Return and remove least recent item
	
	/**
	 * Poll.
	 *
	 * @return the t
	 */
	T poll(); 								//	Return and remove least recent item
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	boolean isEmpty();   					//	Return true if empty; else false
	
	/**
	 * Clear.
	 */
	void clear();	    					//	Remove all items
	
	/**
	 * Prints the.
	 */
	void print();
}
