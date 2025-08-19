package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.PollService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.util.Arrays;

public class PollCommands extends ListenerAdapter {
	private final PollService pollService;
	public PollCommands(PollService pollService) { this.pollService = pollService; }

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		if (!"poll".equals(event.getName())) return;
		String sub = event.getSubcommandName();
		String guildId = event.getGuild().getId();
		if ("create".equals(sub)) {
			String question = event.getOption("question").getAsString();
			String opts = event.getOption("options").getAsString();
			boolean anon = event.getOption("anonymous") != null && event.getOption("anonymous").getAsBoolean();
			var poll = pollService.create(guildId, question, Arrays.asList(opts.split("|")), anon, OffsetDateTime.now().plusDays(1));
			event.reply("Poll #"+poll.getId()+" created").queue();
		} else if ("close".equals(sub)) {
			long id = event.getOption("id").getAsLong();
			pollService.close(id);
			event.reply("Poll closed").setEphemeral(true).queue();
		}
	}
}
