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
 * An animation control (acTL) chunk. This specifies the
 * number of frames and loops. Instances are immutable.
 * @see https://wiki.mozilla.org/APNG_Specification#.60acTL.60:_The_Animation_Control_Chunk
 */
public class Actl implements SmallDataChunk {

	static final String TYPE = "acTL";
	
	
	/*---- Constructor and factory ----*/
	private final int numFrames, numPlays;
	public Actl(int numFrames, int numPlays) {
		if (numFrames <= 0)
			throw new IllegalArgumentException("Invalid number of frames");
		if (numPlays < 0)
			throw new IllegalArgumentException("Invalid number of plays");
		this.numFrames = numFrames;
		this.numPlays = numPlays;
	}
	
	
	static Actl read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		int numFrames = in.readInt32();
		int numPlays  = in.readInt32();
		return new Actl(numFrames, numPlays);
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public void writeData(ChunkWriter out) throws IOException {
		out.writeInt32(numFrames);
		out.writeInt32(numPlays );
	}
	
}
