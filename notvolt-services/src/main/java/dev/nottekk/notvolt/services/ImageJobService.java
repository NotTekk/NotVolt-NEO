package dev.nottekk.notvolt.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nottekk.notvolt.persistence.entity.ImageJobEntity;
import dev.nottekk.notvolt.persistence.repo.ImageJobRepository;

import java.time.OffsetDateTime;
import java.util.Map;

public class ImageJobService {
	public interface Provider {
		String generate(ImageJobEntity.Type type, Map<String, Object> params);
	}

	private final ImageJobRepository repo;
	private final CreditsService credits;
	private final FeatureGateService featureGateService;
	private final Provider provider;
	private final ObjectMapper mapper = new ObjectMapper();

	public ImageJobService(ImageJobRepository repo, CreditsService credits, FeatureGateService featureGateService, Provider provider) {
		this.repo = repo;
		this.credits = credits;
		this.featureGateService = featureGateService;
		this.provider = provider;
	}

	public ImageJobEntity enqueue(String guildId, String userId, ImageJobEntity.Type type, Map<String, Object> params, int cost) {
		int priority = switch (featureGateService.getTier(guildId)) {
			case FREE -> 1; case PREMIUM -> 5; case PREMIUM_PLUS -> 10;
		};
		if (!credits.tryDeduct(guildId, cost)) throw new IllegalStateException("Insufficient credits");
		ImageJobEntity e = new ImageJobEntity();
		e.setGuildId(guildId);
		e.setUserId(userId);
		e.setType(type);
		e.setParamsJson(write(params));
		e.setStatus(ImageJobEntity.Status.QUEUED);
		e.setCost(cost);
		e.setPriority(priority);
		e.setCreatedAt(OffsetDateTime.now());
		return repo.save(e);
	}

	public ImageJobEntity run(ImageJobEntity job) {
		job.setStatus(ImageJobEntity.Status.RUNNING);
		repo.save(job);
		try {
			String out = provider.generate(job.getType(), read(job.getParamsJson()));
			job.setOutputUrl(out);
			job.setStatus(ImageJobEntity.Status.DONE);
			job.setCompletedAt(OffsetDateTime.now());
			return repo.save(job);
		} catch (Exception ex) {
			job.setStatus(ImageJobEntity.Status.ERROR);
			job.setCompletedAt(OffsetDateTime.now());
			return repo.save(job);
		}
	}

	private String write(Map<String, Object> map) {
		try { return mapper.writeValueAsString(map); } catch (Exception e) { return "{}"; }
	}
	@SuppressWarnings("unchecked")
	private Map<String, Object> read(String json) {
		try { return mapper.readValue(json, Map.class); } catch (Exception e) { return java.util.Map.of(); }
	}
}
