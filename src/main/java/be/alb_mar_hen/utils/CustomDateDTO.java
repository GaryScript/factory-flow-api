package be.alb_mar_hen.utils;

public class CustomDateDTO {
    private int year;
    private int monthValue;
    private int dayOfMonth;
    private int hour;
    private int minute;
    private int second;
    private int nano;

    // Getters et setters
    
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getMonthValue() {
		return monthValue;
	}
	
	public void setMonthValue(int monthValue) {
		this.monthValue = monthValue;
	}
	
	public int getDayOfMonth() {
		return dayOfMonth;
	}
	
	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	
	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public void setMinute(int minute) {
		this.minute = minute;
	}
	
	public int getSecond() {
		return second;
	}
	
	public void setSecond(int second) {
		this.second = second;
	}
	
	public int getNano() {
		return nano;
	}
	
	public void setNano(int nano) {
		this.nano = nano;
	}
	
	@Override
	public String toString() {
		return "CustomDateDTO [year=" + year + ", monthValue=" + monthValue + ", dayOfMonth=" + dayOfMonth + ", hour="
				+ hour + ", minute=" + minute + ", second=" + second + ", nano=" + nano + "]";
	}
}
