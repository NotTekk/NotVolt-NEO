package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.persistence.entity.FeedEntity;
import dev.nottekk.notvolt.services.FeatureGateService;
import dev.nottekk.notvolt.services.FeedService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class FeedCommands extends ListenerAdapter {
	private final FeedService feedService;
	private final FeatureGateService featureGateService;

	public FeedCommands(FeedService feedService, FeatureGateService featureGateService) {
		this.feedService = feedService;
		this.featureGateService = featureGateService;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		if (!"feed".equals(event.getName())) return;
		if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) { event.reply("Manage Server required").setEphemeral(true).queue(); return; }
		String guildId = event.getGuild().getId();
		String sub = event.getSubcommandName();
		switch (sub) {
			case "add" -> {
				int limit = switch (featureGateService.getTier(guildId)) { case FREE -> 2; case PREMIUM -> 10; case PREMIUM_PLUS -> 1_000; };
				int current = feedService.list(guildId).size();
				if (current >= limit) { event.reply("Feed limit reached for your tier.").setEphemeral(true).queue(); return; }
				String type = event.getOption("type").getAsString();
				String url = event.getOption("url").getAsString();
				String channelId = event.getOption("channel").getAsChannel().getId();
				FeedEntity e = feedService.add(guildId, FeedEntity.Type.valueOf(type), url, channelId, null);
				event.reply("Feed #"+e.getId()+" added.").setEphemeral(true).queue();
			}
			case "rm" -> {
				long id = event.getOption("id").getAsLong();
				feedService.remove(id);
				event.reply("Feed removed.").setEphemeral(true).queue();
			}
			case "list" -> {
				var list = feedService.list(guildId);
				StringBuilder sb = new StringBuilder();
				for (FeedEntity f : list) sb.append("#").append(f.getId()).append(" ").append(f.getType()).append(" ").append(f.getUrl()).append(" -> <#").append(f.getPostChannelId()).append(">\n");
				event.reply(sb.length() == 0 ? "No feeds" : sb.toString()).setEphemeral(true).queue();
			}
		}
	}
}
