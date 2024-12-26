package be.alb_mar_hen.validators;

import java.util.Optional;

public class NumericValidator {
	public boolean isPositiveOrEqualToZero(int value) {
		return value >= 0;
	}
	
	public boolean isPositiveOrEqualToZero(Optional<Integer> value) {
		return value.isEmpty() || (value.isPresent() && isPositiveOrEqualToZero(value.get()));
	}
	
	public boolean isPositive(int value) {
		return value > 0;
	}
	
	public boolean isPositive(Optional<Integer> value) {
		return value.isEmpty() || (value.isPresent() && isPositive(value.get()));
	}
	
	public boolean isPositive(double value) {
		return value > 0;
	}
}
