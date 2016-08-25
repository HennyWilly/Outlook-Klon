package de.outlookklon;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Hauptklasse der Anwendung
 */
@SpringBootApplication
public class Application {

    /**
     * Hier wird das MainFrame erzeugt und angezeigt
     *
     * @param args Komandozeilenparamenter
     */
    public static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class)
                .headless(false)
                .web(false)
                .run(args);
    }
}
