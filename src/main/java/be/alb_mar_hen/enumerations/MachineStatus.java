package be.alb_mar_hen.enumerations;

public enum MachineStatus {
	OK,
	IN_MAINTENANCE,
	NEED_MAINTENANCE,
	TO_BE_REPLACED;
	
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
	        default:
	            return TO_BE_REPLACED; // Comportement par défaut
	    }
	}
}