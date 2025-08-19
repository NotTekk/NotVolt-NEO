package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.TicketService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicketCommands extends ListenerAdapter {
	private final TicketService ticketService;
	public TicketCommands(TicketService ticketService) { this.ticketService = ticketService; }

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		String name = event.getName();
		String guildId = event.getGuild().getId();
		switch (name) {
			case "report" -> {
				String desc = event.getOption("text") != null ? event.getOption("text").getAsString() : ""; // modal stub
				var t = ticketService.open(guildId, event.getUser().getId(), "report", desc, event.getChannel().getId());
				event.reply("Report opened, ticket #"+t.getId()).setEphemeral(true).queue();
			}
			case "ticket" -> {
				String sub = event.getSubcommandName();
				if ("open".equals(sub)) {
					var t = ticketService.open(guildId, event.getUser().getId(), "general", "", event.getChannel().getId());
					event.reply("Ticket #"+t.getId()+" opened").setEphemeral(true).queue();
				} else if ("close".equals(sub)) {
					long id = event.getOption("id").getAsLong();
					ticketService.close(id);
					event.reply("Ticket #"+id+" closed").setEphemeral(true).queue();
				} else if ("transcript".equals(sub)) {
					long id = event.getOption("id").getAsLong();
					var t = ticketService.generateTranscript(id);
					event.reply(t.getTranscriptUrl() == null ? "No transcript" : t.getTranscriptUrl()).setEphemeral(true).queue();
				}
			}
		}
	}
}
