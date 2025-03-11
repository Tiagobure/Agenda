package model;

public class KeyWord {
	private int id;
	private String keyword;
	private String description;
	private String subject;
	private String talkAbout;
	private int userId;

	
	
	public KeyWord(String keyword, String description, String subject, String talkAbout, int userId) {
		super();
		this.keyword = keyword;
		this.description = description;
		this.subject = subject;
		this.talkAbout = talkAbout;
		this.userId = userId;
	}
	
	public KeyWord(String keyword,String description, String subject, String talkAbout) {
		this(keyword, description, subject, talkAbout, 0); // Assume 0 como valor padrão para usuarioId
	}
	// Getters e setters...

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	@Override
	public String toString() {
		return "PalavraChave [keyword=" + keyword + ", description=" + description + ", subject=" + subject
				+ ", talkAbout=" + talkAbout + "]";
	}
	
	
	
}