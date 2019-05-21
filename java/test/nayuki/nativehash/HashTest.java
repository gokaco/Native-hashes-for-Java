/* 
 * Native hash functions for Java
 * 
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/native-hash-functions-for-java
 */

package nayuki.nativehash;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import org.checkerframework.checker.signedness.qual.*;


abstract class HashTest {
	
	protected static Random random = new Random();
	
	
	protected abstract BlockHasher newHasher(boolean useNative);
	
	protected abstract String[][] getTestVectors();
	
	
	public void run() {
		selfCheck();
		benchmark();
	}
	
	
	public void selfCheck() {
		// Check test vectors
		for (String[] testCase : getTestVectors()) {
			String message = testCase[1];
			String expectHash = testCase[0];
			if (!toHex(getHash(message, false)).equals(expectHash)) {
				String errorMsg = String.format("Self-check failed: %s(%s) != %s", newHasher(false).getClass().getName(), message, expectHash);
				throw new AssertionError(errorMsg);
			}
		}
		
		// Randomized test
		for (int i = 0; i < 1000; i++) {
			int len = random.nextInt(10000);
			BlockHasher h0 = newHasher(false);
			BlockHasher h1 = newHasher(true);
			while (len > 0) {
				int n = random.nextInt(len) + 1;
				@Unsigned byte[] b = new byte[n];
				/*Unable to take parameter as Unsigned
				  Annotation is needed */
				random.nextBytes(b);
				h0.update(b);
				h1.update(b);
				//Issue #2482
				len = len-n;
			}
			//Unable to take parameters as unsigned. Needs Annotation.
			@SuppressWarnings("signedness")
			boolean p = !Arrays.equals(h0.getHash(), h1.getHash());
			if (p)
				throw new AssertionError("Native/Java hash mismatch");
		}
	}
	
	
	public void benchmark() {
		BlockHasher hashJava = newHasher(false);
		BlockHasher hashNative = newHasher(true);
		@Unsigned byte[] b = new byte[1 << 27];  // 128 MiB
		System.out.println("Block size    Java impl    Native impl");
		while (true) {
			/*Unable to take parameter as Unsigned
				  Annotation is needed */
			random.nextBytes(b);
			for (int i = 12; i <= 27; i++) {
				int len = 1 << i;
				long startTime;
				
				startTime = System.nanoTime();
				hashJava.update(b, 0, len);
				double speed0 = len / 1048576.0 * 1e9 / (System.nanoTime() - startTime);
				
				startTime = System.nanoTime();
				hashNative.update(b, 0, len);
				double speed1 = len / 1048576.0 * 1e9 / (System.nanoTime() - startTime);
				
				System.out.printf("%10d B  %5.1f MiB/s  %5.1f MiB/s%n", len, speed0, speed1);
			}
		}
	}
	
	
	protected @Unsigned byte[] getHash(String msg, boolean useNative) {
		try {
			BlockHasher h = newHasher(useNative);
			//Return type cannot be stored as Unsigned. Needs annotation.
			@SuppressWarnings("signedness")
			@Unsigned byte[] p= msg.getBytes("US-ASCII");
			h.update(p);
			return h.getHash();
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}
	}
	
	
	protected static String toHex(@Unsigned byte[] hash) {
		String result = "";
		for (byte b : hash)
			result += String.format("%02X", b & 0xFF);
		return result;
	}
	
}
