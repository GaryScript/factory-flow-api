package be.alb_mar_hen.utils;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public class Conversion {
	public static int extractInt(Object attribute) {
        return attribute instanceof BigDecimal ? ((BigDecimal) attribute).intValue() : 0;
    }

    public static double extractDouble(Object attribute) {
        return attribute instanceof BigDecimal ? ((BigDecimal) attribute).doubleValue() : 0.0;
    }

    public static LocalDateTime extractLocalDateTime(Object attribute) {
        if (attribute instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) attribute).toLocalDateTime();
        } else if (attribute instanceof Date) {
            return ((Date) attribute).toLocalDate().atStartOfDay();  // Quand c'est un java.sql.Date
        }
        return null;  // Si l'attribut n'est pas du type attendu
    }
}
