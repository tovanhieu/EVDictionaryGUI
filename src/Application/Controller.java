package Application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    @FXML
    private Label CloseButton;
    @FXML
    private Button SearchButton,ShowButton,GoogleButton,AddButton;
    @FXML
    private Pane SearchPane,ShowPane,GooglePane,AddPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CloseButton.setOnMouseClicked(event -> System.exit(0));
    }

    @FXML
    private void handleButtonPress(MouseEvent event)
    {
        if (event.getTarget()==SearchButton)
        {
            SearchPane.setVisible(true);
            ShowPane.setVisible(false);
            GooglePane.setVisible(false);
            AddPane.setVisible(false);
        }
        else if (event.getTarget()==AddButton)
        {
            SearchPane.setVisible(false);
            ShowPane.setVisible(false);
            AddPane.setVisible(true);
            GooglePane.setVisible(false);
        }
        else if (event.getTarget()==ShowButton)
        {
            SearchPane.setVisible(false);
            ShowPane.setVisible(true);
            AddPane.setVisible(false);
            GooglePane.setVisible(false);
        }
        else if (event.getTarget()==GoogleButton)
        {
            SearchPane.setVisible(false);
            ShowPane.setVisible(false);
            AddPane.setVisible(false);
            GooglePane.setVisible(true);
        }
    }
}
