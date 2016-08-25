package de.outlookklon.config;

import de.outlookklon.gui.AccountManagementFrame;
import de.outlookklon.gui.MainFrame;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Frames {

    @Bean
    public MainFrame getMainFrame() {
        return new MainFrame();
    }

    @Bean
    public AccountManagementFrame getAccountManagementFrame() {
        return new AccountManagementFrame();
    }
}
