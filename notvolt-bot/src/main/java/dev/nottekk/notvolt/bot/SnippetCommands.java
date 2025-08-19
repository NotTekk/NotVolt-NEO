package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.SnippetService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SnippetCommands extends ListenerAdapter {
	private final SnippetService snippets;
	public SnippetCommands(SnippetService snippets) { this.snippets = snippets; }

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		if (!"snippet".equals(event.getName())) return;
		String guildId = event.getGuild().getId();
		String sub = event.getSubcommandName();
		if ("set".equals(sub)) {
			String name = event.getOption("name").getAsString();
			String content = event.getOption("content").getAsString();
			snippets.set(guildId, name, content);
			event.reply("Snippet '"+name+"' set.").setEphemeral(true).queue();
		} else if ("get".equals(sub)) {
			String name = event.getOption("name").getAsString();
			String content = snippets.get(guildId, name);
			event.reply(content == null ? "No such snippet" : content).setEphemeral(true).queue();
		} else if ("rm".equals(sub)) {
			String name = event.getOption("name").getAsString();
			snippets.remove(guildId, name);
			event.reply("Snippet removed.").setEphemeral(true).queue();
		}
	}
}
