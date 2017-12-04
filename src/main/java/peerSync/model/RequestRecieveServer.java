package peerSync.model;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 */
public class RequestRecieveServer extends UnicastRemoteObject implements RemoteInterface {

    String baseDirectory;
    private Set<PeerFile> trackedFiles;

    public RequestRecieveServer(String baseDirectory, Set<PeerFile> trackedFiles) throws RemoteException {
        super();
        this.baseDirectory = baseDirectory;
        this.trackedFiles = trackedFiles;
        System.out.println(this.baseDirectory);
    }

    @Override
    public void recieveFile(PeerFile pf) throws RemoteException {
        try {
            System.out.println(pf);
            new TransferReceive(55265, getClientHost(), this.baseDirectory
                    + "\\" + pf.getRelativeDirectory()).recieve();

            this.trackedFiles.add(new PeerFile(new File(this.baseDirectory
                    + "\\" + pf.getRelativeDirectory()), new File(this.baseDirectory)));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteFile(PeerFile pf, int i) throws RemoteException {
        try {
            this.trackedFiles.remove(new PeerFile(new File(this.baseDirectory
                    + "\\" + pf.getRelativeDirectory()), new File(this.baseDirectory)));
        } catch (IOException ex) {
            System.out.println("File to be deleted missing");
        }
        
        
        File delFile = new File(this.baseDirectory + "\\" + pf.getRelativeDirectory());
        System.out.println("deleting " + delFile);
        delFile.delete();
    }

    @Override
    public boolean check(PeerFile pf) throws RemoteException {
        try {
            return trackedFiles.contains(new PeerFile(new File(this.baseDirectory + "\\" + pf.getRelativeDirectory()), new File(this.baseDirectory)));
        } catch (IOException ex) {
            return false;

        }
    }

}
