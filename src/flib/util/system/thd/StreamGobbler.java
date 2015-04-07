package flib.util.system.thd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import flib.util.ExecCmd;
import flib.util.inner.enums.EStreamType;
import flib.util.system.ISpawnProcess;

public class StreamGobbler implements Runnable{
	private StringBuffer 				logMsg = null;			// Log STDERR/STDOUT output
	private InputStream 				is;						// STDERR/STDOUT InputStream obj
	private EStreamType 				type; 					// Stream type: ERROR/OUTPUT
	private boolean 					isShow = true;			// Flag to show output in console or not
	private ISpawnProcess				process = null;			// Process which causes STDERR/STDOUT
	private BufferedReader				reader = null;			// STDERR/STDOUT BufferedReader obj
	private boolean						bKeep=true;				// Flag to keep log or not	
	private int							waitPeriod=500;
	
	public StreamGobbler(ISpawnProcess process, BufferedReader reader, EStreamType type, boolean isShow, StringBuffer sbuf) {
		//System.out.printf("\t[Test] Start %s thread...\n", type);
		this.process=process;
		this.reader = reader;
		this.type = type;
        this.isShow = isShow;
        this.logMsg = sbuf;					
	}
	
	public StreamGobbler(ISpawnProcess process, InputStream is, EStreamType type, boolean isShow, StringBuffer sbuf) {
		this.process = process;
        this.is = is;
        this.type = type;
        this.isShow = isShow;
        this.logMsg = sbuf;
    }
	
	public StreamGobbler(InputStream is, StringBuffer logMsg)
	{
		this.logMsg = logMsg;
		this.is = is;
	}
	
	public StreamGobbler(InputStream is)
	{
		this.is = is;
		this.logMsg = new StringBuffer();
	}
	
	public void close()
	{
		try
		{
			if(reader!=null) 
			{
				//System.out.printf("\t[Test] Closing reader...\n");
				reader.close();
			}
		}
		catch(Exception e){e.printStackTrace();}
		bKeep=false;
	}
	
	public void run()
	{
		try
        {
        	byte[] tmp=new byte[1024];
        	while(bKeep){        		
        		while(is.available()>0)
        		{
        			//System.out.printf("\t[Test] Read...start\n");
        			int i=is.read(tmp, 0, 1024);
        			//System.out.printf("\t[Test] Read...done\n");
        			if(i<0)break;
        			String msg = new String(tmp, 0, i);
        			if(isShow) System.out.print(msg);
        			if(logMsg!=null) logMsg.append(msg);
        		}        		
        		try{Thread.sleep(waitPeriod);}catch(Exception ee){}
        	}
        }
        catch(Exception e){}
        
		//System.out.printf("\t[Info] %s thread: bye...\n", type);
        if(process!=null)
        {	        	
        	switch(type)
        	{
        	case OUTPUT:
        		process.SOutDone();
        		break;
        	case ERROR:
        		process.SErrDone();
        		break;
        	}
        }
	}
	
	/*@Override
	public void run() {
		if(reader==null) 
        {            	
            reader = new BufferedReader(new InputStreamReader(is));
        }            
        String line = null;
        
        try
        {
        	do {                	
            	line = reader.readLine();
                if(line!=null)
                {
                	if(isShow) 
                    {                	
                        System.out.println((ExecCmd.hasPrefix?(type.toString() + ">"):"") + line);
                        System.out.flush();
                    }                
                    if(logMsg!=null) logMsg.append((ExecCmd.hasPrefix?(type.toString() + ">"):"") + line+"\n");
                }
            } while(line!=null && bKeep);
        }
        catch(Exception e){e.printStackTrace();}
        
        if(process!=null)
        {	        	
        	switch(type)
        	{
        	case OUTPUT:
        		process.SOutDone();
        		break;
        	case ERROR:
        		process.SErrDone();
        		break;
        	}
        }		
	}*/
}
