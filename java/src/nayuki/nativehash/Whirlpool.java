/* 
 * Native hash functions for Java
 * 
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/native-hash-functions-for-java
 */

package nayuki.nativehash;

import java.util.Arrays;
import org.checkerframework.checker.signedness.qual.*;


public class Whirlpool extends BlockHasher {
	
	protected @Unsigned byte[] state;
	
	
	
	public Whirlpool() {
		super(64);
		state = new byte[64];
	}
	
	
	
	protected void compress(@Unsigned byte[] msg, int off, int len) {
		if (!compress(state, msg, off, len))
			throw new RuntimeException("Native call failed");
	}
	
	
	protected @Unsigned byte[] getHashDestructively() {
		/*Cast from @IntVal(128) int" to "@IntVal(-128) byte cannot be statically verified
		  Similar to issue #2367*/
		@SuppressWarnings("value")
		@Unsigned byte b=(byte)0x80;
		block[blockFilled] = b;
		blockFilled++;
		Arrays.fill(block, blockFilled, block.length, (byte)0);
		if (blockFilled + 32 > block.length) {
			compress(block, 0, block.length);
			Arrays.fill(block, (byte)0);
		}
		length = length << 3;
		for (int i = 0; i < 8; i++){
			//Length cannot be stored as unsigned
			@SuppressWarnings("signedness")
			@Unsigned byte k= (byte)(length >>> (i * 8));
			block[block.length - 1 + i] = k;
		}
		compress(block, 0, block.length);
		
		return state;
	}
	
	
	private static native boolean compress(@Unsigned byte[] state, @Unsigned byte[] msg, int off, int len);
	
	
	static {
		System.loadLibrary("nayuki-native-hashes");
	}
	
}
