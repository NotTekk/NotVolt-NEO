package dev.nottekk.notvolt.services;

public interface PostGateway {
	void post(String guildId, String channelId, String content);
}
