/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.io.*;
import java.net.*;

/**
 *
 * @author SRK
 */
public class ClientThread extends Thread {

    Socket client;
    OutputStream serverOutput;
    commandLineParameter argi;
    boolean isText;

    public ClientThread(String host, int port, String data, boolean text, OutputStream serverStream, commandLineParameter clp) throws IOException {
        argi = clp;
        client = new Socket(host, port);
        isText = text;
        PrintWriter stream = new PrintWriter(client.getOutputStream(), true);
        stream.write(data);
        stream.flush();

        serverOutput = serverStream;
    }

    @Override
    public void run() {
        try {
            int chunk;  // stores the size of the current chunk
            byte[] buffer = new byte[2048];  //stores a part of the data
            InputStream inputStream = client.getInputStream();  //input stream of the client
            String oldBuffer = new String();  //stores a small part of the last buffer (to check for bad words being split between two chunks)

            while ((chunk = inputStream.read(buffer)) != -1) {
                if (isText && BadContent(oldBuffer, buffer, chunk)) {
                    RequestBadContentURL();
                    //System.out.println("Bad content!!!");
                    client.close();
                    return;
                } else {

                    // Clear the old buffer for storage of new data
                    oldBuffer = "";

                    // Store a new part of data from the current buffer
                    if (chunk - argi.filterLen > 0) {
                        for (int i = chunk - argi.filterLen; i < chunk; ++i) {
                            oldBuffer += (char) buffer[i];
                        }
                    }

                    // write the current buffer to the server
                    System.err.println(new String(buffer,"ASCII"));
                    serverOutput.write(buffer, 0, chunk);
                    serverOutput.flush();
                }
            }

        } catch (IOException e) {
            System.err.println(e);
        }
        //System.out.println("Closing Client");
    }

    private boolean BadContent(String oldBuffer, byte[] buffer, int chunk) throws UnsupportedEncodingException {
        String data = new String();
        if (oldBuffer != null) {
            data = oldBuffer;
        }
        data += new String(buffer, "ASCII").substring(chunk);

        //System.err.print("  : " + data);
        for (String word : argi.badWords) {
            if (data.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    private void RequestBadContentURL() throws IOException {
        /*
         This function will make a new client connected
         to the badContent URL and start it.
         */

        String host = "www.ida.liu.se";
        String request;
        request = "GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error2.html HTTP/1.1\r\n";
        request += "Host: www.ida.liu.se\r\n";
        request += "Connection: close\r\n";
        request += "\r\n";
        new ClientThread(host, 80, request, false, serverOutput, argi).start();

        //serverOutput.write(request.getBytes());
    }
}
