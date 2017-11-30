package peerSync.controller;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import peerSync.model.DiscoverBroadcast;
import peerSync.model.DiscoverListen;
import peerSync.model.peerSyncModel;

public class FXMLController extends JPanel implements Initializable {

    JFileChooser chooser;
    peerSyncModel driver;
    Collection hashL;
    ObservableList ipList = FXCollections.observableArrayList();
    DiscoverListen discoveryL = new DiscoverListen();

    @FXML
    private Label folderPathLbl;
    @FXML
    private Label ipAddrLbl;

    @FXML
    private ListView<String> ipAddrListView;

    @FXML
    private void selectFolderHandler(ActionEvent event) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Please Select a folder to sync");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            folderPathLbl.setText(chooser.getSelectedFile().toString());
            driver = new peerSyncModel(chooser.getSelectedFile().toString());
        } else {
            folderPathLbl.setText("no selection");
        }

    }
    
    @FXML
    public void broadcastHandler(ActionEvent event) {
        DiscoverBroadcast discoveryB = new DiscoverBroadcast();
        new Thread(discoveryB).start();
        String ipString = discoveryL.getMyIp().get(0).toString();
        ipAddrLbl.setText(ipString);
    }

    @FXML
    public void refreshHandler(ActionEvent event) {
        ipList.setAll(hashL);
        ipAddrListView.setItems(ipList);
        ipAddrListView.refresh();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new Thread(discoveryL).start();
        hashL = discoveryL.getIpSet();
        // TODO
    }
}
