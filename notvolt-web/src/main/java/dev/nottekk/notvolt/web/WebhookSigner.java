package dev.nottekk.notvolt.web;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class WebhookSigner {
	public static String sign(String secret, String payload) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
			byte[] sig = mac.doFinal(payload.getBytes());
			return Base64.getEncoder().encodeToString(sig);
		} catch (Exception e) { return ""; }
	}
}
