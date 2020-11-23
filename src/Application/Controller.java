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
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
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
import java.util.Stack;

import static Handling.Dictionary.*;
import static Handling.Management.*;
import static GoogleAPI.Translator.*;
import static GoogleAPI.Voice.*;

@SuppressWarnings("ALL")
public class Controller implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private StackPane stackPane;
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
    private WebView webViewShow, webViewSearch, webViewAbout;
    @FXML
    private JFXSlider FontSlider;
    @FXML
    private JFXTextField inputEN, inputVI, inputPronoun;
    @FXML
    private JFXComboBox chooseType;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectSQLite();
        ShowAll();
        ComboBoxItems();
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
        if (selectedMeaning != null) webEngineShow.loadContent(selectedMeaning);
        else webEngineShow.loadContent("<html><h2>Oh no, I can't find this word...</h2></html>");
    }

    //Show Vietnamese meaning when switch English word by pressing arrow key on keyboard
    @FXML
    private void getSelectedMeaningOnKey(KeyEvent event) {
        //Get engine from WebView
        WebEngine webEngineShow = webViewShow.getEngine();
        //Show Vietnamese meaning in WebView area
        String selectedMeaning = searchWord(listView.getSelectionModel().getSelectedItem().toString());
        if (selectedMeaning != null) webEngineShow.loadContent(selectedMeaning);
        else webEngineShow.loadContent("<html><h2>Oh no, I can't find this word...</h2></html>");
    }

    //Show Vietnamese meaning after inputting an English word in TextField
    @FXML
    private void getInputMeaning(ActionEvent event) {
        //Comments are same with above method
        WebEngine webEngineSearch = webViewSearch.getEngine();
        String inputMeaning = searchWord(InputSearch.getText());
        if (inputMeaning != null) webEngineSearch.loadContent(inputMeaning);
        else webEngineSearch.loadContent("<html><h2>Oh no, I can't find this word...</h2></html>");
    }

    //Show information about this project read from HTML file
    @FXML
    private void aboutInformation() throws IOException {
        //Get engine from WebView
        WebEngine webEngineAbout = webViewAbout.getEngine();
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
        } else {
            JFXDialogLayout contentGSound_en = new JFXDialogLayout();
            contentGSound_en.setHeading(new Text("OH NO!"));
            contentGSound_en.setBody(new Text("There are nothing for me to pronounce !?"));
            JFXDialog EnDialog = new JFXDialog(stackPane, contentGSound_en, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(EnDialog, Pos.CENTER);
            JFXButton ohIsee = new JFXButton("Oh, my bad");
            ohIsee.setOnAction(e -> EnDialog.close());
            ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
            ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
            contentGSound_en.setActions(ohIsee);
            EnDialog.show();
        }
    }

    //Get Vietnamese sound by using GoogleAPI at Google Pane
    @FXML
    private void getGoogleSound_vi(MouseEvent event) throws Exception {
        //I can only pronounce a word if you input it
        if (!GoogleSearch.getText().isEmpty()) {
            String viText = callUrlAndParseResult("en", "vi", GoogleSearch.getText());
            InputStream googleSoundVI = getAudio(viText, "vi");
            play(googleSoundVI);
        } else {
            JFXDialogLayout contentGSound_vi = new JFXDialogLayout();
            contentGSound_vi.setHeading(new Text("OH NO!"));
            contentGSound_vi.setBody(new Text("There are nothing for me to pronounce !?"));
            JFXDialog ViDialog = new JFXDialog(stackPane, contentGSound_vi, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(ViDialog,Pos.CENTER);
            JFXButton ohIsee = new JFXButton("Oh, my bad");
            ohIsee.setOnAction(e -> ViDialog.close());
            ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
            ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
            contentGSound_vi.setActions(ohIsee);
            ViDialog.show();
        }
    }

    //Get sound by using GoogleAPI at Show Pane
    @FXML
    private void getShowSound(MouseEvent event) throws IOException, JavaLayerException {
        //I can only pronounce a word if you select it
        if (listView.getSelectionModel().getSelectedIndex() != -1) {
            InputStream showSound = getAudio(listView.getSelectionModel().getSelectedItem().toString(), "en");
            play(showSound);
        } else {
            JFXDialogLayout contentShowSound = new JFXDialogLayout();
            contentShowSound.setHeading(new Text("OH NO!"));
            contentShowSound.setBody(new Text("There are nothing for me to pronounce !?"));
            JFXDialog showSound = new JFXDialog(stackPane, contentShowSound, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(showSound,Pos.CENTER);
            JFXButton ohIsee = new JFXButton("Oh, my bad");
            ohIsee.setOnAction(e -> showSound.close());
            ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
            ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
            contentShowSound.setActions(ohIsee);
            showSound.show();
        }

    }

    //Get sound by using GoogleAPI at Search Pane
    @FXML
    private void getSearchSound(MouseEvent event) throws IOException, JavaLayerException {
        //I can only pronounce a word if you input it
        if (!InputSearch.getText().isEmpty()) {
            InputStream searchSound = getAudio(InputSearch.getText(), "en");
            play(searchSound);
        } else {
            JFXDialogLayout contentSearchSound = new JFXDialogLayout();
            contentSearchSound.setHeading(new Text("OH NO!"));
            contentSearchSound.setBody(new Text("There are nothing for me to pronounce !?"));
            JFXDialog searchSound = new JFXDialog(stackPane, contentSearchSound, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(searchSound,Pos.CENTER);
            JFXButton ohIsee = new JFXButton("Oh, my bad");
            ohIsee.setOnAction(e -> searchSound.close());
            ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
            ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
            contentSearchSound.setActions(ohIsee);
            searchSound.show();
        }
    }

    //Change font size of Vietnamese meaning
    @FXML
    private void getFontChange(MouseEvent event) {
        googleTextArea.setStyle("-fx-font-size: " + FontSlider.getValue());
    }

    //Set items of combobox when you want to add a word to dictionary
    private void ComboBoxItems() {
        String[] items = {"Danh từ", "Nội động từ", "Ngoại động từ", "Tính từ", "Trạng từ", "Phó từ"};
        ObservableList<String> itemsList = FXCollections.observableArrayList(items);
        chooseType.setItems(itemsList);
    }

    //Add new word to dictionary
    @FXML
    private void addToDictionary(ActionEvent event) {
        //Get right format of Vietnamese meaning
        String wordSequence = "<html><h1>" + inputEN.getText()
                + "</h1><h3><i>/" + inputPronoun.getText()
                + "/</i></h3><h2>" + chooseType.getSelectionModel().getSelectedItem().toString()
                + "</h2><ul><li>" + inputVI.getText() + "</li></ul></html>";
        //Add to dictionary
        addWord(inputEN.getText(), wordSequence);
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

    //Delete word from Search Pane
    @FXML
    private void deleteSearch(MouseEvent event) {
        //You can only delete if you have inputted  a word in TextField
        if (!InputSearch.getText().isEmpty()) {
            JFXDialogLayout contentSearch = new JFXDialogLayout();
            contentSearch.setHeading(new Text("WARNING !!!"));
            contentSearch.setBody(new Text("You are going to delete a word from dictionary...\nAre you sure?"));
            JFXDialog delSearch = new JFXDialog(stackPane, contentSearch, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(delSearch,Pos.CENTER);
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
        } else {
            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("LOOK!"));
            content.setBody(new Text("You are trying to delete nothing... Try again !"));
            JFXDialog lookDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(lookDialog,Pos.CENTER);
            JFXButton ohIsee = new JFXButton("Oh, I see");
            ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
            ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
            ohIsee.setOnAction(e -> lookDialog.close());
            content.setActions(ohIsee);
            lookDialog.show();
        }
    }

    //Delete word from Show Pane
    @FXML
    private void deleteShow(MouseEvent event) {
        //You can only delete if you are choosing 1 word from ListView
        if (listView.getSelectionModel().getSelectedIndex() != -1) {
            JFXDialogLayout contentShow = new JFXDialogLayout();
            contentShow.setHeading(new Text("WARNING!!!"));
            contentShow.setBody(new Text("You are going to delete a word from dictionary...\nAre you sure !?"));
            JFXDialog delShow = new JFXDialog(stackPane, contentShow, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(delShow,Pos.CENTER);
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
        } else {
            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text("LOOK!"));
            content.setBody(new Text("You didn't choose any words to delete... Try again !"));
            JFXDialog delShow = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
            StackPane.setAlignment(delShow,Pos.CENTER);
            JFXButton ohIsee = new JFXButton("Oh, I see");
            ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
            ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
            ohIsee.setOnAction(e -> delShow.close());
            content.setActions(ohIsee);
            delShow.show();
        }
    }

    @FXML
    private void alertWarning() {

    }

    //Close the program when click Exit icon
    @FXML
    private void CloseButton(MouseEvent event) {
        //Make sure if you want close program
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        JFXDialogLayout contentClose = new JFXDialogLayout();
        contentClose.setHeading(new Text("BE CAREFUL!"));
        contentClose.setBody(new Text("Are you sure to exit program !?"));
        JFXDialog exitDialog = new JFXDialog(stackPane, contentClose, JFXDialog.DialogTransition.CENTER);
        StackPane.setAlignment(exitDialog,Pos.CENTER);
        JFXButton ohIsee = new JFXButton("Yes, sure");
        ohIsee.setButtonType(JFXButton.ButtonType.RAISED);
        ohIsee.setStyle("-fx-background-color: #2296F2;-fx-text-fill:  #ffffff;");
        ohIsee.setOnAction(e -> stage.close());
        contentClose.setActions(ohIsee);
        exitDialog.show();
    }

}
