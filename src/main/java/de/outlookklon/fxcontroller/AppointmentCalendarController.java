package de.outlookklon.fxcontroller;

import de.outlookklon.MainApplication;
import de.outlookklon.localization.ObservableResourceFactory;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppointmentCalendarController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentCalendarController.class);

    @FXML
    private Menu fileMenu;

    @FXML
    private MenuItem newAppointment;

    @FXML
    private MenuItem close;

    @FXML
    private ListView mailList;

    @FXML
    private TableView appointmentGrid;

    @FXML
    private TableColumn subjectColumn;

    @FXML
    private TableColumn descriptionColumn;

    @FXML
    private TableColumn dateColumn;

    @FXML
    private TextFlow appointmentInfo;

    @FXML
    public void initialize() {
        ObservableResourceFactory resourceFactory = MainApplication.RESOURCE_FACTORY;
        fileMenu.textProperty().bind(resourceFactory.getStringBinding("Menu_File"));
        newAppointment.textProperty().bind(resourceFactory.getStringBinding("AppointmentCalendarFrame_AddAppointment"));
        close.textProperty().bind(resourceFactory.getStringBinding("Menu_Close"));

        subjectColumn.textProperty().bind(resourceFactory.getStringBinding("Appointment_Subject"));
        descriptionColumn.textProperty().bind(resourceFactory.getStringBinding("Appointment_Description"));
        dateColumn.textProperty().bind(resourceFactory.getStringBinding("Appointment_Date"));
    }

    @FXML
    public void onNewAppointment() {

    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) appointmentGrid.getScene().getWindow();
        stage.close();
    }
}
