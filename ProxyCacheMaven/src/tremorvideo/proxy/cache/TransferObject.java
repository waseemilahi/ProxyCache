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

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class TransferObject.
 *
 * @author Waseem Ilahi
 */
public class TransferObject {

	/** The url. */
	private String url;

	/** The incoming socket. */
	private Socket incomingSocket;

	/** The request type. */
	private String requestType;

	/** The headers. */
	private  Map<String, String> headers = new HashMap<>();

	/**
	 * Instantiates a new transfer object.
	 *
	 * @param url the url
	 * @param socket the socket
	 * @param requestType the request type
	 * @param headers the headers
	 */
	public TransferObject(String url, Socket socket, String requestType, Map<String, String> headers){

		this.incomingSocket = socket;
		this.url = url;
		this.requestType = requestType;
		this.headers = headers;

	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setURL(String url){

		this.url = url;
	}

	/**
	 * Sets the socket.
	 *
	 * @param socket the new socket
	 */
	public void setSocket(Socket socket){

		this.incomingSocket = socket;
	}

	/**
	 * Sets the type.
	 *
	 * @param requestType the new type
	 */
	public void setType(String requestType){

		this.requestType = requestType;
	}

	/**
	 * Sets the headers.
	 *
	 * @param headers the headers
	 */
	public void setHeaders(Map<String, String> headers){
		this.headers = headers;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL(){

		return this.url;
	}

	/**
	 * Gets the socket.
	 *
	 * @return the socket
	 */
	public Socket getSocket(){

		return this.incomingSocket;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType(){

		return this.requestType;
	}

	/**
	 * Gets the headers.
	 *
	 * @return the headers
	 */
	public Map<String, String> getHeaders(){

		return this.headers;
	}
}
