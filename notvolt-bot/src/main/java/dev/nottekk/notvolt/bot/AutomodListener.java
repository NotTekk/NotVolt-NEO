package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.services.GuildConfigService;
import dev.nottekk.notvolt.services.automod.AutomodRule;
import dev.nottekk.notvolt.services.automod.AutomodRuleRepository;
import dev.nottekk.notvolt.services.automod.AutomodService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;

public class AutomodListener extends ListenerAdapter {
	private final AutomodService automodService;
	private final GuildConfigService guildConfigService;
	private final RedisReasonStore reasonStore;
	private final AutomodRuleRepository ruleRepository;

	public AutomodListener(AutomodService automodService, GuildConfigService guildConfigService, RedisReasonStore reasonStore, AutomodRuleRepository ruleRepository) {
		this.automodService = automodService;
		this.guildConfigService = guildConfigService;
		this.reasonStore = reasonStore;
		this.ruleRepository = ruleRepository;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromGuild()) return;
		if (event.getAuthor().isBot()) return;
		Member member = event.getMember();
		if (member == null) return;
		var rules = loadRules(event.getGuild().getId());
		var roleIds = member.getRoles().stream().map(r -> r.getId()).collect(java.util.stream.Collectors.toSet());
		var decision = automodService.evaluate(
			event.getGuild().getId(),
			event.getChannel().getId(),
			event.getAuthor().getId(),
			roleIds,
			event.getMessage().getContentRaw(),
			rules
		);
		if (decision.blocked()) {
			reasonStore.put(event.getAuthor().getId(), event.getMessageId(), decision.reason(), Duration.ofMinutes(10));
			if (decision.action() == AutomodRule.Action.DELETE && member.hasPermission(Permission.MESSAGE_MANAGE)) {
				event.getMessage().delete().queue();
			}
			if (decision.action() == AutomodRule.Action.TIMEOUT && member.hasPermission(Permission.MODERATE_MEMBERS)) {
				event.getGuild().timeoutFor(member, java.time.Duration.ofMinutes(Math.max(1, decision.timeoutMinutes()))).reason(decision.reason()).queue();
			}
			if (decision.action() == AutomodRule.Action.WARN) {
				event.getChannel().sendMessage("Please mind the rules.").queue();
			}
		}
	}

	private java.util.List<AutomodRule> loadRules(String guildId) {
		java.util.List<AutomodRule> persisted = ruleRepository.findAll(guildId);
		if (!persisted.isEmpty()) return persisted;
		java.util.List<AutomodRule> rules = new java.util.ArrayList<>();
		var cfg = guildConfigService.getOrCreate(guildId).getValues();
		String mentionCap = cfg.get("automod.mentionCap");
		if (mentionCap != null) {
			rules.add(new AutomodRule().setType(AutomodRule.Type.MENTION_CAP).setThreshold(Integer.parseInt(mentionCap)).setAction(AutomodRule.Action.DELETE));
		}
		String linkCap = cfg.get("automod.linkCap");
		if (linkCap != null) {
			rules.add(new AutomodRule().setType(AutomodRule.Type.LINK_CAP).setThreshold(Integer.parseInt(linkCap)).setAction(AutomodRule.Action.DELETE));
		}
		String regex = cfg.get("automod.regex");
		if (regex != null && !regex.isBlank()) {
			rules.add(new AutomodRule().setType(AutomodRule.Type.REGEX).setPatterns(java.util.List.of(regex)).setAction(AutomodRule.Action.DELETE));
		}
		return rules;
	}

	public java.util.Optional<String> getLatestReasonForUser(String userId) {
		return reasonStore.getLatestReasonForUser(userId);
	}
}
