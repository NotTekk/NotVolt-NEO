package dev.nottekk.notvolt.utility;

import dev.nottekk.notvolt.services.CreditsService;
import dev.nottekk.notvolt.services.FeatureGateService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LimitsCommand extends ListenerAdapter {
	private final CreditsService credits;
	private final FeatureGateService featureGateService;

	public LimitsCommand(CreditsService credits, FeatureGateService featureGateService) {
		this.credits = credits;
		this.featureGateService = featureGateService;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (!"limits".equals(event.getName()) || event.getGuild() == null) return;
		String guildId = event.getGuild().getId();
		int remaining = credits.getRemaining(guildId);
		String tier = featureGateService.getTier(guildId).name();
		event.reply("Tier: " + tier + " | Image credits left today: " + remaining).setEphemeral(true).queue();
	}
}
