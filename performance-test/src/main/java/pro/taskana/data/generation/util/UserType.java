package pro.taskana.data.generation.util;

/**
 * Enum contains supported user types. The types are used to generate the user
 * id.
 * 
 * @author fe
 *
 */
public enum UserType {
	USER("U"), TEAMMANAGER("M");

	private final String identifier;

	private UserType(String identifier) {
		this.identifier = identifier;
	}

	public String getAsString() {
		return identifier;
	}
}
