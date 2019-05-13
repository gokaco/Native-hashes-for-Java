/* 
 * Native hash functions for Java
 * 
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/native-hash-functions-for-java
 */

package nayuki.nativehash;

import java.util.Arrays;
import org.checkerframework.checker.signedness.qual.*;


public class Sha512 extends BlockHasher {
	
	protected @Unsigned long[] state;
	
	
	
	public Sha512() {
		super(128);
		state = new long[]{0x6A09E667F3BCC908L, 0xBB67AE8584CAA73BL, 0x3C6EF372FE94F82BL, 0xA54FF53A5F1D36F1L, 0x510E527FADE682D1L, 0x9B05688C2B3E6C1FL, 0x1F83D9ABFB41BD6BL, 0x5BE0CD19137E2179L};
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
		if (blockFilled + 16 > block.length) {
			compress(block, 0, block.length);
			Arrays.fill(block, (byte)0);
		}
		length = length << 3;
		for (int i = 0; i < 8; i++){
			//Length is not stored as unsigned
			@SuppressWarnings("signedness")
			@Unsigned byte k= (byte)(length >>> (i * 8));
			block[block.length - 1 + i] = k;
		}
		compress(block, 0, block.length);
		
		@Unsigned byte[] result = new byte[state.length * 4];
		for (int i = 0; i < result.length; i++)
			result[i] = (byte)(state[i / 8] >>> (56 - i % 8 * 8));
		return result;
	}
	
	
	private static native boolean compress(@Unsigned long[] state, @Unsigned byte[] msg, int off, int len);
	
	
	static {
		System.loadLibrary("nayuki-native-hashes");
	}
	
}
