/* 
 * Native hash functions for Java
 * 
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/native-hash-functions-for-java
 */

package nayuki.nativehash;

import java.util.Arrays;
import org.checkerframework.checker.signedness.qual.*;


public class Md2 extends BlockHasher {
	
	protected @Unsigned byte[] state;
	protected @Unsigned byte[] checksum;
	
	
	
	public Md2() {
		super(16);
		state = new byte[48];
		checksum = new byte[16];
	}
	
	
	
	protected void compress(@Unsigned byte[] msg, int off, int len) {
		if (!compress(state, checksum, msg, off, len))
			throw new RuntimeException("Native call failed");
	}
	
	
	protected @Unsigned byte[] getHashDestructively() {
		for (int i = blockFilled; i < block.length; i++){
			//Length is not stored as unsigned
			@SuppressWarnings("signedness")
			@Unsigned byte k=(byte)(block.length - blockFilled);
			block[i] = k;
		}
		compress(block, 0, block.length);
		
		for (int i = 0; i < 16; i++)
			block[i] = checksum[i];
		compress(block, 0, block.length);
		
		return Arrays.copyOf(state, 16);
	}
	
	
	private static native boolean compress(@Unsigned byte[] state, @Unsigned byte[] checksum, @Unsigned byte[] msg, int off, int len);
	
	
	static {
		System.loadLibrary("nayuki-native-hashes");
	}
	
}
