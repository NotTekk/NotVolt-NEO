package dev.nottekk.notvolt.bot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class WhyCommand extends ListenerAdapter {
	private final AutomodListener automodListener;

	public WhyCommand(AutomodListener automodListener) {
		this.automodListener = automodListener;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (!"why".equals(event.getName())) return;
		String userId = event.getUser().getId();
		var reason = automodListener.getLatestReasonForUser(userId);
		if (reason.isPresent()) {
			event.reply("Your message was blocked because: " + reason.get()).setEphemeral(true).queue();
		} else {
			event.reply("No recent blocked message reason found.").setEphemeral(true).queue();
		}
	}
}
