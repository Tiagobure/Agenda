package model;

import java.time.LocalDateTime;

public class Schedule {
	private int id;
	private String dayWeek;
	private String hour;
	private String subject;
	private String talkAbout;
	private int userId;
    private LocalDateTime dateTime; // Novo campo para data e hora combinadas


	
	public Schedule(String dayWeek, String hour, String subject, String talkAbout, int userId) {
		
		this.dayWeek = dayWeek;
		this.hour = hour;
		this.subject = subject;
		this.talkAbout = talkAbout;
		this.userId = userId;
		 this.dateTime = LocalDateTime.now();
	}

	public Schedule(String dayWeek, String hour, String subject, String talkAbout) {
		this(dayWeek, hour, subject, talkAbout, 0);

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDayWeek() {
		return dayWeek;
	}

	public void setDayWeek(String dayWeek) {
		this.dayWeek = dayWeek;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTalkAbout() {
		return talkAbout;
	}

	public void setTalkAbout(String talkAbout) {
		this.talkAbout = talkAbout;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

	
	
}