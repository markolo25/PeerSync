package peerSync.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
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
            //get File List, and make peerFiles out of them
            for (File file : new ArrayList<>(FileUtils.listFiles(this.directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE))) {
                try {
                    PeerFile fileAdded = new PeerFile(file, directory);
                    proposedFiles.add(fileAdded);
                    if (!trackedFiles.contains(fileAdded)) { //If file is different from a tracked add it
                        trackedFiles.add(fileAdded);
                        System.out.println(file + " Added");

                        //Create a runnable class, that will make an RMI call to open a socket to recieve a file.
                        new Thread(new RemoteInAThread(remoteIPs, fileAdded, false)).start();

                        //Create a server to send a file
                        new TransferSend(55265, fileAdded.getFile().getAbsolutePath()).send();

                    }
                } catch (FileNotFoundException ex) {
                    /*
                    *If there is a file not found, notify other nodes to delete
                    *their copy
                    */                   
                    new Thread(new RemoteInAThread(remoteIPs,
                            new PeerFile(null, directory, null), true)).start();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println("Exception in Line 83 of Model");
                }
            }
            HashSet<PeerFile> trackedFilesCpy = new HashSet<>(trackedFiles);
            for (PeerFile pFileEval : trackedFilesCpy) {
                if (!proposedFiles.contains(pFileEval)) { //If file has been removed or modified delete them
                    trackedFiles.remove(pFileEval);
                    System.out.println(pFileEval.getFile() + " Removed");
                }

            }
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
