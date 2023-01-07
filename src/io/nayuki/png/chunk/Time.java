/* 
 * PNG library (Java)
 * 
 * Copyright (c) Project Nayuki
 * MIT License. See readme file.
 * https://www.nayuki.io/page/png-library
 */

package io.nayuki.png.chunk;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import io.nayuki.png.Chunk;


/**
 * A last-modification time (tIME) chunk. This gives the time
 * of the last image modification. Instances are immutable.
 * @see https://www.w3.org/TR/2003/REC-PNG-20031110/#11tIME
 */
public record Time(
		int year,
		int month,
		int day,
		int hour,
		int minute,
		int second)
	implements Chunk {
	
	
	static final String TYPE = "tIME";
	
	
	/*---- Constructor ----*/
	
	public Time {
		if (!(0 <= year && year <= Short.MAX_VALUE))
			throw new IllegalArgumentException("Year out of range");
		if (!(1 <= month && month <= 12))
			throw new IllegalArgumentException("Month out of range");
		if (!(1 <= day && day <= 31))
			throw new IllegalArgumentException("Day out of range");
		if (!(0 <= hour && hour <= 23))
			throw new IllegalArgumentException("Hour out of range");
		if (!(0 <= minute && minute <= 59))
			throw new IllegalArgumentException("Minute out of range");
		if (!(0 <= second && second <= 60))
			throw new IllegalArgumentException("Second out of range");
	}
	
	
	public static Time read(DataInput in) throws IOException {
		Objects.requireNonNull(in);
		int year = in.readUnsignedShort();
		int month = in.readUnsignedByte();
		int day = in.readUnsignedByte();
		int hour = in.readUnsignedByte();
		int minute = in.readUnsignedByte();
		int second = in.readUnsignedByte();
		return new Time(year, month, day, hour, minute, second);
	}
	
	
	/*---- Methods ----*/
	
	@Override public String getType() {
		return TYPE;
	}
	
	
	@Override public int getDataLength() {
		return 7;
	}
	
	
	@Override public void writeData(DataOutput out) throws IOException {
		out.writeShort(year);
		out.writeByte(month);
		out.writeByte(day);
		out.writeByte(hour);
		out.writeByte(minute);
		out.writeByte(second);
	}
	
}
