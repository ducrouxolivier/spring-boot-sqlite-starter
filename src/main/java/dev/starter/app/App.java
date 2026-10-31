package dev.starter.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	public static void main(String[] args) throws IOException {
		// sqlite-jdbc creates the file but not its directory.
		Path file = Path.of(System.getenv().getOrDefault("DATABASE_PATH", "data/app.db"));
		if (file.getParent() != null) {
			Files.createDirectories(file.getParent());
		}
		SpringApplication.run(App.class, args);
	}
}
