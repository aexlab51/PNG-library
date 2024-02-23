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
 * A standard RGB color space (sRGB) chunk. This indicates the image
 * samples conform to the sRGB color space and should be displayed
 * using the specified rendering intent. Instances are immutable.
 * @see https://www.w3.org/TR/2003/REC-PNG-20031110/#11sRGB
 */
public class Srgb implements SmallDataChunk {
	
	static final String TYPE = "sRGB";
	private final RenderingIntent renderingIntent;


	/*---- Constructor and factory ----*/
	
	public Srgb(RenderingIntent renderingIntent) {
		Objects.requireNonNull(renderingIntent);
		this.renderingIntent = renderingIntent;
	}
	
	
	static Srgb read(ChunkReader in) throws IOException {
		Objects.requireNonNull(in);
		RenderingIntent renderingIntent = in.readEnum(RenderingIntent.values());
		return new Srgb(renderingIntent);
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public void writeData(ChunkWriter out) throws IOException {
		out.writeUint8(renderingIntent);
	}
	
	
	
	/*---- Enumeration ----*/
	
	public enum RenderingIntent {
		PERCEPTUAL,
		RELITAVIE_COLORIMETRIC,
		SATURATION,
		ABSOLUTE_COLORIMETRIC,
	}
	
}
