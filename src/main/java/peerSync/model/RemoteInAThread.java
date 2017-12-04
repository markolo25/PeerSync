/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peerSync.model;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 */
public class RemoteInAThread implements Runnable {

    private Collection remoteIPs;
    private PeerFile file;
    private boolean isDelete;

    public RemoteInAThread(Collection remoteIPs, PeerFile fileAdded, boolean isDelete) {
        this.remoteIPs = remoteIPs;
        this.file = fileAdded;
        this.isDelete = isDelete;

    }

    @Override
    public void run() {
        RemoteInterface remCli = null;
        try {
            //Setup Client
            if (remoteIPs != null) {
                remCli = (RemoteInterface) Naming.lookup("rmi://" + new ArrayList<>(remoteIPs).get(0) + "/req");
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("No Peers Found");
        }
        try {
            if (file != null && file.getFile() != null) {
                if (isDelete) {
                    remCli.deleteFile(file, 3);

                }
                else {
                    remCli.recieveFile(file);

                }
            }
        }
        catch (RemoteException ex) {
            ex.printStackTrace();
        }

    }

}
