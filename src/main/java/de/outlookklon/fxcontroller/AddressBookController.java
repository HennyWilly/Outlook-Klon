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

public class AddressBookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressBookController.class);

    @FXML
    private Menu fileMenu;

    @FXML
    private Menu newMenu;

    @FXML
    private MenuItem newContact;

    @FXML
    private MenuItem newContactList;

    @FXML
    private MenuItem close;

    @FXML
    private ListView contactListList;

    @FXML
    private TableView contactGrid;

    @FXML
    private TableColumn nameColumn;

    @FXML
    private TableColumn mailColumn;

    @FXML
    private TableColumn dutyPhoneColumn;

    @FXML
    private TextFlow contactInfo;

    @FXML
    public void initialize() {
        ObservableResourceFactory resourceFactory = MainApplication.RESOURCE_FACTORY;
        fileMenu.textProperty().bind(resourceFactory.getStringBinding("Menu_File"));
        newMenu.textProperty().bind(resourceFactory.getStringBinding("Menu_New"));
        newContact.textProperty().bind(resourceFactory.getStringBinding("AddressBookFrame_Menu_NewContact"));
        newContactList.textProperty().bind(resourceFactory.getStringBinding("AddressBookFrame_Menu_NewContactList"));
        close.textProperty().bind(resourceFactory.getStringBinding("Menu_Close"));

        nameColumn.textProperty().bind(resourceFactory.getStringBinding("AddressBookFrame_Table_Name"));
        mailColumn.textProperty().bind(resourceFactory.getStringBinding("AddressBookFrame_Table_Mail"));
        dutyPhoneColumn.textProperty().bind(resourceFactory.getStringBinding("AddressBookFrame_Table_DutyPhone"));
    }

    @FXML
    public void onNewContact() {

    }

    @FXML
    public void onNewContactList() {

    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) contactGrid.getScene().getWindow();
        stage.close();
    }
}
