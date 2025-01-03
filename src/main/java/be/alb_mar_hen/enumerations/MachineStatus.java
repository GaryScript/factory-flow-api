package be.alb_mar_hen.enumerations;

public enum MachineStatus {
	OK,
	IN_MAINTENANCE,
	NEED_MAINTENANCE,
	TO_BE_REPLACED,
	REPLACED,
	NEED_VALIDATION;
	
	public static MachineStatus fromString(String value) {
	    switch (value) {
	        case "1":
	            return OK;
	        case "2":
	            return IN_MAINTENANCE;
	        case "3":
	            return NEED_MAINTENANCE;
	        case "4":
	            return TO_BE_REPLACED;
	        case "5":
	        	return REPLACED;
	        case "6":
	        	return NEED_VALIDATION;
	        default:
	            return TO_BE_REPLACED;
	    }
	}
	
	public static MachineStatus fromDatabaseValue(int value) {
		switch (value) {
		case 1:
			return OK;
		case 2:
			return IN_MAINTENANCE;
		case 3:
			return NEED_MAINTENANCE;
		case 4:
			return TO_BE_REPLACED;
		case 5:
			return REPLACED;
		case 6:
            return NEED_VALIDATION;
		default:
			return TO_BE_REPLACED;
		}
	}
}