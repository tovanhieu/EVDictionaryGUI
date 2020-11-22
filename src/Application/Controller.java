package Application;

/*
 * Class "Controller" control all action of JavaFX application like click, press key,...
 * @Author: Meoki
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static Handling.Dictionary.*;
import static Handling.Management.*;
import static GoogleAPI.Translator.*;
import static GoogleAPI.Audio.*;

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
    private JFXTextArea googleTextArea;
    @FXML
    private WebView webViewShow, webViewSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectSQLite();
        ShowAll();
    }

    //Switch Pane with selected Button
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
        //Get engine from WebView
        WebEngine webEngineShow = webViewShow.getEngine();
        //Show Vietnamese meaning in WebView area
        String selectedMeaning = searchWord(listView.getSelectionModel().getSelectedItem().toString());
        if (selectedMeaning != null) {
            webEngineShow.loadContent(selectedMeaning);
        } else webEngineShow.loadContent("<html><h2>Tạm thời nghĩa của từ này chưa được cập nhật...</h2></html>");
    }

    //Show Vietnamese meaning when switch English word by pressing arrow key on keyboard
    @FXML
    private void getSelectedMeaningOnKey(KeyEvent event) {
        //Get engine from WebView
        WebEngine webEngineShow = webViewShow.getEngine();
        //Show Vietnamese meaning in WebView area
        String selectedMeaning = searchWord(listView.getSelectionModel().getSelectedItem().toString());
        if (selectedMeaning != null) {
            webEngineShow.loadContent(selectedMeaning);
        } else webEngineShow.loadContent("<html><h2>Tạm thời nghĩa của từ này chưa được cập nhật...</h2></html>");
    }


    //Show Vietnamese meaning after inputting an English word in TextField
    @FXML
    private void getInputMeaning(ActionEvent event) {
        //Comments are same with above method
        WebEngine webEngineSearch = webViewSearch.getEngine();
        String inputMeaning = searchWord(InputSearch.getText());
        if (inputMeaning != null) {
            webEngineSearch.loadContent(inputMeaning);
        } else webEngineSearch.loadContent("<html><h2>Tạm thời nghĩa của từ này chưa được cập nhật...</h2></html>");
    }

    //Get Vietnamese meaning by using GoogleAPI
    @FXML
    private void getGoogleMeaning(ActionEvent event) throws Exception {
        String googleMeaning = callUrlAndParseResult("en", "vi", GoogleSearch.getText());
        googleTextArea.setText(googleMeaning);
    }

    //Get sound by using GoogleAPI at Google Pane
    @FXML
    private void getGoogleSound(MouseEvent event) throws IOException, JavaLayerException {
        InputStream googleSound = getAudio(GoogleSearch.getText(), "en");
        play(googleSound);
    }

    //Get sound by using GoogleAPI at Show Pane
    @FXML
    private void getShowSound(MouseEvent event) throws IOException, JavaLayerException {
        InputStream showSound = getAudio(listView.getSelectionModel().getSelectedItem().toString(), "en");
        play(showSound);
    }

    //Get sound by using GoogleAPI at Search Pane
    @FXML
    private void getSearchSound(MouseEvent event) throws IOException, JavaLayerException {
        InputStream searchSound = getAudio(InputSearch.getText(), "en");
        play(searchSound);
    }

    //Close the program when click Exit icon
    @FXML
    private void CloseButton(MouseEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }

}
