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
 * An exchangeable image file profile (eXIf) chunk. This typically conveys
 * metadata for images produced by digital cameras. Instances should
 * be treated as immutable, but arrays are not copied defensively.
 * @see https://ftp-osl.osuosl.org/pub/libpng/documents/pngext-1.5.0.html#C.eXIf
 */
public class Exif implements BytesDataChunk {
	
	static final String TYPE = "eXIf";
	private final byte[] data;


	/*---- Constructor and factory ----*/
	
	public Exif(byte[] data) {
		Objects.requireNonNull(data);
		this.data = data;
	}
	
	
	static Exif read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		return new Exif(in.readRemainingBytes());
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
