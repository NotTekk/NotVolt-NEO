package dev.nottekk.notvolt.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MeControllerTest {
	@Autowired
	MockMvc mvc;

	@Test
	void meRequiresAuth() throws Exception {
		mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
	}

	// TODO: add test with security mocking to assert 200 and body shape when authenticated
}
