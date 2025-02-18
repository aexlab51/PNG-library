/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/png-library
 */

package io.nayuki.png.chunk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;


/**
 * A GIF application extension (gIFx) chunk. This provides backward compatibility for GIF
 * images. Instances should be treated as immutable, but arrays are not copied defensively.
 * @see https://ftp-osl.osuosl.org/pub/libpng/documents/pngext-1.5.0.html#C.gIFx
 */
public class Gifx implements Chunk {
	
	
	static final String TYPE = "gIFx";
	private final byte[] applicationIdentifier;
	private final byte[] authenticationCode;
	private final byte[] applicationData;


	/*---- Constructor and factory ----*/
	
	public Gifx(byte[] applicationIdentifier, byte[] authenticationCode, byte[] applicationData) {
		Objects.requireNonNull(applicationIdentifier);
		Objects.requireNonNull(authenticationCode);
		Objects.requireNonNull(applicationData);

		if (applicationIdentifier.length != 8 || authenticationCode.length != 3)
			throw new IllegalArgumentException("Data array length out of range");
		Util.checkedLengthSum(applicationIdentifier, authenticationCode, applicationData);

		this.applicationIdentifier = applicationIdentifier;
		this.authenticationCode = authenticationCode;
		this.applicationData = applicationData;
	}
	
	
	static Gifx read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		
		byte[] appIdentifier = new byte[8];
		in.readFully(appIdentifier, 0, appIdentifier.length);
		byte[] authCode = new byte[3];
		in.readFully(authCode, 0, authCode.length);
		byte[] appData = in.readRemainingBytes();
		return new Gifx(appIdentifier, authCode, appData);
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public void writeChunk(OutputStream out) throws IOException {
		int dataLen = Util.checkedLengthSum(applicationIdentifier, authenticationCode, applicationData);
		try (ChunkWriter cout = new ChunkWriter(dataLen, TYPE, out)) {
			cout.write(applicationIdentifier);
			cout.write(authenticationCode);
			cout.write(applicationData);
		}
	}
	
}
