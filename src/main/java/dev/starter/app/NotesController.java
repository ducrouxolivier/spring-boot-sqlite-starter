package dev.starter.app;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class NotesController {

	private static final ResponseEntity<Object> NOT_FOUND =
			ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "not found"));
	private static final ResponseEntity<Object> INVALID =
			ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", "title is required"));

	private final JdbcClient db;

	NotesController(JdbcClient db) {
		this.db = db;
	}

	/** 200 only when the database answers. */
	@GetMapping("/up")
	Map<String, String> up() {
		db.sql("SELECT 1").query(Integer.class).single();
		return Map.of("status", "ok");
	}

	@GetMapping("/api/notes")
	List<Note> index() {
		return db.sql("SELECT id, title, body, created_at FROM notes ORDER BY id").query(Note.class).list();
	}

	@GetMapping("/api/notes/{id}")
	ResponseEntity<Object> show(@PathVariable long id) {
		return find(id).<ResponseEntity<Object>>map(ResponseEntity::ok).orElse(NOT_FOUND);
	}

	@PostMapping("/api/notes")
	ResponseEntity<Object> create(@RequestBody NoteInput input) {
		if (!input.valid()) {
			return INVALID;
		}
		var keys = new GeneratedKeyHolder();
		db.sql("INSERT INTO notes (title, body) VALUES (?, ?)")
				.param(input.title().trim()).param(input.body() == null ? "" : input.body())
				.update(keys);
		long id = keys.getKey().longValue();
		return ResponseEntity.created(URI.create("/api/notes/" + id)).body(find(id).orElseThrow());
	}

	@PutMapping("/api/notes/{id}")
	ResponseEntity<Object> update(@PathVariable long id, @RequestBody NoteInput input) {
		if (!input.valid()) {
			return INVALID;
		}
		int changed = db.sql("UPDATE notes SET title = ?, body = ? WHERE id = ?")
				.param(input.title().trim()).param(input.body() == null ? "" : input.body()).param(id)
				.update();
		return changed == 0 ? NOT_FOUND : ResponseEntity.ok(find(id).orElseThrow());
	}

	@DeleteMapping("/api/notes/{id}")
	ResponseEntity<Object> delete(@PathVariable long id) {
		return db.sql("DELETE FROM notes WHERE id = ?").param(id).update() == 0
				? NOT_FOUND : ResponseEntity.noContent().build();
	}

	private java.util.Optional<Note> find(long id) {
		return db.sql("SELECT id, title, body, created_at FROM notes WHERE id = ?").param(id).query(Note.class).optional();
	}
}
