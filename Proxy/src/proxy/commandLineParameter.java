package proxy;

import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author SRK
 */
public class commandLineParameter {

    public int port;
    public List<String> badWords;
    public int filterLen;

    public commandLineParameter() {
        port = 2020;
        badWords = new ArrayList();
        filterLen = 0;
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            if (arg.substring(0, 2).matches("-P")) {
                port = Integer.parseInt(arg.substring(2));
            } else {
                arg = arg.toLowerCase();
                badWords.add(arg.replaceAll("_", " "));
                if (filterLen < arg.length()) {
                    filterLen = arg.length();
                }
            }
        }
    }
}
