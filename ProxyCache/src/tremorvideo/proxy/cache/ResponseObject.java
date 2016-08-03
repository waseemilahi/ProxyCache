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

/**
 * The Class ResponseObject.
 *
 * @author Waseem Ilahi
 */
public class ResponseObject {

	/** The response string. */
	private String responseString;


	/**
	 * Instantiates a new response object.
	 *
	 * @param response the response
	 */
	public ResponseObject(String response){

		this.responseString = response;
	}

	/**
	 * Sets the response.
	 *
	 * @param response the new response
	 */
	public void setResponse(String response){

		this.responseString = response;
	}

	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public String getResponse(){

		return this.responseString;
	}

}
