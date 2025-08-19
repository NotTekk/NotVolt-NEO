package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.PostGateway;
import net.dv8tion.jda.api.JDA;

public class BotPostGateway implements PostGateway {
	private final JDA jda;
	public BotPostGateway(JDA jda) { this.jda = jda; }
	@Override
	public void post(String guildId, String channelId, String content) {
		var c = jda.getTextChannelById(channelId);
		if (c != null) c.sendMessage(content).queue();
	}
}
