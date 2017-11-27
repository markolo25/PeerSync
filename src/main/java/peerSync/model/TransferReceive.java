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
public class TransferReceive extends Thread {

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
    }

    @Override
    public void run() {
        int bytesRead;
        int current = 0;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream buffer = null;
        Socket socket = null;
        try {
            byte[] mbytearray = new byte[1024 * 1024 * 16]; //MAX Size = 16 megabytes

            InputStream inputStream = socket.getInputStream();
            fileOutputStream = new FileOutputStream(directory);

            buffer = new BufferedOutputStream(fileOutputStream);

            //Read mbytearray starting at the 0th position up to it's length
            bytesRead = inputStream.read(mbytearray, 0, mbytearray.length);

            //Store where you end off reading in current
            current = bytesRead;

            do {
                //Starting from where you left off read the length, minus the amount you read sofar
                inputStream.read(mbytearray, current, mbytearray.length - current);
                if (bytesRead >= 0) {
                    current += bytesRead;
                }
            } while (bytesRead > -1); //if there's nothing left to read you're done

            buffer.write(mbytearray, 0, current); //Write array from 0 to current size
            buffer.flush();
            System.out.println("File " + directory + " downloaded (" + current + " bytes read)");

        } catch (Exception e) {

        } finally {
            //Close your streams even though java GC will do it for you  >_> 
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(TransferReceive.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException ex) {
                    Logger.getLogger(TransferReceive.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(TransferReceive.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    /**
     * Preventing overwrites by checking if file sohuld be replaced or not.
     * @param newFile
     * @return 
     */
    public boolean replaceFile(File newFile) {
        File tmpDir = new File(directory);
        if (tmpDir.exists()) {

        } else {

        }
        return true;
    }
}
