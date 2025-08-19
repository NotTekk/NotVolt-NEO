package dev.nottekk.notvolt.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GuildConfigControllerTest {
	@Autowired
	MockMvc mvc;

	@Test
	void configPutRequiresAuth() throws Exception {
		mvc.perform(put("/api/guilds/123/config").contentType(MediaType.APPLICATION_JSON).content("{\"k\":\"v\"}"))
				.andExpect(status().isUnauthorized());
	}

	// TODO: mock authenticated user and RBAC to assert 403 when lacking permissions and 200 when admin
}
