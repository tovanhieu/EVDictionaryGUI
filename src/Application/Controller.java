package Application;

/*
 * Class "Controller" control all action of JavaFX application like click, press key,...
 * @Author: Meoki
 */

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos; //Used in commented code
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text; //Used in commented code
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static Handling.Dictionary.*;
import static Handling.Management.*;
import static GoogleAPI.Translator.*;
import static GoogleAPI.Voice.*;

public class Controller implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private StackPane stackPane; //Root pane
    @FXML
    private Pane SearchPane, ShowPane, GooglePane, AddPane, AboutPane, EditPane, exitPane, DelPane, DelPaneShow;
    @FXML
    private JFXButton SearchButton, ShowButton, AddButton, GoogleButton, AboutButton, ExportButton;
    @FXML
    private TextField InputSearch, GoogleSearch;
    @FXML
    private JFXListView listView;
    @FXML
    private JFXTextArea googleTextArea;
    @FXML
    private WebView webViewShow, webViewSearch, webViewAbout;
    @FXML
    private JFXSlider FontSlider;
    @FXML
    private JFXTextField inputEN, inputVI, inputPronoun, editEN, editVI, editPronoun;
    @FXML
    private JFXComboBox chooseType, chooseEditType;

    //Blur effect to set for AnchorPane when a dialog is opened
    BoxBlur blur = new BoxBlur(10, 10, 10);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Connect to database
        connectSQLite();
        //Show all words in Show pane
        ShowAll();
        //Set items for 2 combobox in Edit pane and Add pane
        ComboBoxItems(chooseType);
        ComboBoxItems(chooseEditType);
        try {
            aboutInformation();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            //Write data to file
            if (file != null) exportDictionary(file);
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
        webEngineShow.loadContent(selectedMeaning);

    }

    //Show Vietnamese meaning when switch English word by pressing arrow key on keyboard
    @FXML
    private void getSelectedMeaningOnKey(KeyEvent event) {
        //Get engine from WebView
        WebEngine webEngineShow = webViewShow.getEngine();
        //Show Vietnamese meaning in WebView area
        String selectedMeaning = searchWord(listView.getSelectionModel().getSelectedItem().toString());
        webEngineShow.loadContent(Objects.requireNonNullElse(selectedMeaning, "<html><h2>Oh no, I can't find this word...</h2></html>"));
    }

    //Show Vietnamese meaning after inputting an English word in TextField
    @FXML
    private void getInputMeaning(ActionEvent event) {
        //Comments are same with above method
        WebEngine webEngineSearch = webViewSearch.getEngine();
        String inputMeaning = searchWord(InputSearch.getText());
        webEngineSearch.loadContent(Objects.requireNonNullElse(inputMeaning, "<html><h2>Oh no, I can't find this word...</h2></html>"));
    }

    //Show information about this project read from HTML file
    @FXML
    private void aboutInformation() throws IOException {
        //Get engine from WebView
        WebEngine webEngineAbout = webViewAbout.getEngine();
        //Show HTML information
        webEngineAbout.loadContent(aboutInfo());
    }

    //Get Vietnamese meaning by using GoogleAPI
    @FXML
    private void getGoogleMeaning(ActionEvent event) throws Exception {
        String googleMeaning = callUrlAndParseResult("en", "vi", GoogleSearch.getText());
        googleTextArea.setEditable(false);
        googleTextArea.setText(googleMeaning);
    }

    //Get English sound by using GoogleAPI at Google Pane
    @FXML
    private void getGoogleSound_en(MouseEvent event) throws IOException, JavaLayerException {
        //I can only pronounce a word if you input it
        if (!GoogleSearch.getText().isEmpty()) {
            InputStream googleSoundEN = getAudio(GoogleSearch.getText(), "en");
            play(googleSoundEN);
        } else noSound();
    }

    //Get Vietnamese sound by using GoogleAPI at Google Pane
    @FXML
    private void getGoogleSound_vi(MouseEvent event) throws Exception {
        //I can only pronounce a word if you input it
        if (!GoogleSearch.getText().isEmpty()) {
            String viText = callUrlAndParseResult("en", "vi", GoogleSearch.getText());
            InputStream googleSoundVI = getAudio(viText, "vi");
            play(googleSoundVI);
        } else noSound();
    }

    //Get sound by using GoogleAPI at Show Pane
    @FXML
    private void getShowSound(MouseEvent event) throws IOException, JavaLayerException {
        //I can only pronounce a word if you select it
        if (listView.getSelectionModel().getSelectedIndex() != -1) {
            InputStream showSound = getAudio(listView.getSelectionModel().getSelectedItem().toString(), "en");
            play(showSound);
        } else noSound();

    }

    //Get sound by using GoogleAPI at Search Pane
    @FXML
    private void getSearchSound(MouseEvent event) throws IOException, JavaLayerException {
        //I can only pronounce a word if you input it
        if (!InputSearch.getText().isEmpty()) {
            InputStream searchSound = getAudio(InputSearch.getText(), "en");
            play(searchSound);
        } else noSound();
    }

    private void noSound() {
        //Method 1 - For people who desire a modern, beautiful flat design
        /*
        JFXDialogLayout contentSearchSound = new JFXDialogLayout();
        contentSearchSound.setHeading(new Text("OH NO!"));
        contentSearchSound.setBody(new Text("There are nothing for me to pronounce !?"));
        JFXDialog searchSound = new JFXDialog(stackPane, contentSearchSound, JFXDialog.DialogTransition.CENTER);
        StackPane.setAlignment(searchSound, Pos.CENTER);
        JFXButton ohIsee = new JFXButton("Oh, my bad");
        ohIsee.setOnAction(e -> searchSound.close());
        ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
        ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
        contentSearchSound.setActions(ohIsee);
        searchSound.show();
        */

        //Method 2 - For normal people
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("There are nothing for me to pronounce !?");
        alert.setTitle("LOOK !");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    //Change font size of Vietnamese meaning
    @FXML
    private void getFontChange(MouseEvent event) {
        googleTextArea.setStyle("-fx-font-size: " + FontSlider.getValue());
    }

    //Set items of combobox when you want to add a word to dictionary
    private void ComboBoxItems(ComboBox ex) {
        String[] items = {"Danh từ", "Nội động từ", "Ngoại động từ", "Tính từ", "Trạng từ", "Phó từ"};
        ObservableList<String> itemsList = FXCollections.observableArrayList(items);
        ex.setItems(itemsList);
    }

    //Add new word to dictionary
    @FXML
    private void addToDictionary(ActionEvent event) {
        //I can only add this word to dictionary if some fields in the pane is filled
        if (!editEN.getText().isEmpty() && chooseEditType.getSelectionModel().getSelectedIndex() != -1 && !editVI.getText().isEmpty()) {
            //Get right format of Vietnamese meaning
            String wordSequence = "<html><h1>" + inputEN.getText()
                    + "</h1><h3><i>/" + inputPronoun.getText()
                    + "/</i></h3><h2>" + chooseType.getSelectionModel().getSelectedItem().toString()
                    + "</h2><ul><li>" + inputVI.getText() + "</li></ul></html>";
            //Add to dictionary
            addWord(inputEN.getText(), wordSequence);
        } else inputAlert();
    }

    //Alert when the fill in Edit pane and Add pane is not filled
    private void inputAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("You have to input English word,Vietnamese meaning and choose type of English word first...");
        alert.setTitle("LOOK !");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    //Clear Search input and meaning
    @FXML
    private void clearSearchInput(MouseEvent event) {
        InputSearch.clear();
        webViewSearch.getEngine().loadContent("");
    }

    //Clear Google input and meaning
    @FXML
    private void clearGoogleInput(MouseEvent event) {
        GoogleSearch.clear();
        googleTextArea.clear();
    }

    //Accept to delete word in Search Pane
    @FXML
    private void YesDelete(ActionEvent event) {
        deleteWord(InputSearch.getText());
        //Clear something else
        InputSearch.clear();
        webViewSearch.getEngine().loadContent("");
        //Restore AnchorPane
        anchorPane.setEffect(null);
        DelPane.toBack();
    }

    //Deny deletting word in Search Pane
    @FXML
    private void NoDelete(ActionEvent event) {
        DelPane.toBack();
        anchorPane.setEffect(null);
    }

    //Delete word from Search Pane
    @FXML
    private void deleteSearch(MouseEvent event) {
        //You can only delete if you have inputted  a word in TextField
        if (!InputSearch.getText().isEmpty()) {
            /*
            JFXDialogLayout contentSearch = new JFXDialogLayout();
            contentSearch.setHeading(new Text("WARNING !!!"));
            contentSearch.setBody(new Text("You are going to delete a word from dictionary...\nAre you sure?"));
            JFXDialog delSearch = new JFXDialog(stackPane, contentSearch, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(delSearch, Pos.CENTER);
            JFXButton cancel = new JFXButton("No, my bad");
            JFXButton accept = new JFXButton("Yes, sure");
            cancel.setOnAction(e -> delSearch.close());
            accept.setOnAction(e -> {
                deleteWord(InputSearch.getText());
                InputSearch.clear();
                webViewSearch.getEngine().loadContent("");
                delSearch.close();
            });
            contentSearch.setActions(cancel);
            contentSearch.setActions(accept);
            delSearch.show();
            */

            DelPane.toFront();
            anchorPane.setEffect(blur);

        } else noDelete();
    }

    //Accept to delete word in Show Pane
    @FXML
    private void YesDeleteShow(ActionEvent event) {
        deleteWord(listView.getSelectionModel().getSelectedItem().toString());
        webViewShow.getEngine().loadContent("");
        listView.getItems().remove(listView.getSelectionModel().getSelectedIndex());
        anchorPane.setEffect(null);
        DelPaneShow.toBack();
    }

    //Deny deletting word in Show Panedelete
    @FXML
    private void NoDeleteShow(ActionEvent event) {
        DelPaneShow.toBack();
        anchorPane.setEffect(null);
    }


    //Delete word from Show Pane
    @FXML
    private void deleteShow(MouseEvent event) {
        //You can only delete if you are choosing 1 word from ListView
        if (listView.getSelectionModel().getSelectedIndex() != -1) {
            /*
            JFXDialogLayout contentShow = new JFXDialogLayout();
            contentShow.setHeading(new Text("WARNING!!!"));
            contentShow.setBody(new Text("You are going to delete a word from dictionary...\nAre you sure !?"));
            JFXDialog delShow = new JFXDialog(stackPane, contentShow, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(delShow, Pos.CENTER);
            JFXButton cancel = new JFXButton("No, my bad");
            JFXButton accept = new JFXButton("Yes, sure");
            cancel.setOnAction(e -> delShow.close());
            accept.setOnAction(e -> {
                deleteWord(listView.getSelectionModel().getSelectedItem().toString());
                webViewShow.getEngine().loadContent("");
                listView.getItems().remove(listView.getSelectionModel().getSelectedIndex());
                delShow.close();
            });
            contentShow.setActions(cancel);
            contentShow.setActions(accept);
            delShow.show();
            */

            DelPaneShow.toFront();
            anchorPane.setEffect(blur);

        } else noDelete();
    }

    //You have to choose a word to delete
    private void noDelete() {
        //Method 1 - For people who desire a modern, beautiful flat design
        /*
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("LOOK!"));
        content.setBody(new Text("You are trying to delete nothing..."));
        JFXDialog delShow = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        StackPane.setAlignment(delShow, Pos.CENTER);
        JFXButton ohIsee = new JFXButton("Oh, I see");
        ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
        ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
        ohIsee.setOnAction(e -> delShow.close());
        content.setActions(ohIsee);
        delShow.show();
        */

        //Method 2 - For normal people
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("You are trying to delete nothing...");
        alert.setTitle("LOOK !");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    //Open Edit Pane when clicking Edit icon
    @FXML
    private void openEditPane(MouseEvent event) {
        if (!InputSearch.getText().isEmpty()) {
            editEN.setText(InputSearch.getText());
            EditPane.toFront();
            editVI.setText("");
            chooseEditType.setValue(null);
            anchorPane.setEffect(blur);
        } else noEdit();
    }

    //Open Edit Pane when clicking Edit icon
    @FXML
    private void openEditShowPane(MouseEvent event) {
        if (listView.getSelectionModel().getSelectedIndex() != -1) {
            editEN.setText(listView.getSelectionModel().getSelectedItem().toString());
            EditPane.toFront();
            editVI.setText("");
            chooseEditType.setValue(null);
            anchorPane.setEffect(blur);
        } else noEdit();
    }

    //Deny editing a word
    @FXML
    private void Cancel(ActionEvent event) {
        EditPane.toBack();
        anchorPane.setEffect(null);
    }

    //You have to choose a word to edit
    private void noEdit() {
        //Method 1 - For people who desire a modern, beautiful flat design
        /*
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("LOOK!"));
        content.setBody(new Text("There are nothing to edit..."));
        JFXDialog editDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        StackPane.setAlignment(editDialog, Pos.CENTER);
        JFXButton ohIsee = new JFXButton("Oh, my bad");
        ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
        ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
        ohIsee.setOnAction(e -> editDialog.close());
        content.setActions(ohIsee);
        editDialog.show();
        */

        //Method 2 - For normal people
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("There are nothing to edit...");
        alert.setTitle("LOOK !");
        alert.setHeaderText(null);
        alert.showAndWait();
        alert.getOnShown();
    }

    //Edit a word in dictionary
    @FXML
    private void editWords(ActionEvent event) {
        if (!editEN.getText().isEmpty() && chooseEditType.getSelectionModel().getSelectedIndex() != -1 && !editVI.getText().isEmpty()) {
            EditPane.toBack();
            anchorPane.setEffect(null);
            String wordSequence = "<html><h1>" + editEN.getText()
                    + "</h1><h3><i>/" + editPronoun.getText()
                    + "/</i></h3><h2>" + chooseEditType.getSelectionModel().getSelectedItem().toString()
                    + "</h2><ul><li>" + editVI.getText() + "</li></ul></html>";
            editWord(editEN.getText(), wordSequence);
        } else inputAlert();
    }

    //Deny closing program
    @FXML
    private void NoExit(ActionEvent event) {
        exitPane.toBack();
        anchorPane.setEffect(null);
    }

    //Accept closing program
    @FXML
    private void YesExit(ActionEvent event) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }

    //Close the program when click Exit icon
    @FXML
    private void CloseButton(MouseEvent event) {
        /*
        //Make sure if you want close program
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        JFXDialogLayout contentClose = new JFXDialogLayout();
        contentClose.setHeading(new Text("BE CAREFUL!"));
        contentClose.setBody(new Text("Are you sure to exit program !?"));
        JFXDialog exitDialog = new JFXDialog(stackPane, contentClose, JFXDialog.DialogTransition.CENTER);
        StackPane.setAlignment(exitDialog, Pos.CENTER);
        JFXButton ohIsee = new JFXButton("Yes, sure");
        ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
        ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
        ohIsee.setOnAction(e -> stage.close());
        contentClose.setActions(ohIsee);
        exitDialog.show();
        */

        exitPane.toFront();
        anchorPane.setEffect(blur);
    }

}
