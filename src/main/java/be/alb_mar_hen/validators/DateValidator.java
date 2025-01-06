package be.alb_mar_hen.validators;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class DateValidator {
	public boolean isInPast(LocalDate date) {
		return date.isBefore(LocalDate.now());
	}
	
	public boolean isInPast(LocalDateTime date) {
		return date.isBefore(LocalDateTime.now());
	}
	
	public boolean isInPast(Optional<LocalDateTime> date) {	    
	    return  date.isEmpty() || (date.isPresent() && isInPast(date.get()));
	}

}	
