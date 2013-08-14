/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package flib.util;

import flib.util.enums.EExecuteProgram;
import flib.util.inner._StreamGobbler;
import flib.util.inner.enums.EStreamType;
import java.io.*;

/**
 *
 * @author John-Lee
 */
public class ExecCmd {
    private String cmdline;
    private String params = null;
    private StringBuffer soutBuf;
    private StringBuffer serrBuf;
    private boolean bSout = false;
    private boolean bSerr = false;
    public static boolean hasPrefix = true;
    
    private int lastExitValue = -1;

    public ExecCmd()
    {
    	
    }

    public ExecCmd(String cl){
    	this();
        if(cl!=null && !cl.isEmpty()){
            cmdline = cl;
        }
    }

    public ExecCmd(String cl, String params){
        this(cl);
        if(params!=null && !params.isEmpty()) {
            this.params = params;
        }
    }
    
    public boolean isDone(){ return bSout&bSerr;}
    public void SOutDone(){bSout=true;}
    public void SErrDone(){bSerr=true;}
    public void appendSOut(String line){soutBuf.append(line);}
    public String getSOut(){return soutBuf.toString();}
    public String getSErr(){return serrBuf.toString();}
    public void appendSErr(String line){serrBuf.append(line);}

    public String exec(boolean show) throws IOException {
        return exec(getCmdline()+getParams(),show);
    }

    public String exec() throws IOException{
       return exec(getCmdline()+getParams(), false);
    }

    public String execProgram() throws IOException{
        String tmpCmdline = getCmdline();
        String subfn = tmpCmdline.substring(tmpCmdline.lastIndexOf(".")+1, tmpCmdline.length());
        return exec(EExecuteProgram.getExecuteProgram(subfn)+" "+tmpCmdline+getParams());
    }

    public void exec(String cmdline, StringBuffer rst) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        String line = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader sb = null;
        process = runtime.exec(cmdline);
        is = process.getInputStream();
        isr = new InputStreamReader(is);
        sb = new BufferedReader(isr);
        while ((line = sb.readLine()) != null) {
            rst.append(line+"\r\n");
        }
        is.close();
        isr.close();
        sb.close();
    }

    public void exec(String cmdline,String keyword) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        String line = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader sb = null;
        process = runtime.exec(cmdline);
        is = process.getInputStream();
        isr = new InputStreamReader(is);
        sb = new BufferedReader(isr);
        while ((line = sb.readLine()) != null) {
            System.out.println(line);
            if(line.contains(keyword)){
                break;
            }
        }
        is.close();
        isr.close();
        sb.close();
    }

    public String execWithStdoutReturn(boolean isShow) throws IOException {
        return execWithStdoutReturn(cmdline,isShow, false);
    }
    
    public int silenctExec(String cmdline, boolean isShowOnErr) throws IOException {
    	try {
            Runtime runtime = Runtime.getRuntime();
            Process process = null;            
            BufferedReader sb = null;
            process = runtime.exec(cmdline);
            InputStream is;
            InputStreamReader isr;
            String line;
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            sb = new BufferedReader(isr);
            while ((line = sb.readLine()) != null) {
            	System.out.println(line);
                System.out.flush();
            }
            is.close();
            isr.close();
            sb.close();

            // any error message?
            _StreamGobbler errorGobbler = new _StreamGobbler(process.getErrorStream(), EStreamType.ERROR, isShowOnErr);
            // any output?
           // _StreamGobbler outputGobbler = new _StreamGobbler(process.getInputStream(), EStreamType.OUTPUT, isShow);

            // kick them off
            errorGobbler.start();
            //outputGobbler.start();
            lastExitValue = process.waitFor();
            Thread.sleep(100);
            return lastExitValue;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public void futureExec(String cmdline, boolean isShow) throws IOException{
    	try {
    		soutBuf = new StringBuffer();
        	serrBuf = new StringBuffer();
            Runtime runtime = Runtime.getRuntime();
            Process process = null;            
            process = runtime.exec(cmdline);

            // any error message?
            _StreamGobbler errorGobbler = new _StreamGobbler(process.getErrorStream(), EStreamType.ERROR, this, isShow, hasPrefix);
            // any output?
            _StreamGobbler outputGobbler = new _StreamGobbler(process.getInputStream(), EStreamType.OUTPUT, this, isShow, hasPrefix);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            lastExitValue = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
    
    public String execWithStdoutReturn(String cmdline, boolean isShow) throws IOException 
    {
    	return this.execWithStdoutReturn(cmdline, isShow, false);
    }
    
    public Tuple execWithStdoutStderrReturn(String cmdline, boolean isShow) throws IOException{
    	try {
            StringBuffer stdOut = new StringBuffer("");
            StringBuffer errOut = new StringBuffer("");
            Runtime runtime = Runtime.getRuntime();
            Process process = null;            
            process = runtime.exec(cmdline);

            // any error message?
            _StreamGobbler errorGobbler = new _StreamGobbler(process.getErrorStream(), EStreamType.ERROR, isShow, errOut);            
            
            // any output?
            _StreamGobbler outputGobbler = new _StreamGobbler(process.getInputStream(), EStreamType.OUTPUT, isShow, stdOut);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            lastExitValue = process.waitFor();
            Thread.sleep(100);
            return new Tuple(stdOut.toString(), errOut.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
        return null;
    }
    
    public String execWithStdoutReturn(String cmdline, boolean isShow, boolean pipeError) throws IOException {
        try {
            StringBuffer res = new StringBuffer("");
            Runtime runtime = Runtime.getRuntime();
            Process process = null;            
            process = runtime.exec(cmdline);

            // any error message?
            _StreamGobbler errorGobbler = null;
            
            if(pipeError) errorGobbler = new _StreamGobbler(process.getErrorStream(), EStreamType.ERROR, isShow, res);
            else errorGobbler = new _StreamGobbler(process.getErrorStream(), EStreamType.ERROR, isShow);
            // any output?
            _StreamGobbler outputGobbler = new _StreamGobbler(process.getInputStream(), EStreamType.OUTPUT, isShow, res);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            lastExitValue = process.waitFor();
            Thread.sleep(100);
            return res.toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
        return "";
    }

    public String exec(String cmdline,boolean isShow) throws IOException{
        try {
            StringBuffer res = new StringBuffer("");
            Runtime runtime = Runtime.getRuntime();
            Process process = null;            
            process = runtime.exec(cmdline);

            // any error message?
            _StreamGobbler errorGobbler = new _StreamGobbler(process.getErrorStream(), EStreamType.ERROR, isShow, res);
            //errorGobbler.setHasPrefix(hasPrefix);
            // any output?
            _StreamGobbler outputGobbler = new _StreamGobbler(process.getInputStream(), EStreamType.OUTPUT, isShow, res);
            //outputGobbler.setHasPrefix(hasPrefix);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            lastExitValue = process.waitFor();
            //System.out.println("ExitValue: " + exitVal);
            Thread.sleep(100);
            return res.toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String exec(String cmdline) throws IOException{
        return exec(cmdline, false);
    }

    public String getExecuteCmd() {
        return getCmdline()+getParams();
    }

    /**
     * @return the cmdline
     */
    public String getCmdline() {
        return cmdline;
    }

    /**
     * @param cmdline the cmdline to set
     */
    public void setCmdline(String cmdline) {
        this.cmdline = cmdline;
    }

    /**
     * @return the params
     */
    public String getParams() {
        if(params!=null && !params.isEmpty()) {
            return " "+params;
        } else {
            return "";
        }
    }

    /**
     * @param params the params to set
     */
    public void setParams(String params) {
        this.params = params;
    }

    /**
     * @return the lastExitValue
     */
    public int getLastExitValue() {
        return lastExitValue;
    }    
}
