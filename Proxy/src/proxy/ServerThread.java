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
public class ServerThread extends Thread {

    Socket server;
    commandLineParameter argi;
    

    private static String receiveData(BufferedReader stream) throws IOException {
        String data = new String();
        String line;

        while (true) {
            stream.ready();
            line = stream.readLine();
            if (line != null && line.length() != 0) {
                data += line + "\r\n";
            } else {
                break;
            }
        }
        if (data.length() == 0) { //server disconnected
            return null;
        }
        return data;
    }

    public ServerThread(Socket s, commandLineParameter clp) {
        server = s;
        argi = clp;
    }

    @Override
    public void run() {
        try {
            BufferedReader fromBrowser = new BufferedReader(new InputStreamReader(server.getInputStream()));
            OutputStream toBrowser = server.getOutputStream();
            Request req;

            // From here on the server is connected to the browser
            // serverInput and serverOutput streams exist
            while (!server.isClosed()) {
                // Step 1, make the client connect to the internet page.
                // To do that, first read the entire inputstream from the server.

                // Storing the input stream to a string.
                String data = receiveData(fromBrowser);

                if (data == null) {
                    server.close();

                } // else
                else {
                    // Putting the data to the request class
                    req = new Request(data);

                    if (req.isText() && req.filter(argi.badWords)/*filter*/) {
                        // if bad word is found
                        badURL();
                    } else if (req.getVal("host") != null) {

                        // Step 2, create a new thread handling the client.
                        //System.out.println("Opening client");
                        req.setVal("connection", "close");
                        ClientThread client;
                        //System.out.println(req.getFullRequest());

                        client = new ClientThread(req.getVal("host"), req.requestPort, req.getFullRequest(), req.isText(), toBrowser, argi);
                        //System.out.println("New Client connection");
                        client.start();

                    } else {

                        //System.out.println("Empty message!");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        //System.out.println("Closing Server!");
    }

    private void badURL() throws IOException {
        String host = "www.ida.liu.se";
        String request;
        request = "GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error1.html HTTP/1.1\r\n";
        request += "Host: www.ida.liu.se\r\n";
        request += "Connection: close\r\n";
        request += "\r\n";
        new ClientThread(host, 80, request, false, server.getOutputStream(), argi).start();

    }
}
