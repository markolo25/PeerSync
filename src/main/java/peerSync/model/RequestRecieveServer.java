package peerSync.model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 */
public class RequestRecieveServer extends UnicastRemoteObject implements remoteInterface {

    String baseDirectory;

    public RequestRecieveServer(String baseDirectory) throws RemoteException {
        super();
        this.baseDirectory = baseDirectory;
        System.out.println(this.baseDirectory);
    }

    @Override
    public void openRecieveSocket(PeerFile pf) throws RemoteException {
        try {
            System.out.println(pf);
            System.out.println("I get here");
            new TransferReceive(55265, getClientHost(), this.baseDirectory
                    + "\\" + pf.getRelativeDirectory()).recieve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
