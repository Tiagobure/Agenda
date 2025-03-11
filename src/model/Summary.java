package model;

public class Summary {
	private int id;
	private String title;
	private String text;
	private String subject;
	private String talkAbout;
	private String attachment; // Caminho do arquivo ou link
	private int userId;



	

	public Summary(String title, String text, String subject, String talkAbout, int userId) {
		this.title = title;
		this.text = text;
		this.subject = subject;
		this.talkAbout = talkAbout;
		this.userId = userId;
		
	}
	public Summary(String title, String text, String subject, String talkAbout) {
		
		this(title, text, subject, talkAbout, 0);
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
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
	public String getAttachment() {
		return attachment;
	}
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	//public KeyWord(String keyword,String description, String subject, String talkAbout) {
	//	this(keyword, description, subject, talkAbout, 0); // Assume 0 como valor padrão para usuarioId
	//}




}