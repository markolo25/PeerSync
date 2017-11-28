package peerSync.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Mark Levie Mendoza <markolo25@gmail.com>
 */
public class TransferSend extends Thread {

    private int port;
    private String directory;

    /**
     * TransferRecieve Constructor, fills in attributes needed to transfer file
     * to a computer
     *
     * @param port
     * @param directory
     */
    public TransferSend(int port, String directory) {
        this.port = port;
        this.directory = directory;
    }

    @Override
    public void run() {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        OutputStream outputStream = null;
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            //Create a server socket
            serverSocket = new ServerSocket(port);

            while (true) {
                System.out.println("Server Open...");

                try {
                    socket = serverSocket.accept();
                    System.out.println("Connected to " + socket);

                    File file = new File(directory);
                    byte[] mbytearray = new byte[(int) file.length()];
                    fileInputStream = new FileInputStream(file);
                    bufferedInputStream = new BufferedInputStream(fileInputStream);
                    bufferedInputStream.read(mbytearray, 0, mbytearray.length);
                    outputStream = socket.getOutputStream();
                    System.out.println("Sending " + directory + " " + mbytearray.length + " bytes");
                    outputStream.write(mbytearray, 0, mbytearray.length);
                    outputStream.flush();
                    System.out.println("Done. Killing Server In charge of: " + directory);
                    break;

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (bufferedInputStream != null) {
                        bufferedInputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

}
