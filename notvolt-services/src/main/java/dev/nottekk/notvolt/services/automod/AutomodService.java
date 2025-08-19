package dev.nottekk.notvolt.services.automod;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class AutomodService {
	public record Decision(boolean blocked, String reason, AutomodRule.Action action, int timeoutMinutes) {}

	public Decision evaluate(String guildId, String channelId, String authorId, Set<String> authorRoleIds, String content, List<AutomodRule> rules) {
		for (AutomodRule rule : rules) {
			if (rule.getExemptChannelIds() != null && rule.getExemptChannelIds().contains(channelId)) continue;
			if (rule.getExemptRoleIds() != null && authorRoleIds != null && authorRoleIds.stream().anyMatch(r -> rule.getExemptRoleIds().contains(r))) continue;
			switch (rule.getType()) {
				case REGEX -> {
					if (rule.getPatterns() != null) {
						for (String p : rule.getPatterns()) {
							if (Pattern.compile(p, Pattern.CASE_INSENSITIVE).matcher(content).find()) {
								return new Decision(true, "Matched regex: " + p, rule.getAction(), rule.getAction() == AutomodRule.Action.TIMEOUT ? Math.max(1, rule.getThreshold()) : 0);
							}
						}
					}
				}
				case WORDLIST -> {
					if (rule.getPatterns() != null) {
						for (String w : rule.getPatterns()) {
							if (content.toLowerCase().contains(w.toLowerCase())) {
								return new Decision(true, "Contains word: " + w, rule.getAction(), rule.getAction() == AutomodRule.Action.TIMEOUT ? Math.max(1, rule.getThreshold()) : 0);
							}
						}
					}
				}
				case LINK_CAP -> {
					int links = countLinks(content);
					if (links > rule.getThreshold()) {
						return new Decision(true, "Too many links: " + links + "/" + rule.getThreshold(), rule.getAction(), rule.getAction() == AutomodRule.Action.TIMEOUT ? Math.max(1, rule.getThreshold()) : 0);
					}
				}
				case MENTION_CAP -> {
					int mentions = countMentions(content);
					if (mentions > rule.getThreshold()) {
						return new Decision(true, "Too many mentions: " + mentions + "/" + rule.getThreshold(), rule.getAction(), rule.getAction() == AutomodRule.Action.TIMEOUT ? Math.max(1, rule.getThreshold()) : 0);
					}
				}
			}
		}
		return new Decision(false, null, null, 0);
	}

	private static int countLinks(String content) {
		if (content == null || content.isEmpty()) return 0;
		var m = Pattern.compile("https?://\\S+").matcher(content);
		int c = 0; while (m.find()) c++; return c;
	}

	private static int countMentions(String content) {
		if (content == null || content.isEmpty()) return 0;
		var m = Pattern.compile("<@!?\\d+>").matcher(content);
		int c = 0; while (m.find()) c++; return c;
	}
}
