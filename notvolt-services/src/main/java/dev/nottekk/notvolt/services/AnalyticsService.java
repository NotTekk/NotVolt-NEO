package dev.nottekk.notvolt.services;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnalyticsService {
	private final Map<LocalDate, Long> commandsUsed = new ConcurrentHashMap<>();
	private final Map<LocalDate, Long> errors = new ConcurrentHashMap<>();
	private final Map<LocalDate, Long> cases = new ConcurrentHashMap<>();

	public void incCommand() { commandsUsed.merge(LocalDate.now(), 1L, Long::sum); }
	public void incError() { errors.merge(LocalDate.now(), 1L, Long::sum); }
	public void incCase() { cases.merge(LocalDate.now(), 1L, Long::sum); }

	public Summary summary() {
		long cmds = commandsUsed.getOrDefault(LocalDate.now(), 0L);
		long errs = errors.getOrDefault(LocalDate.now(), 0L);
		long cs = cases.getOrDefault(LocalDate.now(), 0L);
		return new Summary(cmds, errs, cs);
	}

	public record Summary(long commandsUsed, long errors, long cases) {}
}
