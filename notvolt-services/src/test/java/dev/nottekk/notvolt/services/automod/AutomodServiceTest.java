package dev.nottekk.notvolt.services.automod;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class AutomodServiceTest {
	@Test
	void regexMatches() {
		AutomodService svc = new AutomodService();
		AutomodRule rule = new AutomodRule().setType(AutomodRule.Type.REGEX).setPatterns(List.of("scam-domain\\.com")).setAction(AutomodRule.Action.DELETE);
		var d = svc.evaluate("g","c","u", Set.of(), "visit SCAM-domain.com now", List.of(rule));
		assertTrue(d.blocked());
		assertEquals(AutomodRule.Action.DELETE, d.action());
	}

	@Test
	void linkCapBlocks() {
		AutomodService svc = new AutomodService();
		AutomodRule rule = new AutomodRule().setType(AutomodRule.Type.LINK_CAP).setThreshold(1).setAction(AutomodRule.Action.DELETE);
		var d = svc.evaluate("g","c","u", Set.of(), "check https://a.com and http://b.com", List.of(rule));
		assertTrue(d.blocked());
	}

	@Test
	void mentionCapBlocks() {
		AutomodService svc = new AutomodService();
		AutomodRule rule = new AutomodRule().setType(AutomodRule.Type.MENTION_CAP).setThreshold(1).setAction(AutomodRule.Action.DELETE);
		var d = svc.evaluate("g","c","u", Set.of(), "hi <@123> <@456>", List.of(rule));
		assertTrue(d.blocked());
	}
}
