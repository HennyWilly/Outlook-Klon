package de.outlookklon.fxcontroller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class AddressBookControllerTest extends ApplicationTest {

    private AddressBookController addressBookController;
    private BorderPane rootPane;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ClassLoader.getSystemResource("fxml/AddressBookLayout.fxml"));
        rootPane = fxmlLoader.load();
        addressBookController = fxmlLoader.getController();
        stage.setScene(new Scene(rootPane));
        stage.show();
    }

    @Test
    public void closeApp_OnCloseClicked() throws Exception {
        assertThat(listWindows().size(), is(1));
        clickOn("#fileMenu").clickOn("#close");
        waitForFxEvents();
        assertThat(listWindows().size(), is(0));
    }
}
