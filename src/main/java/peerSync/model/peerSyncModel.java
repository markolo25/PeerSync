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
public class peerSyncModel implements Runnable {

    private File directory;

    private Set<PeerFile> trackedFiles;
    private HashSet<String> remoteIPs;
    private String status;

    /**
     *
     * @param strDirectory
     */
    public peerSyncModel(String strDirectory) {
        //get Directory
        this.directory = new File(strDirectory);

        trackedFiles = new HashSet<>();
        status = "created";
        //start listening for peers
        //send meta data to peers
        //initialize syncing

    }

    @Override
    public void run() {
        status = "running";

        while (true) {
            HashSet<PeerFile> proposedFiles = new HashSet<>();
            //get File List, and make peerFiles out of them
            for (File file : new ArrayList<>(FileUtils.listFiles(this.directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE))) {
                PeerFile fileAdded = new PeerFile(file, directory);
                proposedFiles.add(fileAdded);
                if (!trackedFiles.contains(fileAdded)) { //If file is different from a tracked add it
                    trackedFiles.add(fileAdded);
                    System.out.println(file + " Added");
                }
            }
            for (PeerFile pFileEval : trackedFiles) {
                if (!proposedFiles.contains(pFileEval)) { //If file has been removed or modified delete them
                    trackedFiles.remove(pFileEval);
                    System.out.println(pFileEval.getFile() + "Removed");
                }

            }

        }

    }

    public void queueServers() {

    }

    public void queueClient() {

    }

    public void broadCast() {
        //Insert broadcast here
    }

}
