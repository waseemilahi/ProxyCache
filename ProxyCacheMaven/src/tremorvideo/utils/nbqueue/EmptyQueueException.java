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
 * The Class EmptyQueueException.
 *
 * @author Waseem Ilahi
 */

public class EmptyQueueException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -813438986021386368L;

	/**
	 * Instantiates a new empty queue exception.
	 *
	 * @param message the message
	 */
	public EmptyQueueException( String message ) {
        super( message );
    }
}
