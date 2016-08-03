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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import tremorvideo.utils.nbqueue.ConcurrentQueue;

/**
 * The Class ReceiverServer.
 *
 * @author Waseem Ilahi
 */
class ReceiverServer {

	/** The broken socket queue. */
	static tremorvideo.utils.nbqueue.Queue<Object> brokenSocketQueue;

	/** The transfer queue. */
	static tremorvideo.utils.nbqueue.Queue<TransferObject> transferQueue;

	/** The cache map. */
	static Map<String, ResponseObject> cacheMap;

	/** The receiver thread. */
	ReceiverThread receiverThread;

	/** The process server. */
	ProcessServer processServer;

	/**
	 * Instantiates a new receiver server.
	 *
	 * @param port the port
	 * @param maxCacheSize the max cache size
	 */
	public ReceiverServer(int port, int maxCacheSize){

		boolean running = true;
		boolean listening = true;
		ServerSocket serverSocket;
		boolean resetSocket = false;

		brokenSocketQueue = new ConcurrentQueue<>();
		transferQueue = new ConcurrentQueue<>();
		
		cacheMap = Collections.synchronizedMap(new CacheHashMap(maxCacheSize));

		processServer = new ProcessServer(brokenSocketQueue, transferQueue, cacheMap, Integer.toString(port));

		while (listening) {
			/**
			 * infinite loop to keep the program listening on the port
			 */
			try {
				/**
				 * Attempt to 1. initialized a new ServerSocket with port number
				 * for the current thread 2. Print out port number of the
				 * successfully started thread
				 */
				serverSocket = new ServerSocket(port);
				writeLog( " ReceiverServer created socket on port: " + port);

			} catch (Exception e) {
				/**
				 * When exception happens 1. print out port number that failed to
				 * start
				 */
				writeLog(" ReceiverServer could not create socket on port: " + port);
				System.out.println(getDateStr() + "ReceiverServer could not create socket on port: " + port);
				writeLog( e.getMessage());
				System.out.println(getDateStr() + e.getMessage());

				try {
					/**
					 * Attempt to 1. Sleep for 10 seconds and try again.
					 */
					Thread.sleep(10 * 1000);
				} catch (Exception ex) {
					/**
					 * When interrupt happens 
					 * 
					 * 1. print out the exception
					 * message
					 */
					writeLog( ex.getMessage());
					System.out.println(getDateStr() + ex.getMessage());
				}
				continue;
			}

			try {

				while (running) {
					/**
					 * While the program is running
					 */

					while (!brokenSocketQueue.isEmpty()) {
						/**
						 * While brokenSocketQueue is not empty 
						 * 
						 * 1. remove from brokenSocketQueue 
						 * 2. set resetSocket flag to true
						 */
						brokenSocketQueue.poll();
						resetSocket = true;
					}
					if (resetSocket) {
						/**
						 * if need to reset 
						 * 1. break while running 
						 */
						resetSocket = false;
						break;
					}

					//counter++;
					/**
					 * initialized  the Thread with 
					 * 1. serverSocket.accept() 
					 * 2. brokenSocketQueue,
					 * 3. string representation of port
					 * 4. The transfer Queue.
					 */
					receiverThread = new ReceiverThread(serverSocket.accept(),
							brokenSocketQueue, Integer.toString(port), transferQueue);
					receiverThread.start();

				}
			} catch (Exception e) {
				/**
				 * When encounter exception
				 * 1. print out fail to start port and the exception message 
				 */
				System.out.println(getDateStr()+ " ReceiverServer could not accept connection on port: " + port );
				System.out.println(getDateStr()+ e.getMessage());
				writeLog( " ReceiverServer could not accept connection on port: " + port );
				writeLog( e.getMessage());
			}
			try {
				/**
				 * Attempt to 
				 * 1. close the unusable socket 
				 * 2. Print out the port number 
				 */
				serverSocket.close();
				writeLog(" ReceiverServer closed socket on port: " + port);
			} catch (Exception e) {
				/**
				 * When encounter exception
				 * 1. print out port number that was failed to close
				 */
				System.out.println(getDateStr() + " ReceiverServer could not close socket on port: " + port);
				writeLog(" ReceiverServer could not close socket on port: " + port);
			}
		}

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new ReceiverServer(8081, 1000000);
	}

	/**
	 * Write log.
	 *
	 * @param _Log the log
	 */
	private static void writeLog(String _Log) {

		try{
			// get log file
			String _LogFileName = getCurrentLogFileName();

			// get log file path
			Path _LogFilePath = Paths.get(System.getProperty("user.dir"), "logs", _LogFileName);

			// load file
			File _LogFile = new File(_LogFilePath.toString());
			// if file is not there, create it
			if (!(_LogFile.exists())) {
				_LogFile.createNewFile();
			}

			// init writer
			FileWriter _LogFileWriter = new FileWriter(
					_LogFile.getAbsolutePath(),true);
			BufferedWriter _BufferedLogFileWriter = new BufferedWriter(
					_LogFileWriter);

			// write log
			_BufferedLogFileWriter.write(getDateStr() + " " + _Log + "\n");

			// close writers
			_BufferedLogFileWriter.close();
			_LogFileWriter.close();

		}
		catch(IOException e){
			System.out.println(getDateStr() + " Error while writing to the file!");		
		}
	}

	/**
	 * Gets the current log file name.
	 *
	 * @return the current log file name
	 */
	private static String getCurrentLogFileName() {

		String hostName = new String();
		try{
			hostName = new String(InetAddress.getLocalHost().getHostName());
		}catch(UnknownHostException ue){
			hostName = "proxy-cache-server";
		}
		String _fileName = hostName + "-proxycache-" + new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date()) + ".log";
		return _fileName;
	}

	/**
	 * Gets the date str.
	 *
	 * @return the date str
	 */
	private static String getDateStr() {
		/**
		 * Get the date-time for logging purposes. (1)
		 * <p>
		 * 
		 * @return dateString String Date String of today's string (3)
		 */
		String dateString = new String("");
		Date idate = new Date();
		SimpleDateFormat sdfDestination = new SimpleDateFormat(
				"yyyy-MM-dd.hh:mm:ss:SSS");
		dateString = sdfDestination.format(idate);
		return dateString;
	}

}
