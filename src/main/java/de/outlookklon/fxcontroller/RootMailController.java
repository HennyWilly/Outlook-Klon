package de.outlookklon.fxcontroller;

import de.outlookklon.MainApplication;
import de.outlookklon.localization.ObservableResourceFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RootMailController {

    @FXML
    private TableView emailGrid;

    @FXML
    private TableColumn subjectColumn;

    @FXML
    private TableColumn fromColumn;

    @FXML
    private TableColumn dateColumn;

    @FXML
    public void initialize() {
        ObservableResourceFactory resourceFactory = MainApplication.RESOURCE_FACTORY;
        subjectColumn.textProperty().bind(resourceFactory.getStringBinding("MailFrame_Subject"));
        fromColumn.textProperty().bind(resourceFactory.getStringBinding("MailFrame_From"));
        dateColumn.textProperty().bind(resourceFactory.getStringBinding("Appointment_Date"));
    }
}
