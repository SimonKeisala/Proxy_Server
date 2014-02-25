/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.net.*;
import java.io.*;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author SRK
 */
public class Proxy {

    /**
     * @param args the command line arguments
     */
    private static void startTimer(int delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                System.out.println("Proxy: Timer: Number of threads running: " + threadSet.size());
            }
        }, 0, delay);
    }

    public static void main(String[] args) {
        // Creating variables needed in the future
        commandLineParameter argi = new commandLineParameter();
        argi.parseCommandLine(args); //argi contains the data, in this case only port number
        ServerSocket ss;
        
        // Writing out data
        System.out.println("Using port: " + argi.port);
        System.out.println("Filtered words:");
        for (String word : argi.badWords) {
            System.out.println(word);
        }

        //startTimer(5000);  //starting a timer on 2 seconds writing number of alive threads
        
        try {
            ss = new ServerSocket(argi.port);
            while (true) {
                //System.out.println("Connecting...");
                new ServerThread(ss.accept(), argi).start();
                //System.out.println("New server connected!");
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
