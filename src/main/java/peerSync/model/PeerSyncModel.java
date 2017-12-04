package peerSync.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 *
 * @author Amanda Pan <daikiraidemodaisuki@gmail.com>
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 *
 */
public class PeerSyncModel implements Runnable {

    private File directory;

    private Set<PeerFile> trackedFiles;
    private Collection remoteIPs;
    private String status;

    /**
     *
     * @param strDirectory
     * @param peers
     */
    public PeerSyncModel(String strDirectory, Collection peers) {
        //get Directory
        this.directory = new File(strDirectory);
        remoteIPs = peers;
        trackedFiles = new HashSet<>();
        status = "created";

    }

    @Override
    public void run() {
        status = "running";

        try {
            //Setup RMI Server Stub
            RemoteInterface remServ = new RequestRecieveServer(directory.toString(), trackedFiles);
            Registry reg = LocateRegistry.createRegistry(1099);
            System.out.println("ready to recieve open requests");
            reg.rebind("req", remServ);

        } catch (Exception ex) {
            System.out.println("HelloImpl err: " + ex.getMessage());
            ex.printStackTrace();
        }

        while (true) {
            HashSet<PeerFile> proposedFiles = new HashSet<>();

            //Make a File list of the files currently in the folder
            for (File file : new ArrayList<>(FileUtils.listFiles(this.directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE))) {
                PeerFile fileAdded = null;
                try {
                    fileAdded = new PeerFile(file, directory);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (fileAdded != null) {
                    proposedFiles.add(fileAdded);

                }
            }

            //If a file is missing from the folder remove them from
            //the files we are currently tracking and notify the other node
            HashSet<PeerFile> trackedFilesCpy = new HashSet<>(trackedFiles);
            for (PeerFile pFileEval : trackedFilesCpy) {
                if (!proposedFiles.contains(pFileEval)) { //If file has been removed or modified delete them
                    trackedFiles.remove(pFileEval);
                    new Thread(new RemoteInAThread(remoteIPs,
                            new PeerFile(pFileEval.getFile(), directory, null), true)).start();
                    System.out.println(pFileEval.getFile() + " Removed");
                }

            }

            RemoteInterface remCli = null;
            try {
                remCli = (RemoteInterface) Naming.lookup("rmi://" + new ArrayList<>(remoteIPs).get(0) + "/req");
            } catch (NotBoundException ex) {
                Logger.getLogger(PeerSyncModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(PeerSyncModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(PeerSyncModel.class.getName()).log(Level.SEVERE, null, ex);
            }

            //for each proposed file
            for (PeerFile pFileEval : proposedFiles) {
                //check if it is currently being tracked
                if (trackedFiles.contains(pFileEval)) {
                    if (remCli != null) {
                        try {
                            //If other node does not have send
                            if (!remCli.check(pFileEval)) {
                                sendFile(pFileEval);
                            }
                        } catch (RemoteException ex) {
                            Logger.getLogger(PeerSyncModel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else { //If not  
                    trackedFiles.add(pFileEval); //Track it
                    if (remCli != null) {
                        try {
                            //If other node does not have 
                            if (!remCli.check(pFileEval)) {
                                sendFile(pFileEval); // send it
                            }
                        } catch (RemoteException ex) {
                            Logger.getLogger(PeerSyncModel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

        }

    }

    private void sendFile(PeerFile pf) {
        //Create a runnable class, that will make an RMI call to open a socket to recieve a file.
        Thread tr = new Thread(new RemoteInAThread(remoteIPs, pf, false));
        tr.start();

        try {
            //Create a server to send a file
            new TransferSend(55265, pf.getFile().getAbsolutePath()).send();
        } catch (FileNotFoundException ex) {
            System.out.println("File to send not found");
        }
    }

    public ArrayList<String> getMyIp() throws SocketException {
        ArrayList<String> myIP = new ArrayList();

        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue; // Don't want to broadcast to the loopback interface
            }
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                myIP.add(interfaceAddress.getAddress().getHostAddress());
            }
        }
        return myIP;
    }

    public void queueServers() {

    }

    public void queueClient() {

    }

    public void broadCast() {
        //Insert broadcast here
    }

}
