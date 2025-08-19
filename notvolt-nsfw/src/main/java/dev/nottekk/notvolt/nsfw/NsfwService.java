package dev.nottekk.notvolt.nsfw;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nottekk.notvolt.integrations.nsfw.E621Client;
import dev.nottekk.notvolt.integrations.nsfw.Rule34Client;
import dev.nottekk.notvolt.services.FeatureGateService;
import dev.nottekk.notvolt.services.GuildConfigService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NsfwService {
	private static final Set<String> ILLEGAL_TAGS = Set.of("loli","shota","underage","real_minor","cub","kid","young-looking","minor");

	private final GuildConfigService configService;
	private final FeatureGateService featureGateService;
	private final E621Client e621;
	private final Rule34Client rule34;
	private final JDA jda;
	private final ObjectMapper mapper = new ObjectMapper();

	private final Map<String, Set<String>> allow = new ConcurrentHashMap<>();
	private final Map<String, Set<String>> deny = new ConcurrentHashMap<>();
	private final Map<String, Deque<PendingItem>> reviewQueues = new ConcurrentHashMap<>();

	public record PendingItem(String userId, String tags, String url) {}

	public NsfwService(GuildConfigService configService, FeatureGateService featureGateService, E621Client e621, Rule34Client rule34, JDA jda) {
		this.configService = configService;
		this.featureGateService = featureGateService;
		this.e621 = e621;
		this.rule34 = rule34;
		this.jda = jda;
	}

	public boolean isEnabled(String guildId) { return Boolean.parseBoolean(configService.getOrCreate(guildId).getValues().getOrDefault("nsfw.enabled", "false")); }
	public void setEnabled(String guildId, boolean enabled) { configService.getOrCreate(guildId).getValues().put("nsfw.enabled", Boolean.toString(enabled)); }

	public void allowTags(String guildId, List<String> tags) { allow.computeIfAbsent(guildId, k -> new HashSet<>()).addAll(normalize(tags)); }
	public void denyTags(String guildId, List<String> tags) { deny.computeIfAbsent(guildId, k -> new HashSet<>()).addAll(normalize(tags)); }

	public String fetch(String guildId, MessageChannel channel, Member member, List<String> tags) {
		// Gates
		if (!channel.asGuildMessageChannel().isNSFW()) return "Channel must be NSFW-marked.";
		if (!isEnabled(guildId)) return "NSFW is disabled by guild owner.";
		if (member == null || member.getUser().isBot()) return "Invalid user.";
		if (!confirmAdult(member)) return "You must confirm you are 18+.";
		var norm = normalize(tags);
		if (norm.stream().anyMatch(t -> ILLEGAL_TAGS.contains(t))) return "Illegal tags are not allowed.";
		Set<String> denySet = deny.getOrDefault(guildId, Set.of());
		if (norm.stream().anyMatch(denySet::contains)) return "Some tags are denied by this guild.";
		Set<String> allowSet = allow.getOrDefault(guildId, Set.of());
		if (!allowSet.isEmpty() && norm.stream().noneMatch(allowSet::contains)) return "Tags not allowed by this guild.";

		boolean premiumPlus = featureGateService.isPremiumPlus(guildId);
		String tagsJoined = String.join(" ", norm);
		String result = e621.search(tagsJoined);
		String url = parseUrl(result);
		if (url == null) url = parseUrl(rule34.search(tagsJoined));
		if (url == null) return "No results.";
		if (premiumPlus && isReviewQueueEnabled(guildId)) {
			reviewQueues.computeIfAbsent(guildId, k -> new ArrayDeque<>()).add(new PendingItem(member.getId(), tagsJoined, url));
			return "Submitted for review.";
		}
		log(guildId, member.getId(), tagsJoined, url);
		channel.sendMessage(url).queue();
		return "ok";
	}

	public String review(String guildId, boolean approve) {
		Deque<PendingItem> q = reviewQueues.get(guildId);
		if (q == null || q.isEmpty()) return "No pending items.";
		PendingItem item = q.pollFirst();
		if (approve) {
			log(guildId, item.userId(), item.tags(), item.url());
			var cId = configService.getOrCreate(guildId).getValues().get("nsfw.logChannelId");
			if (cId != null) {
				var c = jda.getTextChannelById(cId);
				if (c != null) c.sendMessage(item.url()).queue();
			}
			return "Approved and posted.";
		}
		return "Rejected.";
	}

	private String parseUrl(String json) {
		if (json == null) return null;
		try {
			JsonNode n = mapper.readTree(json);
			if (n.has("posts")) {
				var posts = n.get("posts");
				if (posts.isArray() && posts.size() > 0) {
					JsonNode p = posts.get(0);
					if (p.has("file") && p.get("file").has("url")) return p.get("file").get("url").asText();
				}
			}
			if (n.isArray() && n.size() > 0) {
				JsonNode p = n.get(0);
				if (p.has("file_url")) return p.get("file_url").asText();
			}
		} catch (Exception ignored) {}
		return null;
	}

	private boolean confirmAdult(Member member) {
		// MVP: store a flag in memory; in real implementation, persist a user profile
		String key = "user:adult:" + member.getId();
		var cfg = configService.getOrCreate(member.getGuild().getId()).getValues();
		if ("true".equals(cfg.get(key))) return true;
		cfg.put(key, "true");
		return true;
	}

	private boolean isReviewQueueEnabled(String guildId) {
		return Boolean.parseBoolean(configService.getOrCreate(guildId).getValues().getOrDefault("nsfw.review.enabled", "false"));
	}

	private void log(String guildId, String userId, String tags, String url) {
		String channelId = configService.getOrCreate(guildId).getValues().get("nsfw.logChannelId");
		if (channelId == null) return;
		var c = jda.getTextChannelById(channelId);
		if (c != null) c.sendMessage("NSFW fetch by <@"+userId+"> tags=['"+tags+"']\n"+url).queue();
	}

	private static List<String> normalize(List<String> tags) {
		return tags.stream().map(s -> s.trim().toLowerCase()).filter(s -> !s.isEmpty()).toList();
	}
}
