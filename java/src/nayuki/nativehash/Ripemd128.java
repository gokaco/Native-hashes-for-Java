/* 
 * Native hash functions for Java
 * 
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/native-hash-functions-for-java
 */

package nayuki.nativehash;

import java.util.Arrays;
import org.checkerframework.checker.signedness.qual.*;


public class Ripemd128 extends BlockHasher {
	
	protected @Unsigned int[] state;
	
	
	
	public Ripemd128() {
		super(64);
		state = new int[]{0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476};
	}
	
	
	
	protected void compress(@Unsigned byte[] msg, int off, int len) {
		if (!compress(state, msg, off, len))
			throw new RuntimeException("Native call failed");
	}
	
	
	protected @Unsigned byte[] getHashDestructively() {
		//Related to issue #2367
		@SuppressWarnings("value")
		@Unsigned byte b=(byte)0x80;
		block[blockFilled] = b;
		blockFilled++;
		Arrays.fill(block, blockFilled, block.length, (byte)0);
		if (blockFilled + 8 > block.length) {
			compress(block, 0, block.length);
			Arrays.fill(block, (byte)0);
		}
		length = length << 3;
		for (int i = 0; i < 8; i++){
			//Length is not stored as unsigned
			@SuppressWarnings("signedness")
			@Unsigned byte k= (byte)(length >>> (i * 8));
			block[block.length - 8 + i] = k;
		}
		compress(block, 0, block.length);
		
		@Unsigned byte[] result = new byte[state.length * 4];
		for (int i = 0; i < result.length; i++)
			result[i] = (byte)(state[i / 4] >>> (i % 4 * 8));
		return result;
	}
	
	
	private static native boolean compress(@Unsigned int[] state, @Unsigned byte[] msg, int off, int len);
	
	
	static {
		System.loadLibrary("nayuki-native-hashes");
	}
	
}
