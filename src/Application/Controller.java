package Application;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane SearchPane, ShowPane, GooglePane, AddPane, AboutPane;
    @FXML
    private JFXButton SearchButton, ShowButton, AddButton, GoogleButton, AboutButton;
    @FXML
    private TextField InputSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //Change Pane with the selected Button
    //Start
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == SearchButton) {
            SearchPane.toFront();
        } else if (event.getSource() == ShowButton) {
            ShowPane.toFront();
        } else if (event.getSource() == AddButton) {
            AddPane.toFront();
        } else if (event.getSource() == GoogleButton) {
            GooglePane.toFront();
        } else if (event.getSource() == AboutButton) {
            AboutPane.toFront();
        }
    }
    //End.

    //Auto complete when search a word
    //Start
    @FXML
    private void InputTextField(ActionEvent event) {
        String[] input = {"Hi", "Hello"};
        TextFields.bindAutoCompletion(InputSearch, input);
    }
    //End.


    //Close the program when click Exit icon
    //Start
    @FXML
    private void CloseButton(MouseEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }
    //End.

}
