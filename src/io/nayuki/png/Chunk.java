/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/
 */

package io.nayuki.png;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.InflaterOutputStream;


/**
 * A PNG/MNG/JNG chunk. Each chunk has a type (4 ASCII uppercase/lowercase
 * letters) and binary data (0 to 2<sup>31</sup>&minus;1 bytes).
 * The CRC-32 field is excluded and handled automatically when reading/writing files.
 */
public interface Chunk {
	
	public abstract String getType();
	
	
	public default boolean isCritical() {
		return (getType().charAt(0) & 0x20) == 0;
	}
	
	public default boolean isPublic() {
		return (getType().charAt(1) & 0x20) == 0;
	}
	
	public default boolean isSafeToCopy() {
		return (getType().charAt(3) & 0x20) != 0;
	}
	
	
	public default int getDataLength() {
		return getData().length;
	}
	
	
	public default byte[] getData() {
		try {
			var out = new ByteArrayOutputStream();
			writeData(new DataOutputStream(out));
			return out.toByteArray();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	
	public abstract void writeData(DataOutput out) throws IOException;
	
	
	public static void checkType(String type) {
		Objects.requireNonNull(type);
		if (type.length() != 4)
			throw new IllegalArgumentException();
		for (int i = 0; i < type.length(); i++) {
			char c = type.charAt(i);
			if (!('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z'))
				throw new IllegalArgumentException();
		}
	}
	
	
	
	public enum CompressionMethod {
		ZLIB_DEFLATE(data -> {
			var bout = new ByteArrayOutputStream();
			try (var iout = new InflaterOutputStream(bout)) {
				iout.write(data);
			}
			return bout.toByteArray();
		});
		
		private Function decompressor;
		
		private CompressionMethod(Function decomp) {
			decompressor = decomp;
		}
		
		public byte[] decompress(byte[] data) throws IOException {
			return decompressor.apply(data);
		}
		
		
		private interface Function {
			public byte[] apply(byte[] data) throws IOException;
		}
	}
	
}
