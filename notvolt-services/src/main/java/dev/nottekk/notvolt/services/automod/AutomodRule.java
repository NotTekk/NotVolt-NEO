package dev.nottekk.notvolt.services.automod;

import java.util.List;
import java.util.Set;

public class AutomodRule {
	public enum Type { REGEX, LINK_CAP, MENTION_CAP, WORDLIST }
	public enum Action { WARN, DELETE, TIMEOUT }

	private Type type;
	private Action action;
	private int threshold; // used for caps/timeouts; for TIMEOUT, represents minutes
	private List<String> patterns; // for REGEX or WORDLIST
	private Set<String> exemptRoleIds;
	private Set<String> exemptChannelIds;

	public Type getType() { return type; }
	public AutomodRule setType(Type type) { this.type = type; return this; }
	public Action getAction() { return action; }
	public AutomodRule setAction(Action action) { this.action = action; return this; }
	public int getThreshold() { return threshold; }
	public AutomodRule setThreshold(int threshold) { this.threshold = threshold; return this; }
	public List<String> getPatterns() { return patterns; }
	public AutomodRule setPatterns(List<String> patterns) { this.patterns = patterns; return this; }
	public Set<String> getExemptRoleIds() { return exemptRoleIds; }
	public AutomodRule setExemptRoleIds(Set<String> exemptRoleIds) { this.exemptRoleIds = exemptRoleIds; return this; }
	public Set<String> getExemptChannelIds() { return exemptChannelIds; }
	public AutomodRule setExemptChannelIds(Set<String> exemptChannelIds) { this.exemptChannelIds = exemptChannelIds; return this; }
}
