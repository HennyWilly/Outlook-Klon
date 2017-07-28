package de.outlookklon;

import de.outlookklon.application.UserException;
import de.outlookklon.localization.ObservableResourceFactory;
import de.outlookklon.view.frames.MainFrame;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Die Hauptklasse der Anwendung.
 */
public final class MainApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApplication.class);

    private static final String RESOURCE_NAME = "OutlookKlon";
    public static final ObservableResourceFactory RESOURCE_FACTORY = new ObservableResourceFactory(RESOURCE_NAME);

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MailClient");
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        loadIcons(primaryStage.getIcons());

        BorderPane rootPane = loadRootLayout();
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadIcons(ObservableList<Image> imageList) {
        imageList.add(new Image(getClass().getResource("mainFrameIcon_16x16.png").toString()));
        imageList.add(new Image(getClass().getResource("mainFrameIcon_24x24.png").toString()));
        imageList.add(new Image(getClass().getResource("mainFrameIcon_32x32.png").toString()));
        imageList.add(new Image(getClass().getResource("mainFrameIcon_64x64.png").toString()));
        imageList.add(new Image(getClass().getResource("mainFrameIcon_128x128.png").toString()));
    }

    @SneakyThrows(IOException.class)
    private BorderPane loadRootLayout() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ClassLoader.getSystemResource("fxml/RootLayout.fxml"));
        return fxmlLoader.load();
    }

    private static void initAndShowGUI() {
        try {
            JFrame mainFrame = new MainFrame();

            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setVisible(true);
        } catch (UserException ex) {
            // Konnte das MainFrame nicht gestartet werden, wird der Swing-Thread auch beendet
            LOGGER.error("Could not start MainFrame", ex);
        }
    }

    /**
     * Hier wird das MainFrame erzeugt und angezeigt
     *
     * @param args Komandozeilenparamenter
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> initAndShowGUI());
        launch(args);
    }
}
