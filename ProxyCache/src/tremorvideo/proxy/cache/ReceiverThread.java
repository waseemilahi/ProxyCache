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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * The Class ReceiverThread.
 *
 * @author Waseem Ilahi
 */
public class ReceiverThread extends Thread {

	/** The broken socket queue. */
	tremorvideo.utils.nbqueue.Queue<Object> brokenSocketQueue;

	/** The transfer queue. */
	tremorvideo.utils.nbqueue.Queue<TransferObject> transferQueue;

	/** The port. */
	String port;

	/** The transfer object. */
	TransferObject transferObject;

	/** The is complete. */
	boolean isComplete = true;

	/** The Constant BUFFER_SIZE. */
	private static final int BUFFER_SIZE = 32768;

	/**
	 * Instantiates a new receiver thread.
	 *
	 * @param socket the socket
	 * @param bsq the bsq
	 * @param portString the port string
	 * @param tq the tq
	 */
	public ReceiverThread(Socket socket, tremorvideo.utils.nbqueue.Queue<Object> bsq,
			String portString, tremorvideo.utils.nbqueue.Queue<TransferObject> tq){

		brokenSocketQueue = bsq;
		transferQueue = tq;
		port = portString;
		transferObject = new TransferObject("",socket, "", new HashMap<String, String>());

	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){

		String url = new String();
		DataOutputStream out = null;
		BufferedReader in = null;

		try { 
			in = new BufferedReader(new InputStreamReader(transferObject.getSocket().getInputStream()));

			String inputLine = null;
			int index;
			///////////////////////////////////
			//begin get request from client

			String inMsg = new String();

			while ((inputLine = in.readLine()) != null) {
				try {
					StringTokenizer tok = new StringTokenizer(inputLine);
					tok.nextToken();
				} catch (Exception e) {
					break;
				}

				inMsg += inputLine + "\n";
			}

			//now get the body of the message
			//inMsg += getData(in);
			//end get request content from client

			//Get the url from the read string and add it to the transfer object and then to transfer queue to be processed
			if(inMsg.contains("url=") && inMsg.contains("HTTP") && !(inMsg.contains("favico")))
				url = inMsg.substring(inMsg.indexOf("url=") + 4, inMsg.indexOf(" HTTP"));
			else isComplete = false;

			if(!isComplete){

				out = new DataOutputStream(transferObject.getSocket().getOutputStream());

				//acknowledge the request.
				String outMsg = "HTTP/1.0 409\n\n";
				byte by[] = new byte[ BUFFER_SIZE ];
				byte my[] = new byte[ BUFFER_SIZE ];
				index = outMsg.length();    

				index = outMsg.length();
				by = outMsg.getBytes();
				out.write( by, 0, index );
				out.flush();

				String message = "ProxyCache: Incomplete Message!\n\n" ;
				index = message.length();
				my = message.getBytes();		

				out.write( my, 0, index );	    
				out.flush();

				//close out all resources
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (transferObject.getSocket() != null) {
					transferObject.getSocket().close();
				}

				return ;

			}
			
			url = java.net.URLDecoder.decode(url, "UTF-8");

			transferObject.setType(parseRequestType(inMsg));
			transferObject.setHeaders(getHeaders( inMsg.substring(inMsg.indexOf('\n') + 1) ));
			transferObject.setURL(url);

			transferQueue.add(transferObject);

		} catch (IOException e) {
			System.out.println(getDateStr() + " ReceiverThread:  could not listen on port: " + port);
			writeLog( " ReceiverThread:  could not listen on port: " + port);

			if(brokenSocketQueue.isEmpty())brokenSocketQueue.add(new Object());
			try{
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (transferObject.getSocket() != null) {
					transferObject.getSocket().close();
				}
			}catch(Exception e2){
				System.out.println(getDateStr() + "ReceiverThread could not close socket(thread) on port: " + port);
				writeLog("ReceiverThread could not close socket(thread) on port: " + port);
			}
		}	

		return;
	}

	/**
	 * Gets the headers.
	 *
	 * @param message the message
	 * @return the headers
	 */
	public Map<String, String> getHeaders(String message){

		Map<String, String> headerMap = new HashMap<>();

		String key = new String();
		String value = new String();
		String tempMessage = new String();

		while( (message.length() > 0) && (message.contains(":")) ){

			if(message.indexOf("\n") >= 0){
				tempMessage = message.substring(0,  message.indexOf("\n"));
				message = message.substring(message.indexOf("\n") + 1);
			}else break;

			key = tempMessage.substring(0,tempMessage.indexOf(":"));
			value = tempMessage.substring(tempMessage.indexOf(":") + 1);

			headerMap.put(key, value);
		}

		return headerMap;
	}

	/**
	 * Parses the request type.
	 *
	 * @param message the message
	 * @return the string
	 */
	public String parseRequestType(String message){

		String result = "GET";

		if(message != null ){

			if(message.indexOf(" ") > 0)result = message.substring(0, message.indexOf(" "));			
		}

		return result;
	}

	/**
	 * Gets the data.
	 *
	 * @param in the in
	 * @param size the size
	 * @return the data
	 */
	public String getData(BufferedReader in, int size) {
		//begin get request from client
		String _msg = new String();
		Calendar myCalendar = Calendar.getInstance();
		//one second time out
		myCalendar.add(Calendar.SECOND, 1);

		if(size < 1) {
			_msg =  getData(in);
		} else {

			try{
				int msgLength = 0;
				char _c[] = new char[1];
				while(msgLength <  size){
					_c[0] = (char)in.read();
					if(_c[0] != -1){
						_msg = _msg + new String(_c);
						msgLength = msgLength + 1;
					}
					//check for timeout.
					if( (Calendar.getInstance()).after(myCalendar))break;
				}
				if(size == msgLength){
					isComplete = true;
				}else{
					isComplete = false;			
				}	     

			} catch (IOException e) {
				isComplete = false;
				writeLog("ReceiverThread: Could Not Read the incoming url!!");
			}
		}
		return _msg;
	}

	/**
	 * Gets the data.
	 *
	 * @param in the in
	 * @return the data
	 */
	public String getData(BufferedReader in) {
		//begin get request from client
		String msg = new String();
		try{
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {

				if(inputLine.length() == 0) break;
				msg += inputLine;
			}

		} catch (IOException e) {
			isComplete = false;
			writeLog("ReceiverThread: Could Not Read the incoming url!!");
		}

		return msg;
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