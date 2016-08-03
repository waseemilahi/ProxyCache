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
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author Waseem Ilahi
 *
 */
public class ProcessThread extends Thread {

	/** The broken socket queue. */
	tremorvideo.utils.nbqueue.Queue<Object> brokenSocketQueue;

	/** The transfer Object. */
	TransferObject transferObject;

	/** The cache map. */
	Map<String, ResponseObject> cacheMap;

	/** The port. */
	String port;

	public ProcessThread(tremorvideo.utils.nbqueue.Queue<Object> bsq, TransferObject to,
			Map<String, ResponseObject> cm, String portString){

		brokenSocketQueue = bsq;
		transferObject = to;
		cacheMap = cm;
		port = portString;
	}

	public void run(){

		/*
		System.out.println("Type: " + transferObject.getType());
		System.out.println("URL: " + transferObject.getURL());

		for (Map.Entry<String,String> entry : transferObject.getHeaders().entrySet()) {
			System.out.println("Key: " +  entry.getKey());
			System.out.println("Value: " +entry.getValue());
		}
		 */

		String responseMessage = new String();
		boolean foundInCache = false;
		String[] responseArray = null;

		synchronized(cacheMap){

			if(cacheMap.containsKey(transferObject.getURL())){
				foundInCache = true;
				responseMessage = cacheMap.get(transferObject.getURL()).getResponse();
			}
		}

		DataOutputStream out = null;
		BufferedReader in = null;

		if(!foundInCache){
			responseArray =  getURLResponse(transferObject);
			responseMessage = responseArray[1];
		}

		try {

			in = new BufferedReader(new InputStreamReader(transferObject.getSocket().getInputStream()));
			out = new DataOutputStream(transferObject.getSocket().getOutputStream());

			out.writeBytes(responseMessage);			
			out.flush();

			//close out all resources
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}

			if (transferObject.getSocket() != null) {
				transferObject.getSocket().close();
			}
		} catch (IOException e) {
			System.out.println(getDateStr() + " ProcessThread:  could not write to port: " + port);
			writeLog( "  ProcessThread:  could not write to port: " + port);
			System.out.println(getDateStr() + e.getMessage());

			if(brokenSocketQueue.isEmpty())brokenSocketQueue.add(new Object());
			try{
				if (out != null) {
					out.close();
				}
				if (transferObject.getSocket() != null) {
					transferObject.getSocket().close();
				}
			}catch(Exception e2){
				System.out.println(getDateStr() + "ProcessThread could not close socket(thread) on port: " + port);
				writeLog("ProcessThread could not close socket(thread) on port: " + port);
			}
		}	

		if(!(foundInCache) && (responseArray != null) && (responseArray[0]!= null) && (responseArray[0].equals("200"))){
			synchronized(cacheMap){
				cacheMap.put(transferObject.getURL(), new ResponseObject(responseMessage));
			}
		}

		writeLog(foundInCache ? " Cached URL: " + transferObject.getURL() : " New Response: " + transferObject.getURL());
		System.out.println(foundInCache ? " Cached URL: " + transferObject.getURL() : " New Response: " + transferObject.getURL());

		return ;
	}

	public String[] getURLResponse(TransferObject transObject){

		String[] response = new String[2];

		HttpURLConnection urlConnection;
		try {

			urlConnection = (HttpURLConnection) new URL(transObject.getURL()).openConnection();
			HttpURLConnection.setFollowRedirects(false);
			urlConnection.setRequestMethod(transObject.getType());
			for (Map.Entry<String,String> entry : transObject.getHeaders().entrySet()) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
			urlConnection.setRequestProperty("Access-Control-Allow-Origin", "*");
			urlConnection.setRequestProperty("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
			urlConnection.setDoOutput(true);

			DataOutputStream postBodyWriter = new DataOutputStream( urlConnection.getOutputStream());
			postBodyWriter.close();

			BufferedReader _PostResponseReader ;
			String input;

			if( (urlConnection.getContentEncoding() != null) && (urlConnection.getContentEncoding().contains("gzip")) ){
				_PostResponseReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(urlConnection.getInputStream())));

			}else {
				_PostResponseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
			}

			StringBuilder responseMessage = new StringBuilder();
			StringBuilder responseHeader = new StringBuilder();

			boolean containsConnectionCloaseHeader = false;
			boolean containsContentLengthHeader = false;

			response[0] = Integer.toString(urlConnection.getResponseCode());

			if(200 == urlConnection.getResponseCode())responseHeader.append("HTTP/1.1 200 OK\r\n\r\n");
			else responseHeader.append("HTTP/1.1 " + Integer.toString(urlConnection.getResponseCode()) + "\r\n\r\n");

			Map<String, List<String>> headerMap = urlConnection.getHeaderFields();
			for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
				if(entry.getKey() != null){
					responseHeader.append( entry.getKey() + " : " + 
							entry.getValue().toString().replace("[", "").replace("]", "") + "\r\n\r\n"  );
					if(entry.getKey().equals("Connection"))containsConnectionCloaseHeader = true;
					if(entry.getKey().equals("Content-Length"))containsContentLengthHeader = true;
				}
			}

			if(!containsConnectionCloaseHeader)responseHeader.append("Connection : close\r\n\r\n");

			while ((input = _PostResponseReader.readLine()) != null) {
				responseMessage.append(input).append("\r\n");
			}
			_PostResponseReader.close();

			if(!containsContentLengthHeader)responseHeader.append("Content-Length : " + (responseMessage.toString()).length() + "\r\n\r\n");

			response[1] =  responseHeader.toString() + responseMessage.toString();

			return response;

		}catch (SocketTimeoutException e) {
			writeLog(" SocketTimeoutException Occured While getting response from URL: " + e.getMessage());
			return new String[]{"500", "HTTP/1.1 500\r\n\r\n"};
		}catch (MalformedURLException e){
			writeLog(" MalformedURLException Occured While getting response from URL: " + e.getMessage());;
			return new String[]{"500", "HTTP/1.1 500\r\n\r\n"};
		}catch (IOException e) {
			writeLog(" IOException Occured While getting response from URL: " + e.getMessage());
			return new String[]{"500", "HTTP/1.1 500\r\n\r\n"};
		}catch (NullPointerException e) {
			writeLog(" NullPointerException Occured While getting response from URL: " + e.getMessage());
			return new String[]{"500", "HTTP/1.1 500\r\n\r\n"};
		}catch (Exception e) {
			writeLog(" Exception Occured While getting response from URL: " + e.getMessage());
			return new String[]{"500", "HTTP/1.1 500\r\n\r\n"};
		}
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