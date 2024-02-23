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
 * An image offset (oFFs) chunk. This specifies the position where
 * the image should be printed on a page alone, or defines the image's
 * location with respect to a larger screen. Instances are immutable.
 * @see https://ftp-osl.osuosl.org/pub/libpng/documents/pngext-1.5.0.html#C.oFFs
 */
public class Offs implements SmallDataChunk {
	
	
	static final String TYPE = "oFFs";
	private final int xPosition;
	private final int yPosition;
	private final UnitSpecifier unitSpecifier;


	/*---- Constructor and factory ----*/
	
	public Offs(
		int xPosition,
		int yPosition,
		UnitSpecifier unitSpecifier) {
		if (xPosition == Integer.MIN_VALUE || yPosition == Integer.MIN_VALUE)
			throw new IllegalArgumentException("Invalid int32 value");
		Objects.requireNonNull(unitSpecifier);

		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.unitSpecifier = unitSpecifier;
	}
	
	
	static Offs read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		int xPosition = in.readInt32();
		int yPosition = in.readInt32();
		UnitSpecifier unitSpecifier = in.readEnum(UnitSpecifier.values());
		return new Offs(xPosition, yPosition, unitSpecifier);
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public void writeData(ChunkWriter out) throws IOException {
		out.writeInt32(xPosition);
		out.writeInt32(yPosition);
		out.writeUint8(unitSpecifier);
	}
	
	
	
	/*---- Enumeration ----*/
	
	public enum UnitSpecifier {
		PIXEL,
		MICROMETRE,
	}
	
}
