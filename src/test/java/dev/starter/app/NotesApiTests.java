package dev.starter.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(properties = "spring.datasource.url=jdbc:sqlite::memory:")
class NotesApiTests {

	@Autowired
	WebApplicationContext context;

	private MockMvc mvc() {
		return MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void upAnswers200WhenTheDatabaseIsReachable() throws Exception {
		mvc().perform(get("/up")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ok"));
	}

	@Test
	void aNoteCanBeCreatedReadUpdatedAndDeleted() throws Exception {
		var mvc = mvc();
		var created = mvc.perform(post("/api/notes").contentType(MediaType.APPLICATION_JSON)
				.content("{\"title\":\" First \",\"body\":\"hello\"}"))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("First")).andReturn();
		String id = com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id").toString();

		mvc.perform(put("/api/notes/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Second\"}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Second")).andExpect(jsonPath("$.body").value(""));
		mvc.perform(delete("/api/notes/" + id)).andExpect(status().isNoContent());
		mvc.perform(get("/api/notes/" + id)).andExpect(status().isNotFound());
	}

	@Test
	void aNoteWithoutATitleIsRejectedWith422() throws Exception {
		mvc().perform(post("/api/notes").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"  \"}"))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void anUnknownNoteIsAJson404() throws Exception {
		mvc().perform(get("/api/notes/999999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value("not found"));
	}
}
