package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.ReminderService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.OffsetDateTime;

public class ReminderCommands extends ListenerAdapter {
	private final ReminderService reminderService;
	public ReminderCommands(ReminderService reminderService) { this.reminderService = reminderService; }

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		if (!"remind".equals(event.getName())) return;
		String sub = event.getSubcommandName();
		String guildId = event.getGuild().getId();
		String channelId = event.getChannel().getId();
		String userId = event.getUser().getId();
		if ("in".equals(sub)) {
			long minutes = event.getOption("minutes").getAsLong();
			String text = event.getOption("text").getAsString();
			reminderService.schedule(guildId, channelId, userId, OffsetDateTime.now().plusMinutes(minutes), text);
			event.reply("Okay, I'll remind you in " + minutes + " minutes.").setEphemeral(true).queue();
		} else if ("at".equals(sub)) {
			String iso = event.getOption("time").getAsString();
			String text = event.getOption("text").getAsString();
			reminderService.schedule(guildId, channelId, userId, OffsetDateTime.parse(iso), text);
			event.reply("Reminder set for " + iso).setEphemeral(true).queue();
		}
	}
}
