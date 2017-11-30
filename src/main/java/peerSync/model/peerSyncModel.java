package peerSync.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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

    private Set<peerFile> trackedFiles;
    private Set<String> remoteIPs;
    private String status;

    /**
     *
     * @param strDirectory
     */
    public peerSyncModel(String strDirectory) {
        //get Directory
        this.directory = new File(strDirectory);

        trackedFiles = new HashSet<>();
        for (File file : new ArrayList<>(FileUtils.listFiles(this.directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE))) {
            trackedFiles.add(new peerFile(file, directory));
        }
        System.out.println(trackedFiles);
        status = "created";
        
        //send meta data to peers
        //initialize syncing

    }
    
    public Set<String> getRemoteIPs() {
        return remoteIPs;
    }

    public void run() {
        status = "running";

        while (true) {
            ArrayList<peerFile> proposedFiles = new ArrayList<>();
            //get File List, and make peerFiles out of them
            for (File file : new ArrayList<>(FileUtils.listFiles(this.directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE))) {
                proposedFiles.add(new peerFile(file, directory));
            }

        }

    }
    
    public void queueServers() {

    }

    public void queueClient() {

    }

    public void broadCast() {
        DiscoverBroadcast discoveryB = new DiscoverBroadcast();
        new Thread(discoveryB).start();
    }

}
