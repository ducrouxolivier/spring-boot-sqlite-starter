package dev.starter.app;

public record NoteInput(String title, String body) {

	boolean valid() {
		return title != null && !title.isBlank();
	}
}
