/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/
 */

package io.nayuki.png.chunk;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import io.nayuki.png.Chunk;


/**
 * A palette (PLTE) chunk. This is required for indexed-color images,
 * but only behaves as a suggestion for true-color images. Instances
 * should be treated as immutable, but arrays are not copied defensively.
 * @see https://www.w3.org/TR/2003/REC-PNG-20031110/#11PLTE
 */
public record Plte(byte[] data) implements Chunk {
	
	static final String TYPE = "PLTE";
	
	
	/*---- Constructors ----*/
	
	public Plte {
		Objects.requireNonNull(data);
		int numEntries = data.length / 3;
		if (!(data.length % 3 == 0 && 1 <= numEntries && numEntries <= 256))
			throw new IllegalArgumentException();
	}
	
	
	public Plte(int[] entries) {
		this(convert(entries));
	}
	
	
	private static byte[] convert(int[] entries) {
		Objects.requireNonNull(entries);
		checkNumEntries(entries.length);
		byte[] result = new byte[Math.multiplyExact(entries.length, 3)];
		int i = 0;
		for (int entry : entries) {
			if (entry >>> 24 != 0)
				throw new IllegalArgumentException();
			for (int j = 16; j >= 0; j -= 8, i++)
				result[i] = (byte)(entry >>> j);
		}
		return result;
	}
	
	
	private static void checkNumEntries(int numEntries) {
		if (!(1 <= numEntries && numEntries <= 256))
			throw new IllegalArgumentException();
	}
	
	
	public static Plte read(int dataLen, DataInput in) throws IOException {
		var data = new byte[dataLen];
		in.readFully(data);
		return new Plte(data);
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public byte[] getData() {
		return data;
	}
	
	
	@Override public void writeData(DataOutput out) throws IOException {
		out.write(data);
	}
	
}
