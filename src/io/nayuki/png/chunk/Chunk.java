/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/png-library
 */

package io.nayuki.png.chunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;


/**
 * A PNG/MNG/JNG chunk. Each chunk has a type (4 ASCII uppercase/lowercase
 * letters) and binary data (0 to 2<sup>31</sup>&minus;1 bytes). The CRC-32
 * field is excluded and handled automatically when reading/writing files.
 * Chunk types where the reserved bit is set (e.g. "ABcD") cannot be represented
 * because they can potentially have a different set of fields. Classes that
 * implement this interface can choose to have mutable or immutable instances.
 */
public interface Chunk {
	
	/**
	 * Reads from the specified input stream and returns a chunk object representing
	 * the data that is read, or empty if the end of stream is immediately
	 * encountered. If the chunk type corresponds to a known class in this package
	 * ({@link io.nayuki.png.chunk}), then this function returns a chunk of that type
	 * (e.g. reading type "IHDR" will return an object of the class {@link Ihdr} or
	 * throw an {@code IllegalArgumentException} if the data is invalid. Otherwise if
	 * the chunk type is not of a known type, then a {@link Custom} chunk is returned.
	 * @param in the input to read the chunk's data from (not {@code null})
	 * @return a chunk object representing the data parsed from the chunk reader,
	 * or empty if the end of stream is immediately encountered, not {@code null}
	 * @throws NullPointerException if the input stream is {@code null}
	 * @throws IllegalArgumentException if the chunk contains invalid data
	 * @throws IOException if an I/O exception occurs
	 */
	public static Optional<Chunk> read(InputStream in) throws IOException {
		Optional<ChunkReader> temp = ChunkReader.tryNew(in);
		//if (temp.isEmpty())
		if (!temp.isPresent())
			return Optional.empty();
		
		try (ChunkReader cin = temp.get()) {
			Chunk chunk;
			switch (cin.getType()){
				case Actl.TYPE: chunk=Actl.read(cin); break;
				case Bkgd.TYPE: chunk=Bkgd.read(cin); break;
				case Chrm.TYPE: chunk=Chrm.read(cin); break;
				case Dsig.TYPE: chunk=Dsig.read(cin); break;
				case Exif.TYPE: chunk=Exif.read(cin); break;
				case Fctl.TYPE: chunk=Fctl.read(cin); break;
				case Fdat.TYPE: chunk=Fdat.read(cin); break;
				case Gama.TYPE: chunk=Gama.read(cin); break;
				case Gifg.TYPE: chunk=Gifg.read(cin); break;
				case Gift.TYPE: chunk=Gift.read(cin); break;
				case Gifx.TYPE: chunk=Gifx.read(cin); break;
				case Hist.TYPE: chunk=Hist.read(cin); break;
				case Iccp.TYPE: chunk=Iccp.read(cin); break;
				case Idat.TYPE: chunk=Idat.read(cin); break;
				case Iend.TYPE: chunk=Iend.SINGLETON; break;
				case Ihdr.TYPE: chunk=Ihdr.read(cin); break;
				case Itxt.TYPE: chunk=Itxt.read(cin); break;
				case Offs.TYPE: chunk=Offs.read(cin); break;
				case Pcal.TYPE: chunk=Pcal.read(cin); break;
				case Phys.TYPE: chunk=Phys.read(cin); break;
				case Plte.TYPE: chunk=Plte.read(cin); break;
				case Sbit.TYPE: chunk=Sbit.read(cin); break;
				case Scal.TYPE: chunk=Scal.read(cin); break;
				case Splt.TYPE: chunk=Splt.read(cin); break;
				case Srgb.TYPE: chunk=Srgb.read(cin); break;
				case Ster.TYPE: chunk=Ster.read(cin); break;
				case Text.TYPE: chunk=Text.read(cin); break;
				case Time.TYPE: chunk=Time.read(cin); break;
				case Trns.TYPE: chunk=Trns.read(cin); break;
				case Ztxt.TYPE: chunk=Ztxt.read(cin); break;
				default: chunk=Custom.read(cin);
			}

			return Optional.of(chunk);
		}
	}
	
	
	/**
	 * Returns the type of this chunk, a length-4 ASCII uppercase/lowercase string.
	 * @return the type of this chunk (not {@code null})
	 */
	public abstract String getType();
	
	
	/**
	 * Tests whether this chunk is critical. Generally speaking, when a
	 * decoder encounters an unrecognized critical chunk, it should stop
	 * processing the file and return an error. Non-critical chunks are
	 * also known as ancillary. Critical chunks must be unsafe to copy.
	 * @return whether this chunk is critical
	 */
	public default boolean isCritical() {
		return (getType().charAt(0) & 0x20) == 0;
	}
	
	
	/**
	 * Tests whether this chunk is public. Each public chunk type must be defined
	 * in the PNG standard or in a list maintained by the registration authority.
	 * @return whether this chunk is public
	 */
	public default boolean isPublic() {
		return (getType().charAt(1) & 0x20) == 0;
	}
	
	
	/**
	 * Tests whether this chunk is safe to copy. Generally speaking, an
	 * application that reads a PNG file, performs some editing, and writes a PNG
	 * file should handle unrecognized chunks by copying safe-to-copy ones and
	 * omitting unsafe-to-copy ones. Safe-to-copy chunks must be non-critical
	 * (ancillary). The detailed exact rules are found in the PNG standard.
	 * @return whether this chunk is safe to copy
	 */
	public default boolean isSafeToCopy() {
		return (getType().charAt(3) & 0x20) != 0;
	}
	
	
	/**
	 * Writes this chunk's entire sequence of bytes (length,
	 * type, data, CRC-32) to the specified output stream.
	 * @param out the output stream to write to (not {@code null})
	 * @throws NullPointerException if {@code out} is {@code null}
	 * @throws IOException if an I/O exceptions occurs
	 */
	public abstract void writeChunk(OutputStream out) throws IOException;
	
	
	/**
	 * Throws an exception if the specified chunk type string is invalid.
	 * A type is valid iff all these conditions are met:
	 * <ul>
	 *   <li>The string has length 4</li>
	 *   <li>All characters are ASCII uppercase or lowercase</li>
	 *   <li>Character 2 (0-based) is uppercase</li>
	 *   <li>If character 0 (0-based) is uppercase, then character 3 is uppercase</li>
	 * </ul>
	 * @param type the chunk type string to check
	 * @throws NullPointerException if the string is {@code null}
	 * @throws IllegalArgumentException if the string does not satisfy all requirements
	 */
	public static void checkType(String type) {
		Objects.requireNonNull(type);
		if (type.length() != 4)
			throw new IllegalArgumentException("Invalid type string length");
		for (int i = 0; i < type.length(); i++) {
			char c = type.charAt(i);
			if (!('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z'))
				throw new IllegalArgumentException("Invalid type string characters");
		}
		if (type.charAt(2) >= 'a')
			throw new IllegalArgumentException("Reserved chunk type");
		if (type.charAt(0) <= 'Z' && type.charAt(3) >= 'a')
			throw new IllegalArgumentException("Chunk type that is critical must be unsafe to copy");
	}
	
	
	
	/*---- Enumeration ----*/
	
	/**
	 * The list of defined compression methods. This is used in several chunk types.
	 */
	public enum CompressionMethod {
		
		/** The DEFLATE compressed format (specified in RFC 1951) wrapped in a ZLIB container (RFC 1950). */
		ZLIB_DEFLATE {
			public byte[] compress(byte[] data) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				try (DeflaterOutputStream dout = new DeflaterOutputStream(bout)) {
					dout.write(data);
				} catch (IOException e) {
					throw new AssertionError("Unreachable exception", e);
				}
				return bout.toByteArray();
			}
			
			public byte[] decompress(byte[] data) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream(data.length);
				try (InflaterOutputStream iout = new InflaterOutputStream(bout)) {
					iout.write(data);
				} catch (IOException e) {
					throw new IllegalArgumentException("Invalid compressed data", e);
				}
				return bout.toByteArray();
			}
		};
		
		
		public abstract byte[] compress(byte[] data);
		
		public abstract byte[] decompress(byte[] data);
		
	}
	
}
