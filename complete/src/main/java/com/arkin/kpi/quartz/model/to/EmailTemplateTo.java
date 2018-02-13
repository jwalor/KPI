package com.arkin.kpi.quartz.model.to;

public class EmailTemplateTo {
	
	private String to;
	private String subject;
	private String text;
	private UsersDestinationsTo userDestination;
	
	public EmailTemplateTo() {
	}	

	public EmailTemplateTo(String to, String subject, String text, UsersDestinationsTo userDestination) {
		this.to = to;
		this.subject = subject;
		this.text = text;
		this.userDestination = userDestination;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public UsersDestinationsTo getUserDestination() {
		return userDestination;
	}

	public void setUserDestination(UsersDestinationsTo userDestination) {
		this.userDestination = userDestination;
	}	

}
