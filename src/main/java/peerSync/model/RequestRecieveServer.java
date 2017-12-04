package peerSync.model;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

/**
 *
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 */
public class RequestRecieveServer extends UnicastRemoteObject implements remoteInterface {

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

//            this.trackedFiles.add(new PeerFile(new File(this.baseDirectory
//                    + "\\" + pf.getRelativeDirectory()), new File(this.baseDirectory)));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}
