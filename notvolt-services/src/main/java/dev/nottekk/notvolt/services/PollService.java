package dev.nottekk.notvolt.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nottekk.notvolt.persistence.entity.PollEntity;
import dev.nottekk.notvolt.persistence.repo.PollRepository;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PollService {
	public static final class OptionData {
		public String key;
		public String label;
		public int count;
	}
	public static final class PollData {
		public boolean anonymous;
		public List<OptionData> options;
	}

	private final PollRepository repo;
	private final ObjectMapper mapper = new ObjectMapper();
	private final Map<Long, Map<String, String>> votesByPoll = new ConcurrentHashMap<>(); // pollId -> (userId -> key)

	public PollService(PollRepository repo) {
		this.repo = repo;
	}

	public PollEntity create(String guildId, String question, List<String> optionLabels, boolean anonymous, OffsetDateTime closesAt) {
		PollData data = new PollData();
		data.anonymous = anonymous;
		data.options = new ArrayList<>();
		int i = 1;
		for (String label : optionLabels) {
			OptionData od = new OptionData();
			od.key = "opt" + (i++);
			od.label = label;
			od.count = 0;
			data.options.add(od);
		}
		PollEntity e = new PollEntity();
		e.setGuildId(guildId);
		e.setQuestion(question);
		e.setOptionsJson(write(data));
		e.setClosesAt(closesAt);
		return repo.save(e);
	}

	public Optional<PollEntity> find(long id) { return repo.findById(id); }

	public PollEntity vote(long pollId, String userId, String key) {
		PollEntity e = repo.findById(pollId).orElseThrow();
		PollData data = read(e.getOptionsJson());
		if (e.getClosesAt() != null && e.getClosesAt().isBefore(OffsetDateTime.now())) return e; // closed
		Map<String, String> byUser = votesByPoll.computeIfAbsent(pollId, k -> new ConcurrentHashMap<>());
		String prev = byUser.put(userId, key);
		if (prev != null) {
			data.options.stream().filter(o -> o.key.equals(prev)).findFirst().ifPresent(o -> o.count = Math.max(0, o.count - 1));
		}
		data.options.stream().filter(o -> o.key.equals(key)).findFirst().ifPresent(o -> o.count++);
		e.setOptionsJson(write(data));
		return repo.save(e);
	}

	public PollEntity close(long pollId) {
		PollEntity e = repo.findById(pollId).orElseThrow();
		e.setClosesAt(OffsetDateTime.now());
		return repo.save(e);
	}

	public String exportCsv(long pollId) {
		PollEntity e = repo.findById(pollId).orElseThrow();
		PollData data = read(e.getOptionsJson());
		StringBuilder sb = new StringBuilder();
		sb.append("option,label,count\n");
		for (OptionData od : data.options) sb.append(od.key).append(',').append('"').append(od.label.replace("\"","'"))
				.append('"').append(',').append(od.count).append('\n');
		return sb.toString();
	}

	private String write(PollData d) { try { return mapper.writeValueAsString(d); } catch (Exception e) { return "{}"; } }
	private PollData read(String s) { try { return mapper.readValue(s, new TypeReference<PollData>(){}); } catch (Exception e) { return new PollData(); } }
}
