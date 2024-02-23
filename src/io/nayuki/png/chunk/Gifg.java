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
 * A GIF graphic control extension (gIFg) chunk. This provides
 * backward compatibility for GIF images. Instances are immutable.
 * @see https://ftp-osl.osuosl.org/pub/libpng/documents/pngext-1.5.0.html#C.gIFg
 */
public class Gifg implements SmallDataChunk {
	
	
	static final String TYPE = "gIFg";

	private final int disposalMethod;
	private final boolean userInputFlag;
	private final int delayTime;


	/*---- Constructor and factory ----*/
	public Gifg(int disposalMethod, boolean userInputFlag, int delayTime) {
		if (disposalMethod >>> 3 != 0)
			throw new IllegalArgumentException("Disposal method out of range");
		if (delayTime >>> 16 != 0)
			throw new IllegalArgumentException("Delay time out of range");

		this.disposalMethod = disposalMethod;
		this.userInputFlag = userInputFlag;
		this.delayTime = delayTime;
	}
	
	
	static Gifg read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		int disposalMethod = in.readUint8();
		int userInputFlag = in.readUint8();
		int delayTime = in.readUint16();
		if (userInputFlag >>> 1 != 0)
			throw new IllegalArgumentException("User input flag out of range");
		return new Gifg(disposalMethod, userInputFlag != 0, delayTime);
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public void writeData(ChunkWriter out) throws IOException {
		out.writeUint8(disposalMethod);
		out.writeUint8(userInputFlag ? 1 : 0);
		out.writeUint16(delayTime);
	}
	
}
