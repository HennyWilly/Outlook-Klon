package de.outlookklon.config;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for all Path configurations.
 */
@Configuration
public class Paths {

    @Autowired
    @Value("${user.home}")
    private File homeDirectory;

    @Bean(name = "dataFolder")
    public File dataFolder() {
        File dataFolder = new File(homeDirectory, ".outlookklon");
        return createRequiredParentDirectory(dataFolder);
    }

    @Bean(name = "contactFile")
    public File contactFile() {
        return new File(dataFolder(), "Kontakte.json");
    }

    @Bean(name = "appointmentFile")
    public File appointmentFile() {
        return new File(dataFolder(), "Termine.json");
    }

    @Bean(name = "accountFolderPattern")
    public String accountFolderPattern() {
        return FilenameUtils.concat(dataFolder().getAbsolutePath(), "%s");
    }

    @Bean(name = "accountSettingsFilePattern")
    public String accountSettingsFilePattern() {
        return FilenameUtils.concat(accountFolderPattern(), "settings.json");
    }

    private File createRequiredParentDirectory(File file) {
        File directory = file.getParentFile();
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        return file;
    }
}
