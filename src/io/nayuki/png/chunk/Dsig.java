/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/png-library
 */

package io.nayuki.png.chunk;

import java.io.IOException;
import java.util.Objects;


/**
 * A digital signature (dSIG) chunk. This allows detecting unauthorized changes
 * to the chunks enclosed in a pair of digital signature chunks. Instances
 * should be treated as immutable, but arrays are not copied defensively.
 * @see https://ftp-osl.osuosl.org/pub/libpng/documents/pngext-1.5.0.html#RC.dSIG
 */
public class Dsig implements BytesDataChunk {
	
	static final String TYPE = "dSIG";
	private final byte[] data;


	/*---- Constructor and factory ----*/
	
	public Dsig(byte[] data) {
		Objects.requireNonNull(data);
		this.data = data;
	}
	
	
	static Dsig read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		return new Dsig(in.readRemainingBytes());
	}
	
	
	/*---- Method ----*/
	
	@Override public String getType() {
		return TYPE;
	}

	@Override
	public byte[] data() {
		return data;
	}
}
