/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/
 */

package io.nayuki.png;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import io.nayuki.png.chunk.Idat;
import io.nayuki.png.chunk.Iend;
import io.nayuki.png.chunk.Ihdr;


/**
 * A structured representation of chunks that form a PNG file.
 * Instances are mutable. There is some degree of validation and exclusion of invalid data.
 * When serializing an instance, the resulting list of chunks is: field {@code ihdr}
 * (must be present), field {@beforeIdats} (zero or more), field {@code idats} (one or more),
 * field {@code afterIdats} (zero or more), {@code Iend.SINGLETON} (implicit).
 */
public final class PngImage {
	
	/**
	 * Reads the specified input file and returns a new
	 * {@code PngImage} object representing chunks read.
	 * @param inFile the input file to read from
	 * @throws NullPointerException if {@code inFile} is {@code null}
	 * @throws IOException if an I/O exception occurs
	 * @returns a new {@code XngFile} object representing chunks read
	 */
	public static PngImage read(File inFile) throws IOException {
		Objects.requireNonNull(inFile);
		try (var in = new BufferedInputStream(new FileInputStream(inFile))) {
			return read(in);
		}
	}
	
	
	/**
	 * Reads the specified input stream and returns a new {@code PngImage}
	 * object representing chunks read. This does not close the stream.
	 * This reads until the end of stream if no exception is thrown.
	 * @param in the input stream to read from
	 * @throws NullPointerException if {@code inFile} is {@code null}
	 * @throws IOException if an I/O exception occurs
	 * @returns a new {@code XngFile} object representing chunks read
	 */
	public static PngImage read(InputStream in) throws IOException {
		Objects.requireNonNull(in);
		XngFile xng = XngFile.read(in, true);
		if (xng.type() != XngFile.Type.PNG)
			throw new IllegalArgumentException();
		return new PngImage(xng.chunks());
	}
	
	
	/** The single IHDR chunk, if present. */
	public Optional<Ihdr> ihdr = Optional.empty();
	
	/** The chunks positioned before the IDAT chunks. */
	public List<Chunk> beforeIdats = new ArrayList<>();
	
	/** The consecutive IDAT chunks. */
	public List<Idat> idats = new ArrayList<>();
	
	/** The chunks positioned after the IDAT chunks. */
	public List<Chunk> afterIdats = new ArrayList<>();
	
	
	/**
	 * Constructs a blank PNG image where all fields are initially empty (not {@code null}).
	 */
	public PngImage() {}
	
	
	private PngImage(List<Chunk> chunks) {
		boolean hasIend = false;
		for (Chunk chunk : chunks) {
			if (hasIend)
				throw new IllegalArgumentException();
			else if (chunk instanceof Iend)
				hasIend = true;
			else if (ihdr.isEmpty()) {
				if (chunk instanceof Ihdr chk)
					ihdr = Optional.of(chk);
				else
					throw new IllegalArgumentException();
			} else if (chunk instanceof Ihdr)
				throw new IllegalArgumentException();
			else if (chunk instanceof Idat chk) {
				if (afterIdats.isEmpty())
					idats.add(chk);
				else
					throw new IllegalArgumentException();
			} else if (idats.isEmpty())
				beforeIdats.add(chunk);
			else
				afterIdats.add(chunk);
		}
		if (ihdr.isEmpty() || idats.isEmpty() || !hasIend)
			throw new IllegalArgumentException();
	}
	
	
	/**
	 * Writes the signature and chunks of this PNG file to the specified output file.
	 * @throws NullPointerException if {@code outFile}
	 * or any of this object's fields is {@code null}
	 * @throws IllegalStateException if the current
	 * lists of chunks do not form a valid PNG file
	 * @throws IOException if an I/O exception occurs
	 */
	public void write(File outFile) throws IOException {
		Objects.requireNonNull(outFile);
		try (var out = new BufferedOutputStream(new FileOutputStream(outFile))) {
			write(out);
		}
	}
	
	
	/**
	 * Writes the signature and chunks of this PNG file to the
	 * specified output stream. This does not close the stream.
	 * @throws NullPointerException if {@code out}
	 * or any of this object's fields is {@code null}
	 * @throws IllegalStateException if the current
	 * lists of chunks do not form a valid PNG file
	 * @throws IOException if an I/O exception occurs
	 */
	public void write(OutputStream out) throws IOException {
		Objects.requireNonNull(out);
		if (ihdr.isEmpty() || idats.isEmpty())
			throw new IllegalStateException();
		
		List<Chunk> chunks = new ArrayList<>();
		chunks.add(ihdr.get());
		chunks.addAll(beforeIdats);
		chunks.addAll(idats);
		chunks.addAll(afterIdats);
		chunks.add(Iend.SINGLETON);
		new XngFile(XngFile.Type.PNG, chunks).write(out);
	}
	
}
