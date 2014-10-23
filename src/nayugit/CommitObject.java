package nayugit;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;


public final class CommitObject extends GitObject {
	
	public ObjectId tree;
	public List<ObjectId> parents;
	public String message;
	
	public String authorName;
	public String authorEmail;
	public int authorTime;
	public int authorTimezone;
	
	public String committerName;
	public String committerEmail;
	public int committerTime;
	public int committerTimezone;
	
	
	
	public CommitObject(byte[] data) throws UnsupportedEncodingException, DataFormatException {
		parents = new ArrayList<ObjectId>();
		
		int index = 0;
		int start;
		String line;
		String[] parts;
		
		for (start = index; data[index] != '\n'; index++);
		line = new String(data, start, index - start, "UTF-8");
		parts = line.split(" ", 2);
		if (!parts[0].equals("tree"))
			throw new DataFormatException("Tree field expected");
		tree = new ObjectId(parts[1]);
		index++;
		
		while (true) {
			for (start = index; data[index] != '\n'; index++);
			line = new String(data, start, index - start, "UTF-8");
			parts = line.split(" ", 2);
			if (!parts[0].equals("parent"))
				break;
			parents.add(new ObjectId(parts[1]));
			index++;
		}
		
		if (!parts[0].equals("author"))
			throw new DataFormatException("Author field expected");
		Matcher m = AUTHORSHIP_PATTERN.matcher(parts[1]);
		if (!m.matches())
			throw new DataFormatException("Invalid author data");
		authorName = m.group(1);
		authorEmail = m.group(2);
		authorTime = Integer.parseInt(m.group(3));
		authorTimezone = Integer.parseInt(m.group(4) + "1") * (Integer.parseInt(m.group(5)) * 60 + Integer.parseInt(m.group(6)));
		index++;
		
		for (start = index; data[index] != '\n'; index++);
		line = new String(data, start, index - start, "UTF-8");
		parts = line.split(" ", 2);
		if (!parts[0].equals("committer"))
			throw new DataFormatException("Committer field expected");
		m = AUTHORSHIP_PATTERN.matcher(parts[1]);
		if (!m.matches())
			throw new DataFormatException("Invalid committer data");
		committerName = m.group(1);
		committerEmail = m.group(2);
		committerTime = Integer.parseInt(m.group(3));
		committerTimezone = Integer.parseInt(m.group(4) + "1") * (Integer.parseInt(m.group(5)) * 60 + Integer.parseInt(m.group(6)));
		index++;
		
		if (data[index] != '\n')
			throw new DataFormatException("Blank line expected");
		index++;
		message = new String(data, index, data.length - index, "UTF-8");
	}
	
	
	
	public byte[] toBytes() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("tree ").append(tree.hexString).append("\n");
			for (ObjectId parent : parents)
				sb.append("parent ").append(parent.hexString).append("\n");
			sb.append(String.format("author %s <%s> %d %s\n", authorName, authorEmail, authorTime, formatTimezone(authorTimezone)));
			sb.append(String.format("committer %s <%s> %d %s\n", committerName, committerEmail, committerTime, formatTimezone(committerTimezone)));
			sb.append("\n").append(message);
			return addHeader("commit", sb.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}
	}
	
	
	public String toString() {
		return String.format("CommitObject(tree=%s)", tree.hexString);
	}
	
	
	
	private static String formatTimezone(int timezone) {
		String sign = timezone >= 0 ? "+" : "-";
		timezone = Math.abs(timezone);
		int hours = timezone / 60;
		int minutes = timezone % 60;
		return String.format("%s%02d%02d", sign, hours, minutes);
	}
	
	
	private static final Pattern AUTHORSHIP_PATTERN = Pattern.compile("(.*?) <([^>]*)> (\\d+) ([+-])(\\d{2})(\\d{2})");
	
}