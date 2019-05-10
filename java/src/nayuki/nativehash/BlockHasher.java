/* 
 * Native hash functions for Java
 * 
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/native-hash-functions-for-java
 */

package nayuki.nativehash;

import org.checkerframework.checker.signedness.qual.*;

public abstract class BlockHasher implements Cloneable {
	
	protected @Unsigned byte[] block;
	protected int blockFilled;
	protected long length;
	
	
	
	public BlockHasher(int blockLen) {
		block = new byte[blockLen];
		blockFilled = 0;
		length = 0;
	}
	
	
	
	public void update(@Unsigned byte[] b) {
		update(b, 0, b.length);
	}
	
	
	public void update(@Unsigned byte[] b, int off, int len) {
		int blockLen = block.length;
		length = length+len;
		
		if (blockFilled > 0) {
			int n = Math.min(blockLen - blockFilled, len);
			System.arraycopy(b, off, block, blockFilled, n);
			blockFilled = blockFilled +n;
			if (blockFilled == blockLen) {
				compress(block, 0, blockLen);
				off += n;
				len -= n;
			} else
				return;
		}
		
		if (len >= blockLen) {
			int n = len / blockLen * blockLen;
			compress(b, off, n);
			off += n;
			len -= n;
		}
		
		System.arraycopy(b, off, block, 0, len);
		blockFilled = len;
	}
	
	
	public BlockHasher clone() {
		try {
			BlockHasher result = (BlockHasher)super.clone();
			result.block = result.block.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
	
	
	public @Unsigned byte[] getHash() {
		return clone().getHashDestructively();
	}
	
	
	protected abstract void compress(@Unsigned byte[] msg, int off, int len);
	
	
	protected abstract @Unsigned byte[] getHashDestructively();
	
}
