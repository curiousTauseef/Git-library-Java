package nayugit;

import java.util.Arrays;
import java.util.regex.Pattern;


public final class ObjectId implements Comparable<ObjectId> {
	
	public final String hexString;  // 40 characters of 0-9 and lowercase a-f
	private final byte[] bytes;  // 20 bytes
	
	
	
	public ObjectId(String hexStr) {
		if (hexStr == null)
			throw new NullPointerException();
		if (!HEX_STRING_PATTERN.matcher(hexStr).matches())
			throw new IllegalArgumentException();
		
		bytes = new byte[NUM_BYTES];
		for (int i = 0; i < bytes.length; i++)
			bytes[i] = (byte)Integer.parseInt(hexStr.substring(i * 2, (i + 1) * 2), 16);
		
		char[] chars = hexStr.toCharArray();
		for (int i = 0; i < chars.length; i++) {  // Normalize to lowercase
			if (chars[i] >= 'A' && chars[i] <= 'F')
				chars[i] -= 'A' - 'a';
		}
		hexString = new String(chars);
	}
	
	
	public ObjectId(byte[] bytes) {
		if (bytes == null)
			throw new NullPointerException();
		if (bytes.length != NUM_BYTES)
			throw new IllegalArgumentException();
		
		this.bytes = bytes;
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes)
			sb.append(HEX_DIGITS[(b >>> 4) & 0xF]).append(HEX_DIGITS[b & 0xF]);
		hexString = sb.toString();
	}
	
	
	public ObjectId(byte[] bytes, int off) {
		if (bytes == null)
			throw new NullPointerException();
		if (bytes.length - off < NUM_BYTES)
			throw new IllegalArgumentException();
		
		this.bytes = Arrays.copyOfRange(bytes, off, off + NUM_BYTES);
		StringBuilder sb = new StringBuilder();
		for (byte b : this.bytes)
			sb.append(HEX_DIGITS[(b >>> 4) & 0xF]).append(HEX_DIGITS[b & 0xF]);
		hexString = sb.toString();
	}
	
	
	
	public byte getByte(int index) {
		if (index < 0 || index >= NUM_BYTES)
			throw new IndexOutOfBoundsException();
		return bytes[index];
	}
	
	
	public byte[] getBytes() {
		return bytes.clone();
	}
	
	
	public boolean equals(Object obj) {
		if (!(obj instanceof ObjectId))
			return false;
		return Arrays.equals(bytes, ((ObjectId)obj).bytes);
	}
	
	
	public int hashCode() {
		return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
	
	
	public int compareTo(ObjectId other) {
		return hexString.compareTo(other.hexString);
	}
	
	
	public String toString() {
		return String.format("ObjectId(value=%s)", hexString);
	}
	
	
	
	private static final int NUM_BYTES = 20;
	private static final Pattern HEX_STRING_PATTERN = Pattern.compile("[0-9a-fA-F]{" + (NUM_BYTES * 2) + "}");
	private static char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
	
}
