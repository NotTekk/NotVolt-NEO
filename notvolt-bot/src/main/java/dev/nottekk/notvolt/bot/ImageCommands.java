package dev.nottekk.notvolt.bot;

import dev.nottekk.notvolt.persistence.entity.ImageJobEntity;
import dev.nottekk.notvolt.services.ImageJobService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class ImageCommands extends ListenerAdapter {
	private final ImageJobService jobs;

	public ImageCommands(ImageJobService jobs) { this.jobs = jobs; }

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		String name = event.getName();
		String guildId = event.getGuild().getId();
		String userId = event.getUser().getId();
		try {
			switch (name) {
				case "img" -> handleImg(event, guildId, userId);
			}
		} catch (IllegalStateException quota) {
			event.reply("Out of image credits.").setEphemeral(true).queue();
		}
	}

	private void handleImg(SlashCommandInteractionEvent event, String guildId, String userId) {
		String sub = event.getSubcommandName();
		Map<String, Object> params = new HashMap<>();
		ImageJobEntity.Type type;
		int cost = 1;
		switch (sub) {
			case "gen" -> { type = ImageJobEntity.Type.TEXT2IMG; params.put("prompt", event.getOption("prompt").getAsString()); }
			case "upscale" -> { type = ImageJobEntity.Type.UPSCALE; params.put("attachment", event.getOption("attachment").getAsAttachment().getUrl()); cost = 2; }
			case "meme" -> { type = ImageJobEntity.Type.MEME; params.put("template", event.getOption("template").getAsString()); params.put("text", event.getOption("text").getAsString()); }
			default -> { return; }
		}
		ImageJobEntity job = jobs.enqueue(guildId, userId, type, params, cost);
		event.reply("Job #"+job.getId()+" queued...").queue(hook -> {
			var sent = hook.retrieveOriginal().complete();
			new Thread(() -> {
				ImageJobEntity finished = jobs.run(job);
				if (finished.getStatus() == ImageJobEntity.Status.DONE && finished.getOutputUrl() != null) {
					event.getHook().editOriginal("Job #"+finished.getId()+" done:").setAttachments(new Message.MentionedUsers()).queue();
					try { event.getChannel().sendMessage(finished.getOutputUrl()).queue(); } catch (Exception ignored) {}
				} else {
					event.getHook().editOriginal("Job #"+finished.getId()+" failed.").queue();
				}
			}).start();
		});
	}
}
