package dev.nottekk.notvolt.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.time.OffsetDateTime;

public class ModLogService {
	private final GuildConfigService guildConfigService;
	private final JDA jda;

	public ModLogService(GuildConfigService guildConfigService, JDA jda) {
		this.guildConfigService = guildConfigService;
		this.jda = jda;
	}

	public void log(String guildId, long caseId, String action, String actorId, String targetId, String reason) {
		String channelId = guildConfigService.getOrCreate(guildId).getValues().get("mod.logChannelId");
		if (channelId == null || channelId.isBlank()) return;
		TextChannel channel = jda.getTextChannelById(channelId);
		if (channel == null) return;
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(new Color(0x5865F2));
		eb.setTitle("Case #" + caseId + " - " + action.toUpperCase());
		eb.addField("Actor", "<@" + actorId + ">", true);
		eb.addField("Target", "<@" + targetId + ">", true);
		if (reason != null && !reason.isBlank()) eb.addField("Reason", reason, false);
		eb.setTimestamp(OffsetDateTime.now());
		channel.sendMessageEmbeds(eb.build()).queue();
	}
}
