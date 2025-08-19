package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.moderation.ModerationService;
import dev.nottekk.notvolt.music.MusicGateway;
import dev.nottekk.notvolt.nsfw.NsfwService;
import dev.nottekk.notvolt.integrations.nsfw.E621Client;
import dev.nottekk.notvolt.integrations.nsfw.Rule34Client;
import dev.nottekk.notvolt.persistence.entity.ImageJobEntity;
import dev.nottekk.notvolt.persistence.repo.ImageJobRepository;
import dev.nottekk.notvolt.persistence.repo.FeedRepository;
import dev.nottekk.notvolt.persistence.repo.PollRepository;
import dev.nottekk.notvolt.persistence.repo.TicketRepository;
import dev.nottekk.notvolt.persistence.repo.ReminderRepository;
import dev.nottekk.notvolt.services.*;
import dev.nottekk.notvolt.services.automod.AutomodService;
import dev.nottekk.notvolt.services.playbook.PlaybookService;
import dev.nottekk.notvolt.services.adapters.JpaCaseRepositoryAdapter;
import dev.nottekk.notvolt.services.automod.AutomodRuleRepository;
import dev.nottekk.notvolt.persistence.repo.CaseRepository;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Path;
import dev.nottekk.notvolt.services.MessageService;

@SpringBootApplication
@EnableScheduling
public class BotLauncher extends ListenerAdapter {
	public static void main(String[] args) throws Exception {
		String token = System.getenv("NOTVOLT_TOKEN");
		if (token == null || token.isBlank()) {
			throw new IllegalStateException("NOTVOLT_TOKEN is not set");
		}

		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(BotLauncher.class)
				.headless(true)
				.run(args);

		JDABuilder builder = JDABuilder.createDefault(token)
				.setActivity(Activity.playing("/help"))
				.setStatus(OnlineStatus.ONLINE);

		var jda = builder.build();

		CaseRepository caseRepo = ctx.getBeanFactory().createBean(CaseRepository.class);
		CaseService caseService = new CaseService(new JpaCaseRepositoryAdapter(caseRepo));
		GuildConfigService guildConfigService = new GuildConfigService();
		FeatureGateService featureGateService = new FeatureGateService(guildConfigService);
		PremiumService premiumService = new PremiumService(featureGateService, System.getenv("REDIS_URL"));
		CreditsService creditsService = new CreditsService(featureGateService);
		ModLogService modLogService = new ModLogService(guildConfigService, jda);
		ModerationService moderationService = new ModerationService(caseService, modLogService, jda);
		AutomodService automodService = new AutomodService();
		PlaybookService playbookService = new PlaybookService(jda);
		AutomodRuleRepository ruleRepo = new AutomodRuleRepository(Path.of(".data"));
		AnalyticsService analyticsService = new AnalyticsService();

		String redisUrl = System.getenv("REDIS_URL");
		RedisReasonStore redisStore = new RedisReasonStore(redisUrl);
		AutomodListener automodListener = new AutomodListener(automodService, guildConfigService, redisStore, ruleRepo);

		MusicGateway musicGateway = new MusicGateway(jda, featureGateService);
		musicGateway.connect();
		LyricsService lyricsService = new LyricsService(System.getenv("LYRICS_API_KEY"));

		NsfwService nsfwService = new NsfwService(guildConfigService, featureGateService, new E621Client(System.getenv("USER_AGENT")), new Rule34Client(System.getenv("USER_AGENT")), jda);

		ImageJobRepository imageJobRepo = ctx.getBeanFactory().createBean(ImageJobRepository.class);
		ImageJobService imageJobService = new ImageJobService(imageJobRepo, creditsService, featureGateService, (type, params) -> {
			return switch (type) {
				case TEXT2IMG -> "https://picsum.photos/1024/576?text=" + java.net.URLEncoder.encode((String) params.getOrDefault("prompt", "notvolt"), java.nio.charset.StandardCharsets.UTF_8);
				case UPSCALE -> (String) params.getOrDefault("attachment", "https://picsum.photos/512");
				case MEME -> "https://picsum.photos/seed/meme/800/600";
			};
		});

		FeedRepository feedRepo = ctx.getBeanFactory().createBean(FeedRepository.class);
		PostGateway postGateway = new BotPostGateway(jda);
		FeedService feedService = new FeedService(feedRepo, postGateway);

		TicketRepository ticketRepo = ctx.getBeanFactory().createBean(TicketRepository.class);
		TicketService ticketService = new TicketService(ticketRepo, postGateway);

		PollRepository pollRepo = ctx.getBeanFactory().createBean(PollRepository.class);
		PollService pollService = new PollService(pollRepo);

		ReminderRepository reminderRepo = ctx.getBeanFactory().createBean(ReminderRepository.class);
		ReminderService reminderService = new ReminderService(reminderRepo, postGateway);

		SnippetService snippetService = new SnippetService();
		MessageService messageService = new MessageService(guildConfigService);

		jda.addEventListener(
				new BotLauncher(), new SlashRouter(messageService),
				new MetricsMiddleware(analyticsService),
				new ModerationCommands(moderationService),
				automodListener, new WhyCommand(automodListener), new AutomodSlashCommands(ruleRepo, playbookService),
				new MusicCommands(musicGateway, featureGateService, lyricsService),
				new NsfwCommands(nsfwService, featureGateService),
				new ImageCommands(imageJobService),
				new LimitsCommand(creditsService, featureGateService),
				new FeedCommands(feedService, featureGateService),
				new TicketCommands(ticketService),
				new PollCommands(pollService),
				new ReminderCommands(reminderService),
				new SnippetCommands(snippetService),
				new PremiumCommands(premiumService, featureGateService)
		);
	}

	@Override
	public void onReady(ReadyEvent event) {
		event.getJDA().updateCommands().addCommands(
				Commands.slash("ping", "Latency check"),
				Commands.slash("help", "Show help"),
				Commands.slash("whois", "Show info about the current user"),
				Commands.slash("ban", "Ban a user").addOption(OptionType.USER, "user", "Target user", true).addOption(OptionType.STRING, "reason", "Reason", false),
				Commands.slash("kick", "Kick a user").addOption(OptionType.USER, "user", "Target user", true).addOption(OptionType.STRING, "reason", "Reason", false),
				Commands.slash("timeout", "Timeout a user").addOption(OptionType.USER, "user", "Target user", true).addOption(OptionType.INTEGER, "seconds", "Duration seconds", true).addOption(OptionType.STRING, "reason", "Reason", false),
				Commands.slash("unban", "Unban a user").addOption(OptionType.STRING, "userId", "User ID", true).addOption(OptionType.STRING, "reason", "Reason", false),
				Commands.slash("purge", "Delete recent messages").addOption(OptionType.INTEGER, "count", "Count (1-100)", true).addOption(OptionType.STRING, "reason", "Reason", false),
				Commands.slash("slowmode", "Set channel slowmode").addOption(OptionType.INTEGER, "seconds", "Seconds", true).addOption(OptionType.STRING, "reason", "Reason", false),
				Commands.slash("lock", "Lock the channel").addOption(OptionType.STRING, "reason", "Reason", false),
				Commands.slash("warn", "Warn a user").addOption(OptionType.USER, "user", "Target user", true).addOption(OptionType.STRING, "reason", "Reason", false),
				Commands.slash("case", "Show a moderation case").addOption(OptionType.INTEGER, "id", "Case id", true),
				Commands.slash("why", "Explain why your last message was blocked"),
				Commands.slash("automod", "Manage automod rules")
					.addSubcommands(new SubcommandData("rule", "Add or remove a rule")
							.addOption(OptionType.STRING, "action", "add or rm", true)
							.addOption(OptionType.STRING, "type", "Rule type", false)
							.addOption(OptionType.INTEGER, "threshold", "Threshold", false)
							.addOption(OptionType.STRING, "patterns", "Comma words or regex", false)
							.addOption(OptionType.STRING, "ruleAction", "WARN/DELETE/TIMEOUT", false)
					),
				Commands.slash("playbook", "Apply or revert playbooks")
					.addSubcommands(new SubcommandData("apply", "Apply a playbook")
							.addOption(OptionType.STRING, "type", "RAID_SHIELD/SCAM_SWEEP/DRAMA_COOLDOWN", true))
					.addSubcommands(new SubcommandData("revert", "Revert last playbook")),

				// Music commands
				Commands.slash("play", "Play a track").addOption(OptionType.STRING, "query", "URL or search", true),
				Commands.slash("skip", "Skip current track"),
				Commands.slash("pause", "Pause playback"),
				Commands.slash("resume", "Resume playback"),
				Commands.slash("queue", "Show queue"),
				Commands.slash("remove", "Remove from queue").addOption(OptionType.INTEGER, "index", "Position in queue (1-based)", true),
				Commands.slash("move", "Move item in queue").addOption(OptionType.INTEGER, "from", "From position", true).addOption(OptionType.INTEGER, "to", "To position", true),
				Commands.slash("shuffle", "Shuffle the queue"),
				Commands.slash("loop", "Loop track or queue").addOption(OptionType.STRING, "mode", "off/track/queue", true),
				Commands.slash("bassboost", "Apply bass boost (Premium)"),
				Commands.slash("nightcore", "Apply nightcore (Premium+)"),
				Commands.slash("eq", "Apply EQ preset (Premium)"),
				Commands.slash("lyrics", "Fetch lyrics for a query").addOption(OptionType.STRING, "query", "Artist - Title", true),

				// NSFW commands
				Commands.slash("nsfw", "NSFW module")
					.addSubcommands(new SubcommandData("enable", "Enable NSFW (owner only)"))
					.addSubcommands(new SubcommandData("disable", "Disable NSFW (owner only)"))
					.addSubcommands(new SubcommandData("tags", "Allow or deny tags")
							.addOption(OptionType.STRING, "op", "allow or deny", true)
							.addOption(OptionType.STRING, "tags", "Space-separated tags", true))
					.addSubcommands(new SubcommandData("fetch", "Fetch content by tags").addOption(OptionType.STRING, "tags", "Space-separated tags", true))
					.addSubcommands(new SubcommandData("review", "Approve or reject pending items (Premium+ mods)")
							.addOption(OptionType.STRING, "op", "approve or reject", true)),

				// Image commands and limits
				Commands.slash("img", "Image jobs")
					.addSubcommands(new SubcommandData("gen", "Generate from text").addOption(OptionType.STRING, "prompt", "Prompt", true))
					.addSubcommands(new SubcommandData("upscale", "Upscale an image").addOption(OptionType.ATTACHMENT, "attachment", "Image", true))
					.addSubcommands(new SubcommandData("meme", "Meme template").addOption(OptionType.STRING, "template", "Template", true).addOption(OptionType.STRING, "text", "Text", true)),
				Commands.slash("limits", "Show remaining limits"),

				// Feed commands
				Commands.slash("feed", "Manage content ingests")
					.addSubcommands(new SubcommandData("add", "Add a feed")
							.addOption(OptionType.STRING, "type", "RSS/YT/TWITCH/REDDIT", true)
							.addOption(OptionType.STRING, "url", "Feed URL", true)
							.addOption(OptionType.CHANNEL, "channel", "Destination channel", true))
					.addSubcommands(new SubcommandData("rm", "Remove a feed").addOption(OptionType.INTEGER, "id", "Feed id", true))
					.addSubcommands(new SubcommandData("list", "List feeds")),

				// Ticket
				Commands.slash("report", "Open a report (modal stub)").addOption(OptionType.STRING, "text", "Describe the issue", false),
				Commands.slash("ticket", "Ticket management")
					.addSubcommands(new SubcommandData("open", "Open a ticket"))
					.addSubcommands(new SubcommandData("close", "Close a ticket").addOption(OptionType.INTEGER, "id", "Ticket id", true))
					.addSubcommands(new SubcommandData("transcript", "Get transcript").addOption(OptionType.INTEGER, "id", "Ticket id", true)),

				// Poll
				Commands.slash("poll", "Create or close a poll")
					.addSubcommands(new SubcommandData("create", "Create a poll")
							.addOption(OptionType.STRING, "question", "Question", true)
							.addOption(OptionType.STRING, "options", "Options separated by |", true)
							.addOption(OptionType.BOOLEAN, "anonymous", "Anonymous?", false))
					.addSubcommands(new SubcommandData("close", "Close a poll").addOption(OptionType.INTEGER, "id", "Poll id", true)),

				// Reminders
				Commands.slash("remind", "Schedule a reminder")
					.addSubcommands(new SubcommandData("in", "Remind in minutes").addOption(OptionType.INTEGER, "minutes", "Minutes", true).addOption(OptionType.STRING, "text", "Message", true))
					.addSubcommands(new SubcommandData("at", "Remind at ISO time").addOption(OptionType.STRING, "time", "ISO-8601", true).addOption(OptionType.STRING, "text", "Message", true)),

				// Snippets
				Commands.slash("snippet", "Manage canned replies")
					.addSubcommands(new SubcommandData("set", "Set a snippet").addOption(OptionType.STRING, "name", "Name", true).addOption(OptionType.STRING, "content", "Content", true))
					.addSubcommands(new SubcommandData("get", "Get a snippet").addOption(OptionType.STRING, "name", "Name", true))
					.addSubcommands(new SubcommandData("rm", "Remove a snippet").addOption(OptionType.STRING, "name", "Name", true)),
				Commands.slash("premium", "Show current tier and perks")
		).queue();
	}
}
