/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package flib.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import flib.env.Envset;
import flib.proto.IDebug;

/**
 *
 * @author John-Lee
 */
public class JDebug implements IDebug{
    private Logger log;
    private String id;
    private String logName=null;
    
    public enum EFormatType {
    	XML,Simple
    }

    public JDebug(){
        this("JDebug");
    }

    public JDebug(String name) {
        Envset.checkEnv();
        logName = Envset.getDebugFilePath();
        if(name!=null && !name.isEmpty()) {
            id = name;
        }
        log = Logger.getLogger(id);
        initialize(log);
    }

    public JDebug(String name, String fname) {
    	Envset.checkEnv();
    	logName = fname;
        if(name!=null && !name.isEmpty()) {
            id = name;
        }
        log = Logger.getLogger(id);
        initialize(log);        
    }
    
    protected void initialize(Logger log){
    	if(logName!=null) {
    		try{
    			FileHandler fileHandler = new FileHandler(logName, Envset.getLogFileSizeLimit(), 1);
                switch(Envset.getFormatType()){
                case Simple:
                	fileHandler.setFormatter(new SimpleFormatter());
                	break;            
                }
                fileHandler.setLevel(Envset.getLogFileHandlerLevel());                  
                log.addHandler(fileHandler);
    		}catch(Exception e){e.printStackTrace();}
    	}
    }
    
    public void debug(Object msg) {
        debug(msg,Level.INFO);
    }

    public synchronized void debug(Object msg, Level level) {        
        if(Envset.isDebugFlag()) {
            log.log(level, msg.toString());            
        }else {
            //log = Logger.getLogger(id);
        }
    }

    public static Logger getLogger(String name, String fname) throws IOException{
    	Envset.checkEnv();
        if(Envset.isDebugFlag()) {
            Logger log = Logger.getLogger(name);
            FileHandler fileHandler = new FileHandler(fname);
            log.addHandler(fileHandler);
            return log;
        }else {
            Logger log = Logger.getLogger(name);
            FileHandler fileHandler = new FileHandler(fname);
            log.addHandler(fileHandler);
            log.setLevel(Level.SEVERE);
            return log;
        }
    }
    public static Logger getLogger(String name) {
        Envset.checkEnv();
        if(Envset.isDebugFlag()) {
            Logger log = Logger.getLogger(name);
            return log;
        }else {
            Logger log = Logger.getLogger(name);
            log.setLevel(Level.SEVERE);
            return log;
        }
    }
}
