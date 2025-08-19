package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.automod.AutomodRule;
import dev.nottekk.notvolt.services.automod.AutomodRuleRepository;
import dev.nottekk.notvolt.services.playbook.Playbook;
import dev.nottekk.notvolt.services.playbook.PlaybookService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.nio.file.Path;
import java.util.List;

public class AutomodSlashCommands extends ListenerAdapter {
	private final AutomodRuleRepository ruleRepo;
	private final PlaybookService playbookService;

	public AutomodSlashCommands(AutomodRuleRepository repo, PlaybookService playbookService) {
		this.ruleRepo = repo;
		this.playbookService = playbookService;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		String name = event.getName();
		if (event.getGuild() == null) return;
		switch (name) {
			case "automod" -> handleAutomod(event);
			case "playbook" -> handlePlaybook(event);
		}
	}

	private void handleAutomod(SlashCommandInteractionEvent event) {
		String sub = event.getSubcommandName();
		String guildId = event.getGuild().getId();
		if ("rule".equals(sub)) {
			String action = event.getOption("action").getAsString();
			if ("add".equalsIgnoreCase(action)) {
				String type = event.getOption("type").getAsString();
				int threshold = event.getOption("threshold") != null ? (int) event.getOption("threshold").getAsLong() : 0;
				String patterns = event.getOption("patterns") != null ? event.getOption("patterns").getAsString() : null;
				String ruleAction = event.getOption("ruleAction") != null ? event.getOption("ruleAction").getAsString() : "DELETE";
				AutomodRule r = new AutomodRule()
						.setType(AutomodRule.Type.valueOf(type))
						.setThreshold(threshold)
						.setAction(AutomodRule.Action.valueOf(ruleAction));
				if (patterns != null) r.setPatterns(java.util.Arrays.asList(patterns.split(",")));
				ruleRepo.add(guildId, r);
				event.reply("Rule added.").setEphemeral(true).queue();
			} else if ("rm".equalsIgnoreCase(action)) {
				int index = (int) event.getOption("index").getAsLong();
				ruleRepo.remove(guildId, index);
				event.reply("Rule removed.").setEphemeral(true).queue();
			}
		}
	}

	private void handlePlaybook(SlashCommandInteractionEvent event) {
		String action = event.getSubcommandName();
		String guildId = event.getGuild().getId();
		String type = event.getOption("type").getAsString();
		Playbook.Type pbType = Playbook.Type.valueOf(type);
		Playbook pb = switch (pbType) {
			case RAID_SHIELD -> new Playbook(pbType, java.util.Map.of());
			case SCAM_SWEEP -> new Playbook(pbType, java.util.Map.of());
			case DRAMA_COOLDOWN -> new Playbook(pbType, java.util.Map.of());
		};
		if ("apply".equalsIgnoreCase(action)) {
			var preview = playbookService.preview(guildId, pb);
			event.reply("Applying playbook " + type + " to " + preview.size() + " channels").setEphemeral(true).queue();
			playbookService.apply(guildId, pb);
		} else if ("revert".equalsIgnoreCase(action)) {
			playbookService.revert(guildId);
			event.reply("Reverted playbook").setEphemeral(true).queue();
		}
	}
}
