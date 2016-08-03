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
package tremorvideo.proxy.cache;

import java.util.Map;

/**
 * The Class ProcessServer.
 *
 * @author Waseem Ilahi
 */
public class ProcessServer extends Thread {

	/** The broken socket queue. */
	tremorvideo.utils.nbqueue.Queue<Object> brokenSocketQueue;

	/** The transfer queue. */
	tremorvideo.utils.nbqueue.Queue<TransferObject> transferQueue;

	/** The cache map. */
	Map<String, ResponseObject> cacheMap;

	/** The port. */
	String port;

	/** The process thread. */
	ProcessThread processThread;

	/**
	 * Instantiates a new process server.
	 *
	 * @param bsq the bsq
	 * @param tq the tq
	 * @param cm the cm
	 * @param portString the port string
	 */
	public ProcessServer(tremorvideo.utils.nbqueue.Queue<Object> bsq, tremorvideo.utils.nbqueue.Queue<TransferObject> tq,
			Map<String, ResponseObject> cm, String portString){

		brokenSocketQueue = bsq;
		transferQueue = tq;
		cacheMap = cm;
		port = portString;

		start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){

		boolean active = true;
		TransferObject transferObject = null;

		while(active) {

			while( (transferObject = transferQueue.poll()) != null) { 

				//Fork here	
				processThread = new ProcessThread(brokenSocketQueue, transferObject, cacheMap, port);
				processThread.start();							

			}//end of polling		  
		}//end of while running
	}
}
