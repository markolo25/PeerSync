package peerSync.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 *
 * @author Amanda Pan <daikiraidemodaisuki@gmail.com>
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 *
 */
public class peerSyncModel {

    private File directory;
    private ArrayList<File> filesToBeSynced;
    private HashSet<String> remoteIPs;

    public peerSyncModel(String strDirectory) {     
        //get Directory
        this.directory = new File(strDirectory);       
        //get File List
        filesToBeSynced = new ArrayList<>(FileUtils.listFiles(this.directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
        //start listening for peers
        
        //send meta data to peers
        
        //initialize syncing
        
    }
    
    
    public void queueServers(){
        
    }
    
    public void queueClient(){
        
    }
    
    public void broadCast(){
        //Insert broadcast here
    }


}
