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
public class DiscoverListen implements Runnable {

    Set ipSet = new HashSet(); // Collects IP addresses
    ArrayList<String> myIp = new ArrayList(); // Collects own IP address(es)
    DatagramSocket socket;
    
    public Set getIpSet() {
        return ipSet;
    }    

    @Override
    public void run() {
        try {
            // Listen for own IP addresses
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't broadcast to loopback interface
                }
                // Add own IP addresses to myIp
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    myIp.add(interfaceAddress.getAddress().getHostAddress());
                }
            }

            // Keep socket open to listen to all the UDP trafic that is destined for this port
            socket = new DatagramSocket(8000, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                System.out.println("Listen: Ready to receive broadcast packets");

                // Receive a packet (if someone is shouting it's IP to me)
                byte[] receiveBuffer = new byte[15000];
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);

                // Packet received
                System.out.println("Listen: Packet received from " + packet.getAddress().getHostAddress());

                // Check if message match
                String message = new String(packet.getData()).trim();
                if (message.equals("PeerSyncRequest")) {
                    byte[] sendData = "PeerSyncResponse".getBytes();

                    // Send a response (tell shouter that I am heard it)
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);

                    if (!myIp.contains(sendPacket.getAddress().getHostAddress())) {
                        ipSet.add(sendPacket.getAddress().getHostAddress());
                    }
                    System.out.println("Listen List: " + ipSet);
                    System.out.println("Listen: Sent packet to: " + sendPacket.getAddress().getHostAddress());
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
