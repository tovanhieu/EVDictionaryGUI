package Application;

/*
 * Class "Controller" control all action of JavaFX application like click, press key,...
 * @Author: Meoki
 */

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static Handling.Management.*;
import static Handling.Dictionary.*;

public class Controller implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane SearchPane, ShowPane, GooglePane, AddPane, AboutPane;
    @FXML
    private JFXButton SearchButton, ShowButton, AddButton, GoogleButton, AboutButton, ExportButton;
    @FXML
    private TextField InputSearch, GoogleSearch;
    @FXML
    private JFXListView listView;
    @FXML
    private JFXTextArea searchTextArea, showTextArea, googleTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectSQLite();
        ShowAll();
    }

    //Change Pane with the selected Button
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
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
        } else if (event.getSource() == ExportButton) {
            FileChooser fileChooser = new FileChooser();
            //Title of dialog
            fileChooser.setTitle("Save");
            //File's format
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            //Open dialog to choose where to save file
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                //Write data to file
                exportDictionary(file);
            }
        }
    }

    //Show all words
    private void ShowAll() {
        //ObservableList of English words
        ObservableList<String> rawList = FXCollections.observableArrayList(getRaw());
        //Add above list to ListView
        listView.setItems(rawList);
    }

    //Show Vietnamese meaning when click at English word
    @FXML
    private void getSelectedMeaning(MouseEvent event) {
        showTextArea.setEditable(false);
        showTextArea.setText(searchWord(listView.getSelectionModel().getSelectedItem().toString()));
    }

    //Show Vietnamese meaning after inputting an English word in TextField
    @FXML
    private void getInputMeaning(ActionEvent event) {
        searchTextArea.setEditable(false);
        searchTextArea.setText(searchWord(InputSearch.getText()));
    }


    //Close the program when click Exit icon
    @FXML
    private void CloseButton(MouseEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }

}
