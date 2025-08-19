package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.MessageService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashRouter extends ListenerAdapter {
	private MessageService messages;
	public SlashRouter() {}
	public SlashRouter(MessageService messages) { this.messages = messages; }

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		String name = event.getName();
		String guildId = event.getGuild() != null ? event.getGuild().getId() : null;
		if ("ping".equals(name)) {
			String reply = messages != null ? messages.get(guildId, "cmd.ping", new Object[]{}) : "Pong!";
			event.reply(reply).queue();
		}
	}
}
