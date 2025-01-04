package be.alb_mar_hen.enumerations;

public enum MaintenanceStatus {
	IN_PROGRESS, 
	DONE,
	PENDING_VALIDATION; 
	
	public static MaintenanceStatus fromString(String status) {
        if (status == null) {
            return null; 
        }
        switch (status) {
            case "IN_PROGRESS":
                return MaintenanceStatus.IN_PROGRESS;
            case "DONE":
                return MaintenanceStatus.DONE;
            case "1":
                return MaintenanceStatus.IN_PROGRESS;  
            case "2":
                return MaintenanceStatus.DONE; 
			case "PENDING_VALIDATION":
				return MaintenanceStatus.PENDING_VALIDATION;
			case "3":
				return MaintenanceStatus.PENDING_VALIDATION;
            default:
                throw new IllegalArgumentException("Unknown value for MaintenanceStatus: " + status);
        }
    }
	
	public static MaintenanceStatus fromDatabaseValue(int value) {
		switch (value) {
		case 1:
			return MaintenanceStatus.IN_PROGRESS;
		case 2:
			return MaintenanceStatus.DONE;
		case 3:
			return MaintenanceStatus.PENDING_VALIDATION;
		default:
			throw new IllegalArgumentException("Unknown value for MaintenanceStatus: " + value);
		}
	}
}
