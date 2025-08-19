package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.nsfw.NsfwService;
import dev.nottekk.notvolt.services.FeatureGateService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

public class NsfwCommands extends ListenerAdapter {
	private final NsfwService nsfwService;
	private final FeatureGateService featureGateService;

	public NsfwCommands(NsfwService nsfwService, FeatureGateService featureGateService) {
		this.nsfwService = nsfwService;
		this.featureGateService = featureGateService;
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		if (!event.isFromGuild()) { event.reply("Not available in DMs.").setEphemeral(true).queue(); return; }
		String name = event.getName();
		switch (name) {
			case "nsfw" -> handleNsfw(event);
		}
	}

	private void handleNsfw(SlashCommandInteractionEvent event) {
		String sub = event.getSubcommandName();
		String guildId = event.getGuild().getId();
		Member m = event.getMember();
		if (m == null) { event.reply("Invalid member.").setEphemeral(true).queue(); return; }
		if ("enable".equalsIgnoreCase(sub)) {
			if (!m.isOwner()) { event.reply("Only guild owner can enable NSFW.").setEphemeral(true).queue(); return; }
			nsfwService.setEnabled(guildId, true);
			event.reply("NSFW enabled.").setEphemeral(true).queue();
			return;
		}
		if ("disable".equalsIgnoreCase(sub)) {
			if (!m.isOwner()) { event.reply("Only guild owner can disable NSFW.").setEphemeral(true).queue(); return; }
			nsfwService.setEnabled(guildId, false);
			event.reply("NSFW disabled.").setEphemeral(true).queue();
			return;
		}
		if ("tags".equalsIgnoreCase(sub)) {
			String op = event.getOption("op").getAsString();
			String tags = event.getOption("tags").getAsString();
			var list = Arrays.asList(tags.split(" "));
			if ("allow".equalsIgnoreCase(op)) { nsfwService.allowTags(guildId, list); event.reply("Tags allowed.").setEphemeral(true).queue(); }
			else if ("deny".equalsIgnoreCase(op)) { nsfwService.denyTags(guildId, list); event.reply("Tags denied.").setEphemeral(true).queue(); }
			return;
		}
		if ("fetch".equalsIgnoreCase(sub)) {
			String tags = event.getOption("tags").getAsString();
			String res = nsfwService.fetch(guildId, event.getChannel(), m, Arrays.asList(tags.split(" ")));
			if (!"ok".equals(res)) { event.reply(res).setEphemeral(true).queue(); }
			else { event.reply("Sent.").setEphemeral(true).queue(); }
			return;
		}
		if ("review".equalsIgnoreCase(sub)) {
			if (!featureGateService.isPremiumPlus(guildId)) { event.reply("Premium+ required.").setEphemeral(true).queue(); return; }
			if (!m.hasPermission(Permission.MODERATE_MEMBERS)) { event.reply("Mods only.").setEphemeral(true).queue(); return; }
			String op = event.getOption("op").getAsString();
			boolean approve = "approve".equalsIgnoreCase(op);
			String msg = nsfwService.review(guildId, approve);
			event.reply(msg).setEphemeral(true).queue();
		}
	}
}
