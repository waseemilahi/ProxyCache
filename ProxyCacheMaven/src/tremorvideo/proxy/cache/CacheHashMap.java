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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Waseem Ilahi
 *
 */
public class CacheHashMap extends LinkedHashMap<String, ResponseObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int MAX_ENTRIES = 1000;
	
	public CacheHashMap(int maxSize){
		
		super(1000, 0.75f, true);
		
		this.MAX_ENTRIES = maxSize;
	}

	@Override
    protected boolean removeEldestEntry(Map.Entry<String,ResponseObject> eldest) {
       return size() > MAX_ENTRIES;
    }

}
