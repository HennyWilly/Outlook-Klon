package de.outlookklon.fxcontroller.dialogs;

import de.outlookklon.MainApplication;
import de.outlookklon.localization.ObservableResourceFactory;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Dialogs {

    private static final Logger LOGGER = LoggerFactory.getLogger(Dialogs.class);

    private Dialogs() {
        // Private util constructor
    }

    public static boolean openNewContactDialog(Window owner) {
        ObservableResourceFactory resourceFactory = MainApplication.RESOURCE_FACTORY;
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClassLoader.getSystemResource("fxml/dialogs/ContactDialogLayout.fxml"));
            Parent parent = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();

            // TODO Adapt to 'ContactFrame_NewContactFormat', 'ContactFrame_EditContactFormat' and 'ContactFrame_EditContact'.
            dialogStage.titleProperty().bind(resourceFactory.getStringBinding("AddressBookFrame_Menu_NewContact"));

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            Scene scene = new Scene(parent);
            dialogStage.setScene(scene);
            MainApplication.loadIcons(dialogStage.getIcons());

            // Set the person into the controller.
            ContactDialogController controller = loader.getController();

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException ex) {
            LOGGER.error("Error while loading dialog", ex);
            return false;
        }
    }
}
