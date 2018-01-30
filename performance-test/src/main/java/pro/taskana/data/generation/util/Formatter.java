package pro.taskana.data.generation.util;

/**
 * Helper class for string formatting.
 * 
 * @author fe
 *
 */
public class Formatter {

	public static final String PADDING_CHARACTER = "0";
	/**
	 * Converts a number to a {@link String} with a specified length by adding leading
	 * zeros.
	 * 
	 * @param num
	 *            number which should be converted to a string
	 * @param requiredLength
	 *            length of the returned string
	 * @return formatted string
	 */
	public static String format(int num, int requiredLength) {

		String formattedText = Integer.toString(num);
		if (formattedText.length() > requiredLength) {
			throw new IllegalArgumentException(
					"The number " + num + " is longer than the required length: " + requiredLength + "!");
		}
		while (formattedText.length() < requiredLength) {
			formattedText = PADDING_CHARACTER + formattedText;
		}
		return formattedText;
	}

}
