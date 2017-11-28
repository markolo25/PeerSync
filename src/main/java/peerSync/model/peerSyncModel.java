package peerSync.model;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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
    private HashSet<File> filesToBeSynced;

    public peerSyncModel(String directory) {
        this.directory = new File(directory);
        filesToBeSynced = new HashSet<File>();

    }

    public void scanFileDirectories() {
        List<File> files = (List<File>) FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            try {
                System.out.println(file.getCanonicalPath());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
