/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SRK
 */
public class Request {

    List<Pair<String, String>> resources;
    String requestType;
    int requestPort;
    boolean isText;

    public Request(String data) {
        isText = false;
        resources = new ArrayList();
        String[] lines = data.split("\r\n");

        requestType = lines[0].replaceFirst("CONNECT", "GET");

        for (int i = 1; i < lines.length; ++i) {
            // rename the current line
            String currLine = lines[i];

            // split the current line into two words, key + value
            String[] words = currLine.split(": ", 2);

            // set the key to the first element and value to the second element
            String key = words[0].toLowerCase();
            String val = words[1];

            // Do some renames and checks
            switch (key) {
                case "proxy-connection":
                    key = "connection";
                    break;
                case "accept":
                    isText = (val.contains("html") || val.contains("text"));
                    break;
                case "host":
                    String[] host = val.split(":");
                    if (host.length == 2) {
                        //System.out.println("host split, port: " + host[1]);
                        requestPort = Integer.parseInt(host[1]);
                        val = host[0];
                    } else {
                        requestPort = 80;
                    }
                    break;
            }

            // add the final key + value to the resources
            resources.add(new Pair(key, val));
        }
        // Modify the GET-request
        // Not implemented in this version (not yet fully tested)
        /*
         String[] ReqPhase1 = requestType.split(" ");
         if (ReqPhase1.length != 3) {
         System.err.println("Request Type corrupt!");
         return;
         }
         String[] ReqPhase2 = ReqPhase1[1].split(":");
         int ReqSize = ReqPhase2.length;
         if (Integer.parseInt(ReqPhase2[ReqSize - 1]) > 0) {
         //System.err.println("Port included in request type!");
         requestType = ReqPhase1[0] + " ";
         for (int i = 0; i < ReqSize - 1; ++i) {
         requestType += ReqPhase2[i];
         if (i < ReqSize - 2) {
         requestType += ":";
         }
         }
         requestType += " " + ReqPhase1[2];
         }
         System.out.println(requestType);
         */
    }

    public String getVal(String key) // solution has ordo(n) time!
    {
        /* 
         This function loops through the entire list.
         If the key is found within the list,
         the value of that key is returned.
         If the key is not found this function returns null.
         */
        for (int i = 0; i < resources.size(); ++i) {
            if (resources.get(i).first.equals(key)) {
                //System.out.println("Request debug getVal (" + key + ") - returns: " + resources.get(i).second);
                return resources.get(i).second;
            }
        }
        //System.out.println("Request debug getVal (" + key + ") - key not found!");
        return null;
    }

    public boolean filter(List<String> fWords) {

        for (String word : fWords) {
            if (requestType.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    public boolean isText() {
        return isText;
    }

    public boolean setVal(String key, String val) // solution has ordo(n) time!
    {
        /* 
         This function loops through the entire list.
         If the key is found within the list,
         the value for that key is changed  
         */
        for (int i = 0;
                i < resources.size();
                ++i) {
            if (resources.get(i).first.equals(key)) {
                //System.out.println("New value set for key (" + key + "): " + val);
                resources.get(i).second = val;
                return true;
            }
        }

        return false;
    }

    public String getFullRequest() {

        // Recreating the request
        String req = new String();

        // Add the requestType (the GET-line)
        req += requestType + "\r\n";

        // Add each other resource existing in the resource list
        for (int i = 0; i < resources.size(); ++i) {
            req += resources.get(i).first + ": " + resources.get(i).second + "\r\n";
        }
        // Add the final new line and return
        req += "\r\n";
        return req;
    }
}
