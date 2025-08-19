package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.moderation.ModerationService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModerationCommands extends ListenerAdapter {
	private final ModerationService moderationService;

	public ModerationCommands(ModerationService moderationService) {
		this.moderationService = moderationService;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		String name = event.getName();
		Member member = event.getMember();
		if (member == null || event.getGuild() == null) return;
		String guildId = event.getGuild().getId();
		String actorId = member.getId();
		switch (name) {
			case "ban" -> {
				if (!member.hasPermission(Permission.BAN_MEMBERS)) { event.reply("Missing permission BAN_MEMBERS").setEphemeral(true).queue(); return; }
				String targetId = event.getOption("user").getAsUser().getId();
				String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "";
				var c = moderationService.ban(guildId, actorId, targetId, reason);
				event.reply("Banned. Case #" + c.getId()).queue();
			}
			case "kick" -> {
				if (!member.hasPermission(Permission.KICK_MEMBERS)) { event.reply("Missing permission KICK_MEMBERS").setEphemeral(true).queue(); return; }
				String targetId = event.getOption("user").getAsUser().getId();
				String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "";
				var c = moderationService.kick(guildId, actorId, targetId, reason);
				event.reply("Kicked. Case #" + c.getId()).queue();
			}
			case "timeout" -> {
				if (!member.hasPermission(Permission.MODERATE_MEMBERS)) { event.reply("Missing permission MODERATE_MEMBERS").setEphemeral(true).queue(); return; }
				String targetId = event.getOption("user").getAsUser().getId();
				long seconds = event.getOption("seconds").getAsLong();
				String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "";
				var c = moderationService.timeout(guildId, actorId, targetId, reason, seconds);
				event.reply("Timed out. Case #" + c.getId()).queue();
			}
			case "unban" -> {
				if (!member.hasPermission(Permission.BAN_MEMBERS)) { event.reply("Missing permission BAN_MEMBERS").setEphemeral(true).queue(); return; }
				String targetId = event.getOption("userId").getAsString();
				String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "";
				var c = moderationService.unban(guildId, actorId, targetId, reason);
				event.reply("Unbanned. Case #" + c.getId()).queue();
			}
			case "purge" -> {
				if (!member.hasPermission(Permission.MESSAGE_MANAGE) && !member.hasPermission(Permission.MANAGE_MESSAGES)) { event.reply("Missing permission MANAGE_MESSAGES").setEphemeral(true).queue(); return; }
				int count = (int) event.getOption("count").getAsLong();
				String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "";
				String channelId = event.getChannel().getId();
				var c = moderationService.purge(guildId, actorId, channelId, count, reason);
				event.reply("Purged. Case #" + c.getId()).queue();
			}
			case "slowmode" -> {
				if (!member.hasPermission(Permission.MANAGE_CHANNEL)) { event.reply("Missing permission MANAGE_CHANNEL").setEphemeral(true).queue(); return; }
				int seconds = (int) event.getOption("seconds").getAsLong();
				String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "";
				String channelId = event.getChannel().getId();
				var c = moderationService.slowmode(guildId, actorId, channelId, seconds, reason);
				event.reply("Slowmode set. Case #" + c.getId()).queue();
			}
			case "lock" -> {
				if (!member.hasPermission(Permission.MANAGE_CHANNEL)) { event.reply("Missing permission MANAGE_CHANNEL").setEphemeral(true).queue(); return; }
				String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "";
				String channelId = event.getChannel().getId();
				var c = moderationService.lock(guildId, actorId, channelId, reason);
				event.reply("Locked. Case #" + c.getId()).queue();
			}
			case "warn" -> {
				if (!member.hasPermission(Permission.MODERATE_MEMBERS)) { event.reply("Missing permission MODERATE_MEMBERS").setEphemeral(true).queue(); return; }
				String targetId = event.getOption("user").getAsUser().getId();
				String reason = event.getOption("reason") != null ? event.getOption("reason").getAsString() : "";
				var c = moderationService.warn(guildId, actorId, targetId, reason);
				event.reply("Warned. Case #" + c.getId()).queue();
			}
			case "case" -> {
				long id = event.getOption("id").getAsLong();
				moderationService
						.getClass(); // placeholder no-op to ensure class is referenced
				// For simplicity, we will just echo the id here; the bot could fetch from CaseService via ModerationService exposure
				event.reply("Case #" + id).queue();
			}
		}
	}
}
