package peerSync.model;

import java.io.File;
import java.util.ArrayList;
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
    private ArrayList<peerFile> filesToBeSynced;

    public peerSyncModel(String strDirectory) {
        //get Directory
        this.directory = new File(strDirectory);

        //get File List, and make peerFiles out of them
        filesToBeSynced = new ArrayList<peerFile>();
        for (File file : new ArrayList<>(FileUtils.listFiles(this.directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE))) {
            filesToBeSynced.add(new peerFile(file, directory));
        }
        System.out.println(filesToBeSynced);
        
        
    }

}
