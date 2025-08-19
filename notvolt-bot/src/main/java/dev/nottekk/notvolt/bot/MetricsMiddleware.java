package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class MetricsMiddleware extends ListenerAdapter {
	private static final Logger log = LoggerFactory.getLogger("commands");
	private final AnalyticsService analytics;

	public MetricsMiddleware(AnalyticsService analytics) {
		this.analytics = analytics;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		analytics.incCommand();
		Map<String, Object> m = new HashMap<>();
		m.put("event", "command");
		m.put("name", event.getName());
		if (event.getGuild() != null) m.put("guildId", event.getGuild().getId());
		m.put("userId", event.getUser().getId());
		log.info(toJson(m));
	}

	private String toJson(Map<String, Object> m) {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		boolean first = true;
		for (var e : m.entrySet()) {
			if (!first) sb.append(','); first = false;
			sb.append('"').append(e.getKey()).append('"').append(':');
			sb.append('"').append(String.valueOf(e.getValue()).replace("\"","'" )).append('"');
		}
		sb.append('}');
		return sb.toString();
	}
}
