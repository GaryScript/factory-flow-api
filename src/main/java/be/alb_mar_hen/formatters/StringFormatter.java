package be.alb_mar_hen.formatters;

public class StringFormatter {
    /**
     * Converts the first character of the string to uppercase.
     * @param input the string to format
     * @return the string with the first character in uppercase
     */
    public String firstToUpper(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }
}

