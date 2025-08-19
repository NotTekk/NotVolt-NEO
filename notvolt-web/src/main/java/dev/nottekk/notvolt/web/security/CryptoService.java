package dev.nottekk.notvolt.web.security;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {
	private static final String ALG = "AES";
	private static final String CIPHER = "AES/GCM/NoPadding";
	private static final int GCM_TAG_BITS = 128;
	private static final int IV_BYTES = 12;
	private final SecretKey key;
	private final SecureRandom random = new SecureRandom();

	public CryptoService() {
		String k = System.getenv("NOTVOLT_CRYPTO_SECRET");
		if (k == null || k.isBlank()) {
			this.key = generateKey();
		} else {
			byte[] bytes;
			try { bytes = Base64.getDecoder().decode(k); } catch (IllegalArgumentException ex) { bytes = k.getBytes(); }
			if (bytes.length < 16) {
				bytes = java.util.Arrays.copyOf(bytes, 16);
			}
			this.key = new SecretKeySpec(bytes.length >= 32 ? java.util.Arrays.copyOf(bytes, 32) : java.util.Arrays.copyOf(bytes, 16), ALG);
		}
	}

	public String encrypt(String plain) {
		if (plain == null) return null;
		try {
			byte[] iv = new byte[IV_BYTES];
			random.nextBytes(iv);
			Cipher cipher = Cipher.getInstance(CIPHER);
			cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
			byte[] ct = cipher.doFinal(plain.getBytes());
			byte[] out = new byte[iv.length + ct.length];
			System.arraycopy(iv, 0, out, 0, iv.length);
			System.arraycopy(ct, 0, out, iv.length, ct.length);
			return Base64.getEncoder().encodeToString(out);
		} catch (Exception e) { return null; }
	}

	public String decrypt(String enc) {
		if (enc == null) return null;
		try {
			byte[] in = Base64.getDecoder().decode(enc);
			byte[] iv = java.util.Arrays.copyOfRange(in, 0, IV_BYTES);
			byte[] ct = java.util.Arrays.copyOfRange(in, IV_BYTES, in.length);
			Cipher cipher = Cipher.getInstance(CIPHER);
			cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
			return new String(cipher.doFinal(ct));
		} catch (Exception e) { return null; }
	}

	private static SecretKey generateKey() {
		try {
			KeyGenerator kg = KeyGenerator.getInstance(ALG);
			kg.init(256);
			return kg.generateKey();
		} catch (Exception e) {
			return new SecretKeySpec(new byte[16], ALG);
		}
	}
}
