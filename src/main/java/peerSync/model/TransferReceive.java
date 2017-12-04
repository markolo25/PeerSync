package peerSync.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 */
public class TransferReceive {

    private int port;
    private String source;
    private String directory;

    /**
     * TransferRecieve Constructor, fills in attributes needed to transfer file
     * to a computer
     *
     * @param port
     * @param source
     * @param directory
     */
    public TransferReceive(int port, String source, String directory) {
        this.port = port;
        this.source = source;
        this.directory = directory;
        System.out.println("Transfer Recieve for" + directory + "from" + source + "created");
    }

    public void recieve() {
        System.out.println("Recieve started");
        int bytesRead;
        int current = 0;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        Socket socket = null;
        try {
            new File(directory).getParentFile().mkdirs();
            socket = new Socket(source, port);
            socket.setSoTimeout(15000);
            System.out.println("Waiting on server for (" + directory + ")");

            //recieve file
            byte[] mbytearray = new byte[1024*1024*1024]; 

            InputStream inputStream = socket.getInputStream();
            fileOutputStream = new FileOutputStream(directory);

            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            do {
                //Read mbytearray starting at the 0th position up to it's length
                bytesRead = inputStream.read(mbytearray, 0, mbytearray.length);

                //Store where you end off reading in current
                current = bytesRead;

                //Starting from where you left off read the length, minus the amount you read sofar
                inputStream.read(mbytearray, current, mbytearray.length - current);
                if (bytesRead >= 0) {
                    current += bytesRead;
                }
            }
            while (bytesRead < -1); //if there's nothing left to read you're done

            bufferedOutputStream.write(mbytearray, 0, current); //Write array from 0 to current size
            bufferedOutputStream.flush();
            System.out.println("Recieved: (" + directory + ") from (" + source + ") " + mbytearray.length + " bytes");

        }
        catch (Exception e) {

        }
        finally {
            //Close your streams even though java GC will do it for you  >_> 
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch (IOException ex) {
                    System.out.println("Can't close fileOutputStream");
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                }
                catch (IOException ex) {
                    System.out.println("Can't close BufferedOutputStream");
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (IOException ex) {
                    System.out.println("Can't close socket");
                }
            }
        }

    }
}
