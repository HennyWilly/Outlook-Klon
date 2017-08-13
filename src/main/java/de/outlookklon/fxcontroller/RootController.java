package de.outlookklon.fxcontroller;

import de.outlookklon.MainApplication;
import de.outlookklon.fxcontroller.dialogs.Dialogs;
import de.outlookklon.localization.Localization;
import de.outlookklon.localization.ObservableResourceFactory;
import java.io.IOException;
import java.util.Locale;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    @FXML
    private Menu fileMenu;

    @FXML
    private Menu newMenu;

    @FXML
    private MenuItem newMail;

    @FXML
    private MenuItem newContact;

    @FXML
    private MenuItem newAppointment;

    @FXML
    private MenuItem close;

    @FXML
    private Menu extrasMenu;

    @FXML
    private MenuItem openAddressBook;

    @FXML
    private MenuItem openAppointmentCalendar;

    @FXML
    private Menu languageMenu;

    @FXML
    private MenuItem openAccountSettings;

    @FXML
    private Button receive;

    @FXML
    public void initialize() {
        ObservableResourceFactory resourceFactory = MainApplication.RESOURCE_FACTORY;
        fileMenu.textProperty().bind(resourceFactory.getStringBinding("Menu_File"));
        newMenu.textProperty().bind(resourceFactory.getStringBinding("Menu_New"));
        newMail.textProperty().bind(resourceFactory.getStringBinding("MainFrame_EMail"));
        newContact.textProperty().bind(resourceFactory.getStringBinding("Contact"));
        newAppointment.textProperty().bind(resourceFactory.getStringBinding("Appointment"));
        close.textProperty().bind(resourceFactory.getStringBinding("Menu_Close"));
        extrasMenu.textProperty().bind(resourceFactory.getStringBinding("Menu_Extras"));
        openAddressBook.textProperty().bind(resourceFactory.getStringBinding("AddressBookFrame_Title"));
        openAppointmentCalendar.textProperty().bind(resourceFactory.getStringBinding("Calendar"));
        languageMenu.textProperty().bind(resourceFactory.getStringBinding("Languages"));
        openAccountSettings.textProperty().bind(resourceFactory.getStringBinding("AccountManagementFrame_Title"));
        receive.textProperty().bind(resourceFactory.getStringBinding("MainFrame_Receive"));

        initLanguageMenu();
    }

    private void initLanguageMenu() {
        ToggleGroup toggleGroup = new ToggleGroup();

        Locale currentLocale = Localization.getLocale();
        for (Locale locale : Localization.getLocalizedLocales()) {
            RadioMenuItem languageMenuItem = new RadioMenuItem(locale.getDisplayLanguage());
            languageMenuItem.setToggleGroup(toggleGroup);
            languageMenuItem.setSelected(locale.equals(currentLocale));
            languageMenuItem.setUserData(locale);

            languageMenu.getItems().add(languageMenuItem);
        }

        toggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            RadioMenuItem sender = (RadioMenuItem) newValue;
            if (sender != null) {
                Locale selectedLocale = (Locale) sender.getUserData();
                if (selectedLocale != null) {
                    Locale.setDefault(selectedLocale);
                    MainApplication.RESOURCE_FACTORY.setLanguage(selectedLocale);
                }
            }
        });
    }

    @FXML
    public void onNewMail() {
        openMailWindow();
    }

    @FXML
    public void onNewContact() {
        Dialogs.openNewContactDialog(getWindow());
    }

    @FXML
    public void onNewAppointment() {

    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) getWindow();
        stage.close();
    }

    private Window getWindow() {
        return receive.getScene().getWindow();
    }

    @FXML
    public void onOpenAddressBook() {
        openAddressBookWindow();
    }

    @FXML
    public void onOpenAppointmentCalendar() {
        openAppointmentCalendarWindow();
    }

    @FXML
    public void onOpenAccountSettings() {

    }

    private void openMailWindow() {
        try {
            openWindow("fxml/MailLayout.fxml");
        } catch (IOException ex) {
            LOGGER.error("Could not open mail window", ex);
        }
    }

    private void openAddressBookWindow() {
        try {
            openWindow("fxml/AddressBookLayout.fxml");
        } catch (IOException ex) {
            LOGGER.error("Could not open address book window", ex);
        }
    }

    private void openAppointmentCalendarWindow() {
        try {
            openWindow("fxml/AppointmentCalendarLayout.fxml");
        } catch (IOException ex) {
            LOGGER.error("Could not open appointment calendar window", ex);
        }
    }

    private void openWindow(String resourcePath) throws IOException {
        ObservableResourceFactory resourceFactory = MainApplication.RESOURCE_FACTORY;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ClassLoader.getSystemResource(resourcePath));
        Parent root1 = (Parent) fxmlLoader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        MainApplication.loadIcons(stage.getIcons());
        stage.show();
    }
}
