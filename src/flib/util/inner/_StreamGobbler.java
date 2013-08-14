/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package flib.util.inner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import flib.util.ExecCmd;
import flib.util.inner.enums.EStreamType;

/**
 *
 * @author John-Lee
 */
public class _StreamGobbler extends Thread {
	private StringBuffer 				logMsg = null;
    private InputStream 				is;
    private EStreamType 				type; //输出流的类型ERROR或OUTPUT
    private boolean 					isShow = true;
    private ExecCmd						cmd = null;

    public _StreamGobbler(InputStream is, EStreamType type) {
    	this(is, type, false, null);
    }

    public _StreamGobbler(InputStream is, EStreamType type, boolean isShow)
    {
    	this(is, type, isShow, null);
    }
    
    public _StreamGobbler(InputStream is, EStreamType type, boolean isShow, StringBuffer sbuf) {        
        this.is = is;
        this.type = type;
        this.isShow = isShow;
        this.logMsg = sbuf;
    }
    public _StreamGobbler(InputStream is, EStreamType type, ExecCmd cmd, boolean isShow, boolean hasPrefix)
    {
    	this.is = is;
    	this.type = type;
    	this.cmd = cmd;
    	this.isShow = isShow;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if(isShow) {                	
                    System.out.println((ExecCmd.hasPrefix?(type.toString() + ">"):"") + line);
                    System.out.flush();
                }                
                if(logMsg!=null) logMsg.append((ExecCmd.hasPrefix?(type.toString() + ">"):"") + line+"\n");
                if(cmd!=null)
                {
                	switch(type)
                	{
                	case OUTPUT:
                		cmd.appendSOut(line+"\n");
                		break;
                	case ERROR:
                		cmd.appendSErr(line+"\n");
                		break;
                	}
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if(cmd!=null)
        {
        	switch(type)
        	{
        	case OUTPUT:
        		cmd.SOutDone();
        		break;
        	case ERROR:
        		cmd.SErrDone();
        		break;
        	}
        }
    }

    /**
     * @return the isShow
     */
    public boolean isIsShow() {
        return isShow;
    }

    /**
     * @param isShow the isShow to set
     */
    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }   
}
