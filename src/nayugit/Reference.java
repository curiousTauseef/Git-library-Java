package nayugit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Reference {
	
	public final String name;
	public ObjectId target;
	
	
	
	public Reference(String name) {
		this(name, null);
	}
	
	
	public Reference(String name, ObjectId target) {
		checkName(name);
		this.name = name;
		this.target = target;  // Can be null
	}
	
	
	
	public String toString() {
		return String.format("Reference(name=%s, id=%s)", name, target != null ? target.hexString : "null");
	}
	
	
	
	static void checkName(String name) {
		if (name == null)
			throw new NullPointerException();
		Matcher m = NAME_PATTERN.matcher(name);
		if (!m.matches())
			throw new IllegalArgumentException("Invalid reference name: " + name);
		
		String[] parts = name.split("/", -1);
		if (parts[0].equals("remotes") && (parts[1].equals(".") || parts[1].equals(".."))
				|| parts[parts.length - 1].equals("HEAD"))
			throw new IllegalArgumentException("Invalid reference name");
	}
	
	
	private static final Pattern NAME_PATTERN = Pattern.compile("(heads|remotes/[^/]+|tags)/[A-Za-z0-9_-]+");
	
}