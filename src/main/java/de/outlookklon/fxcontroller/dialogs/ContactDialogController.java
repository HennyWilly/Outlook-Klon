package de.outlookklon.fxcontroller.dialogs;

import de.outlookklon.MainApplication;
import de.outlookklon.localization.ObservableResourceFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;

public class ContactDialogController {

    @FXML
    Label firstNameLabel;

    @FXML
    Label lastNameLabel;

    @FXML
    Label displayNameLabel;

    @FXML
    Label nickNameLabel;

    @FXML
    Label mailLabel;

    @FXML
    Label mail2Label;

    @FXML
    Label dutyPhoneLabel;

    @FXML
    Label privatePhoneLabel;

    @FXML
    Label mobilePhoneLabel;

    @FXML
    TextField firstNameText;

    @FXML
    TextField lastNameText;

    @FXML
    TextField displayNameText;

    @FXML
    TextField nickNameText;

    @FXML
    TextField mailText;

    @FXML
    TextField mail2Text;

    @FXML
    TextField dutyPhoneText;

    @FXML
    TextField privatePhoneText;

    @FXML
    TextField mobilePhoneText;

    @FXML
    public Button ok;

    @FXML
    public Button abort;

    @Getter
    private boolean okClicked = false;

    @FXML
    public void initialize() {
        ObservableResourceFactory resourceFactory = MainApplication.RESOURCE_FACTORY;
        firstNameLabel.textProperty().bind(resourceFactory.getStringBinding("Contact_Forename"));
        lastNameLabel.textProperty().bind(resourceFactory.getStringBinding("Contact_Surname"));
        displayNameLabel.textProperty().bind(resourceFactory.getStringBinding("Account_DisplayName"));
        nickNameLabel.textProperty().bind(resourceFactory.getStringBinding("Contact_Nickname"));
        mailLabel.textProperty().bind(resourceFactory.getStringBinding("Account_MailAddress"));
        mail2Label.textProperty().bind(resourceFactory.getStringBinding("Contact_MailAddress2"));
        dutyPhoneLabel.textProperty().bind(resourceFactory.getStringBinding("Contact_DutyPhone"));
        privatePhoneLabel.textProperty().bind(resourceFactory.getStringBinding("Contact_PrivatePhone"));
        mobilePhoneLabel.textProperty().bind(resourceFactory.getStringBinding("Contact_MobilePhone"));

        ok.textProperty().bind(resourceFactory.getStringBinding("Button_Ok"));
        abort.textProperty().bind(resourceFactory.getStringBinding("Button_Abort"));
    }

    @FXML
    public void onOk() {
        if (isInputValid()) {
            // TODO Update model

            okClicked = true;
            close();
        }
    }

    @FXML
    public void onAbort() {
        close();
    }

    private boolean isInputValid() {
        // TODO Do some magic
        return true;
    }

    private void close() {
        Stage stage = (Stage) firstNameLabel.getScene().getWindow();
        stage.close();
    }
}
