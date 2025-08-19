package dev.nottekk.notvolt.music;

public class Track {
	private String title;
	private String url;
	private long lengthMs;
	private String requesterId;

	public Track() {}
	public Track(String title, String url, long lengthMs, String requesterId) {
		this.title = title;
		this.url = url;
		this.lengthMs = lengthMs;
		this.requesterId = requesterId;
	}
	public String getTitle() { return title; }
	public String getUrl() { return url; }
	public long getLengthMs() { return lengthMs; }
	public String getRequesterId() { return requesterId; }
}
