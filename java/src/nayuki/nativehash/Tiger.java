/* 
 * Native hash functions for Java
 * 
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/native-hash-functions-for-java
 */

package nayuki.nativehash;

import java.util.Arrays;
import org.checkerframework.checker.signedness.qual.*;


public class Tiger extends BlockHasher {
	
	protected @Unsigned long[] state;
	protected @Unsigned byte padding;
	
	
	
	public Tiger() {
		super(64);
		state = new long[]{0x0123456789ABCDEFL, 0xFEDCBA9876543210L, 0xF096A5B4C3B2E187L};
		padding = 0x01;
	}
	
	
	
	protected void compress(@Unsigned byte[] msg, int off, int len) {
		if (!compress(state, msg, off, len))
			throw new RuntimeException("Native call failed");
	}
	
	
	protected @Unsigned byte[] getHashDestructively() {
		block[blockFilled] = padding;
		blockFilled++;
		Arrays.fill(block, blockFilled, block.length, (byte)0);
		if (blockFilled + 8 > block.length) {
			compress(block, 0, block.length);
			Arrays.fill(block, (byte)0);
		}
		length = length << 3;
		for (int i = 0; i < 8; i++){
			//Length cannot be stored as unsigned
			@SuppressWarnings("signedness")
			@Unsigned byte k= (byte)(length >>> (i * 8));
			block[block.length - 8 + i] = k;
		}
		compress(block, 0, block.length);
		
		@Unsigned byte[] result = new byte[state.length * 8];
		for (int i = 0; i < result.length; i++)
			result[i] = (byte)(state[i / 8] >>> (i % 8 * 8));
		return result;
	}
	
	
	private static native boolean compress(@Unsigned long[] state, @Unsigned byte[] msg, int off, int len);
	
	
	static {
		System.loadLibrary("nayuki-native-hashes");
	}
	
}
