package peerSync.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 */
public interface RemoteInterface extends Remote{
    public void recieveFile(PeerFile pf) throws RemoteException;
    public void deleteFile(PeerFile pf) throws RemoteException;
}