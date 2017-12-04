package peerSync.model;

import java.io.File;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
public class peerSyncModel implements Runnable {

    private File directory;

    private Set<PeerFile> trackedFiles;
    private Collection remoteIPs;
    private String status;

    /**
     *
     * @param strDirectory
     * @param peers
     */
    public peerSyncModel(String strDirectory, Collection peers) {
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
            remoteInterface remServ = new RequestRecieveServer(directory.toString());
            Registry reg = LocateRegistry.createRegistry(1099);
            System.out.println("ready to recieve open requests");
            reg.rebind("192.168.1.20", remServ);

        }
        catch (Exception ex) {
            System.out.println("HelloImpl err: " + ex.getMessage());
            ex.printStackTrace();
        }

        while (true) {
            remoteInterface remCli = null;
            try {
                //Setup Client
                remCli = (remoteInterface) Naming.lookup("rmi://" + new ArrayList<>(remoteIPs).get(0) + "/req");
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println("No Peers Found");
            }

            HashSet<PeerFile> proposedFiles = new HashSet<>();
            //get File List, and make peerFiles out of them
            for (File file : new ArrayList<>(FileUtils.listFiles(this.directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE))) {
                try {
                    PeerFile fileAdded = new PeerFile(file, directory);
                    proposedFiles.add(fileAdded);
                    if (!trackedFiles.contains(fileAdded)) { //If file is different from a tracked add it
                        trackedFiles.add(fileAdded);
                        System.out.println(file + " Added");
                        remCli.openRecieveSocket(fileAdded);
                        new TransferSend(55265, fileAdded.getFile().getAbsolutePath()).send();

                    }
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
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
