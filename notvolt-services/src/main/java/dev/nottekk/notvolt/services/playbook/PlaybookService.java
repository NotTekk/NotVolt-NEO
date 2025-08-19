package dev.nottekk.notvolt.services.playbook;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlaybookService {
	private final JDA jda;
	// guildId -> (channelId -> previous setting snapshot)
	private final Map<String, Map<String, String>> previousState = new HashMap<>();

	public PlaybookService(JDA jda) {
		this.jda = jda;
	}

	public Map<String, String> preview(String guildId, Playbook playbook) {
		Map<String, String> diff = new HashMap<>();
		playbook.getChannelSettings().forEach((channelId, setting) -> {
			TextChannel c = jda.getTextChannelById(channelId);
			if (c != null) diff.put(channelId, setting);
		});
		return diff;
	}

	public void apply(String guildId, Playbook playbook) {
		Map<String, String> prev = new HashMap<>();
		playbook.getChannelSettings().forEach((channelId, setting) -> {
			TextChannel c = jda.getTextChannelById(channelId);
			if (c == null) return;
			// Snapshot
			prev.put(channelId, snapshot(c));
			// Apply
			if (setting.startsWith("slowmode=")) {
				int seconds = Integer.parseInt(setting.substring("slowmode=".length()));
				c.getManager().setSlowmode(seconds).queue();
			} else if (setting.equals("lock=true")) {
				c.getManager().setLocked(true).queue();
			}
		});
		previousState.put(guildId, prev);
	}

	public void revert(String guildId) {
		Map<String, String> prev = previousState.remove(guildId);
		if (prev == null) return;
		prev.forEach((channelId, snapshot) -> {
			TextChannel c = jda.getTextChannelById(channelId);
			if (c == null) return;
			applySnapshot(c, snapshot);
		});
	}

	private static String snapshot(TextChannel c) {
		int slow = c.getSlowmode();
		boolean locked = c.isLocked();
		return "slowmode=" + slow + ";lock=" + locked;
	}

	private static void applySnapshot(TextChannel c, String snap) {
		for (String part : snap.split(";")) {
			if (part.startsWith("slowmode=")) {
				int seconds = Integer.parseInt(part.substring("slowmode=".length()));
				c.getManager().setSlowmode(seconds).queue();
			} else if (part.startsWith("lock=")) {
				boolean locked = Boolean.parseBoolean(part.substring("lock=".length()));
				c.getManager().setLocked(locked).queue();
			}
		}
	}
}
