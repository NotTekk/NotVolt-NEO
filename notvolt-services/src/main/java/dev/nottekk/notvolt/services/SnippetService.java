package dev.nottekk.notvolt.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnippetService {
	private final Map<String, Map<String, String>> guildToSnippets = new ConcurrentHashMap<>();

	public void set(String guildId, String name, String content) {
		guildToSnippets.computeIfAbsent(guildId, g -> new ConcurrentHashMap<>()).put(name.toLowerCase(), content);
	}

	public String get(String guildId, String name) {
		var m = guildToSnippets.get(guildId);
		if (m == null) return null;
		return m.get(name.toLowerCase());
	}

	public void remove(String guildId, String name) {
		var m = guildToSnippets.get(guildId);
		if (m != null) m.remove(name.toLowerCase());
	}

	public String render(String template, Map<String, String> vars) {
		String out = template;
		for (var e : vars.entrySet()) {
			out = out.replace("${" + e.getKey() + "}", e.getValue());
		}
		return out;
	}
}
