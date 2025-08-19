package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.music.MusicGateway;
import dev.nottekk.notvolt.music.Track;
import dev.nottekk.notvolt.services.FeatureGateService;
import dev.nottekk.notvolt.services.LyricsService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MusicCommands extends ListenerAdapter {
	private final MusicGateway gateway;
	private final FeatureGateService featureGateService;
	private final LyricsService lyricsService;

	public MusicCommands(MusicGateway gateway, FeatureGateService featureGateService, LyricsService lyricsService) {
		this.gateway = gateway;
		this.featureGateService = featureGateService;
		this.lyricsService = lyricsService;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		String name = event.getName();
		String guildId = event.getGuild().getId();
		switch (name) {
			case "play" -> {
				String query = event.getOption("query").getAsString();
				Track t = new Track(query, query, 0L, event.getUser().getId()); // TODO: resolve via lavalink, support scsearch when not URL
				gateway.enqueue(guildId, t);
				event.reply("Queued: " + query).queue();
			}
			case "skip" -> {
				var s = gateway.skip(guildId);
				event.reply(s.isPresent() ? "Skipped." : "Nothing to skip.").setEphemeral(!s.isPresent()).queue();
			}
			case "pause" -> { gateway.pause(guildId); event.reply("Paused.").queue(); }
			case "resume" -> { gateway.resume(guildId); event.reply("Resumed.").queue(); }
			case "queue" -> {
				List<Track> list = gateway.getQueue(guildId).stream().collect(Collectors.toList());
				EmbedBuilder eb = new EmbedBuilder().setTitle("Queue").setColor(new Color(0x5865F2));
				for (int i=0;i<Math.min(list.size(), 10);i++) {
					Track tr = list.get(i);
					eb.addField((i+1) + ". " + (tr.getTitle() == null ? tr.getUrl() : tr.getTitle()), "<@"+tr.getRequesterId()+">", false);
				}
				event.replyEmbeds(eb.build()).queue();
			}
			case "remove" -> {
				int index = (int) event.getOption("index").getAsLong();
				boolean ok = gateway.remove(guildId, index - 1);
				event.reply(ok ? "Removed." : "Invalid index.").setEphemeral(!ok).queue();
			}
			case "move" -> {
				int from = (int) event.getOption("from").getAsLong() - 1;
				int to = (int) event.getOption("to").getAsLong() - 1;
				boolean ok = gateway.move(guildId, from, to);
				event.reply(ok ? "Moved." : "Invalid positions.").setEphemeral(!ok).queue();
			}
			case "shuffle" -> { gateway.shuffle(guildId); event.reply("Shuffled.").queue(); }
			case "loop" -> {
				String mode = event.getOption("mode").getAsString();
				if ("track".equalsIgnoreCase(mode)) { gateway.setLoopTrack(guildId, true); gateway.setLoopQueue(guildId, false); }
				else if ("queue".equalsIgnoreCase(mode)) { gateway.setLoopQueue(guildId, true); gateway.setLoopTrack(guildId, false); }
				else { gateway.setLoopQueue(guildId, false); gateway.setLoopTrack(guildId, false); }
				event.reply("Loop: " + mode).queue();
			}
			case "bassboost" -> {
				if (!featureGateService.isPremium(guildId)) { event.reply("Premium required").setEphemeral(true).queue(); return; }
				gateway.applyBassBoost(guildId); event.reply("Bass boost applied.").queue();
			}
			case "nightcore" -> {
				if (!featureGateService.isPremiumPlus(guildId)) { event.reply("Premium+ required").setEphemeral(true).queue(); return; }
				gateway.applyNightcore(guildId); event.reply("Nightcore applied.").queue();
			}
			case "eq" -> {
				if (!featureGateService.isPremium(guildId)) { event.reply("Premium required").setEphemeral(true).queue(); return; }
				gateway.applyEq(guildId); event.reply("EQ applied.").queue();
			}
			case "lyrics" -> {
				String q = event.getOption("query").getAsString();
				String res = lyricsService.findLyrics(q);
				event.reply(res).setEphemeral(true).queue();
			}
		}
	}
}
