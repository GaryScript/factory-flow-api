package be.alb_mar_hen.enumerations;

public enum ZoneColor {
	GREEN,
	ORANGE,
	RED,
	BLACK;
	
	public static ZoneColor fromDatabaseValue(String value) {
        switch (value) {
            case "1":
                return GREEN;
            case "2":
                return ORANGE;
            case "3":
                return RED;
            case "4":
                return BLACK;
            default:
                throw new IllegalArgumentException("Unknown ZoneColor value: " + value);
        }
    }
	
	public static ZoneColor fromDatabaseValue(int value) {
		switch (value) {
		case 1:
			return GREEN;
		case 2:
			return ORANGE;
		case 3:
			return RED;
		case 4:
			return BLACK;
		default:
			throw new IllegalArgumentException("Unknown ZoneColor value: " + value);
		}
	}
}
