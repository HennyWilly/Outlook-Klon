package de.outlookklon;

import de.outlookklon.gui.MainFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private MainFrame frame;

    @Override
    public void run(String... args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        SpringApplication.exit(appContext);
                    }
                });
            }
        });
    }
}
