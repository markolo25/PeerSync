/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peerSync.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Source: https://demey.io/network-discovery-using-udp-broadcast/
 * @author Amanda
 */
public class DiscoverBroadcast implements Runnable {

    Set ipSet = new HashSet(); // Collects IP addresses
    ArrayList<String> myIp = new ArrayList(); // Collects own IP address(es)
    DatagramSocket socket;

    public Set getIpSet() {
        return ipSet;
    }    

    @Override
    public void run() {
        // Find server using UDP broadcast
        try {
            // Open port to send package
            socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] sendData = "PeerSyncRequest".getBytes();

            // Try sending request to 255.255.255.255 first as default
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8000);
                socket.send(sendPacket);
                System.out.println("Broadcast: Request sent to 255.255.255.255");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            // Broadcast to all network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't broadcast to loopback interface
                }
                
                // Add broadcasted IP addresses to myIp
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    myIp.add(interfaceAddress.getAddress().getHostAddress());
                    if (broadcast == null) {
                        continue;
                    }

                    // Send broadcast package
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8000);
                        socket.send(sendPacket);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Broadcast: Request sent to " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            System.out.println("Broadcast: Done looping. Waiting for reply...");

            // Wait for a response
            byte[] receiveBuffer = new byte[15000];
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packet);

            // Got a response (if someone says it's listening to me)
            System.out.println("Broadcast: Recieved response from " + packet.getAddress().getHostAddress());

            // Check if message match
            String message = new String(packet.getData()).trim();
            if (message.equals("PeerSyncResponse")) {
                // Add IP address to ipSet if is not own IP
                if (!myIp.contains(packet.getAddress().getHostAddress())) {
                    ipSet.add(packet.getAddress().getHostAddress());
                }
                System.out.println("Broadcast List: " + ipSet);
            }
            socket.close(); // Close port
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
