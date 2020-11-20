package Application;

import Handling.Dictionary;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import static Handling.Management.*;

public class Controller implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane SearchPane, ShowPane, GooglePane, AddPane, AboutPane;
    @FXML
    private JFXButton SearchButton, ShowButton, AddButton, GoogleButton, AboutButton;
    @FXML
    private TextField InputSearch;
    @FXML
    private JFXTextArea searchTextArea,showTextArea;
    @FXML
    private JFXListView listView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectSQLite();
        ShowAll();
    }

    //Change Pane with the selected Button
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

    //Show all words
    private void ShowAll()
    {
        //ObvervableList of English words
        ObservableList<String> rawList = FXCollections.observableArrayList(getRaw());
        //Add above list to ListView
        listView.setItems(rawList);
    }


    //Close the program when click Exit icon
    @FXML
    private void CloseButton(MouseEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }

}
