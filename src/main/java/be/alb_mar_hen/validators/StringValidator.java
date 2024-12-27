package be.alb_mar_hen.validators;

import java.util.Optional;
import java.util.regex.Pattern;

public class StringValidator {
	public boolean matchRegEx(String regEx, String string) {
		return Pattern.matches(regEx, string);
	}
	
	public boolean isLongerOrEqualsThan(String string, int min) {
		return string.length() >= min; 
	}
	
	public boolean isLongerOrEqualsThan(Optional<String> string, int min) {
		return string.isEmpty() || (string.isPresent() && isLongerOrEqualsThan(string.get(), min)); 
	}
	
	public boolean hasValue(String string) {
	    return string != null && !string.isEmpty();
	}


}
