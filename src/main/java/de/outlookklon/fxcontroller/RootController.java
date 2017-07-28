package de.outlookklon.fxcontroller;

import de.outlookklon.MainApplication;
import de.outlookklon.localization.Localization;
import de.outlookklon.localization.ObservableResourceFactory;
import java.util.Locale;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class RootController {

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

    }

    @FXML
    public void onNewContact() {

    }

    @FXML
    public void onNewAppointment() {

    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) receive.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onOpenAddressBook() {

    }

    @FXML
    public void onOpenAppointmentCalendar() {

    }

    @FXML
    public void onOpenAccountSettings() {

    }
}
