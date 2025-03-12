package com.fgouget.framework.util;

import javax.inject.Named;

@Named
public class Logger {

    public void log(String message){
        System.out.println(message);
    }
}
