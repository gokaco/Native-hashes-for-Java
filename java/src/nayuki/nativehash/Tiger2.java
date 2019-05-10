/* 
 * Native hash functions for Java
 * 
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/native-hash-functions-for-java
 */

package nayuki.nativehash;


public class Tiger2 extends Tiger {
	
	public Tiger2() {
		super();
		@SuppressWarnings("value")
		byte b= (byte)0x80;
		padding = b;
	}
	
}
