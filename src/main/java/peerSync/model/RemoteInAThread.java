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
    private PeerFile fileAdded;

    public RemoteInAThread(Collection remoteIPs, PeerFile fileAdded) {
        this.remoteIPs = remoteIPs;
        this.fileAdded = fileAdded;
    }

    @Override
    public void run() {
        RemoteInterface remCli = null;
        try {
            //Setup Client
            remCli = (RemoteInterface) Naming.lookup("rmi://" + new ArrayList<>(remoteIPs).get(0) + "/req");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("No Peers Found");
        }
        try {
            if (fileAdded != null) {
                remCli.recieveFile(fileAdded);

            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

    }

}
