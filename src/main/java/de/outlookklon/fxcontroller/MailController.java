package de.outlookklon.fxcontroller;

import de.outlookklon.MainApplication;
import de.outlookklon.localization.ObservableResourceFactory;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailController.class);

    @FXML
    private Menu fileMenu;

    @FXML
    private Menu attachMenu;

    @FXML
    private MenuItem attachFile;

    @FXML
    private MenuItem close;

    @FXML
    private Menu optionsMenu;

    @FXML
    private Menu formatMenu;

    @FXML
    private ToggleGroup formatToggleGroup;

    @FXML
    private RadioMenuItem plainFormat;

    @FXML
    private RadioMenuItem htmlFormat;

    @FXML
    private Button send;

    @FXML
    private Button attach;

    @FXML
    private Label mailSenderLabel;

    @FXML
    private Label mailReceiverLabel;

    @FXML
    private Label mailCcLabel;

    @FXML
    private Label mailSubjectLabel;

    @FXML
    private ComboBox mailSender;

    @FXML
    public void initialize() {
        ObservableResourceFactory resourceFactory = MainApplication.RESOURCE_FACTORY;
        fileMenu.textProperty().bind(resourceFactory.getStringBinding("Menu_File"));
        attachMenu.textProperty().bind(resourceFactory.getStringBinding("MailFrame_Attach"));
        attachFile.textProperty().bind(resourceFactory.getStringBinding("MailFrame_AttachFile"));
        close.textProperty().bind(resourceFactory.getStringBinding("Menu_Close"));
        optionsMenu.textProperty().bind(resourceFactory.getStringBinding("Menu_Options"));
        formatMenu.textProperty().bind(resourceFactory.getStringBinding("MailFrame_MailFormat"));
        plainFormat.textProperty().bind(resourceFactory.getStringBinding("MailFrame_PlainText"));

        send.textProperty().bind(resourceFactory.getStringBinding("MailFrame_Send"));
        attach.textProperty().bind(resourceFactory.getStringBinding("MailFrame_Attachment"));

        mailSenderLabel.textProperty().bind(resourceFactory.getStringBinding("MailFrame_From"));
        mailReceiverLabel.textProperty().bind(resourceFactory.getStringBinding("MailFrame_To"));
        mailCcLabel.textProperty().bind(resourceFactory.getStringBinding("MailFrame_Cc"));
        mailSubjectLabel.textProperty().bind(resourceFactory.getStringBinding("MailFrame_Subject"));

        initFormatToggleMenu();
    }

    private void initFormatToggleMenu() {
        formatToggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            RadioMenuItem sender = (RadioMenuItem) newValue;
            if (sender == plainFormat) {
                onPlainFormat();
            } else if (sender == htmlFormat) {
                onHtmlFormat();
            } else if (sender != null) {
                LOGGER.warn("Unknown format RadioMenuItem");
            }
        });
    }

    @FXML
    public void onAttachFile() {

    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) send.getScene().getWindow();
        stage.close();
    }

    public void onPlainFormat() {

    }

    public void onHtmlFormat() {

    }
}
