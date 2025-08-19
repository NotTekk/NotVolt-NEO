package dev.nottekk.notvolt.services;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageService {
	private final GuildConfigService guildConfigService;

	public MessageService(GuildConfigService guildConfigService) {
		this.guildConfigService = guildConfigService;
	}

	public Locale resolveLocale(String guildId) {
		String loc = guildId == null ? null : guildConfigService.getOrCreate(guildId).getValues().get("i18n.locale");
		if (loc == null || loc.isBlank()) return Locale.ENGLISH;
		try {
			String[] parts = loc.split("[_-]");
			return parts.length == 1 ? new Locale(parts[0]) : new Locale(parts[0], parts[1]);
		} catch (Exception e) {
			return Locale.ENGLISH;
		}
	}

	public String get(String guildId, String key, Object... args) {
		Locale locale = resolveLocale(guildId);
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
			String pattern = bundle.getString(key);
			return args == null || args.length == 0 ? pattern : MessageFormat.format(pattern, args);
		} catch (MissingResourceException e) {
			try {
				ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
				String pattern = bundle.getString(key);
				return args == null || args.length == 0 ? pattern : MessageFormat.format(pattern, args);
			} catch (Exception ignored) {}
		}
		return key;
	}
}
