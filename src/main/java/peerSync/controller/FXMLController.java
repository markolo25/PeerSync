package peerSync.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import peerSync.model.DiscoverBroadcast;
import peerSync.model.DiscoverListen;

public class FXMLController extends JPanel implements Initializable {

    JFileChooser chooser;

    @FXML
    private Label folderPathLbl;

    @FXML
    private void selectFolderHandler(ActionEvent event) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Please Select a folder to sync");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println(chooser.getSelectedFile());
            folderPathLbl.setText(chooser.getSelectedFile().toString());
        } else {
            folderPathLbl.setText("no selection");
        }

    }
    
    @FXML
    public void syncHandler (ActionEvent event) {
        Thread discoveryL = new Thread(DiscoverListen.getInstance());
        Thread discoveryB = new Thread(DiscoverBroadcast.getInstance());
        discoveryL.start();
        discoveryB.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
