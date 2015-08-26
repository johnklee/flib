package flib.util.system.thd;

import java.io.InputStream;

import com.jcraft.jsch.Channel;

import flib.util.inner.enums.EStreamType;
import flib.util.system.ISpawnProcess;

public class ByteStreamGobbler implements Runnable{
	private StringBuffer 				logMsg = null;			// Log STDERR/STDOUT output
	private InputStream 				is;						// STDERR/STDOUT InputStream obj
	private EStreamType 				type; 					// Stream type: ERROR/OUTPUT
	private boolean 					isShow = true;			// Flag to show output in console or not
	private ISpawnProcess				process = null;			// Process which causes STDERR/STDOUT
	//private BufferedReader				reader = null;			// STDERR/STDOUT BufferedReader obj
	private boolean						bKeep=true;				// Flag to keep log or not
	private Channel						channel;
	private int							escapeCharacter=0x1d;	// Sign to stop interact mode				
	
	/*public ByteStreamGobbler(ISpawnProcess process, BufferedReader reader, EStreamType type, boolean isShow, StringBuffer sbuf) {
		this.process=process;
		this.reader = reader;
		this.type = type;
        this.isShow = isShow;
        this.logMsg = sbuf;					
	}*/
		
	public ByteStreamGobbler(ISpawnProcess process, Channel channel, StringBuffer logMsg) throws Exception
	{
		this.channel = channel;
		this.is = channel.getInputStream();
		this.logMsg = logMsg;
		this.process = process;
		this.type = EStreamType.OUTPUT;
	}
	
	public void close()
	{
		try
		{
			if(is!=null) 
			{
				//System.out.printf("\t[Test] Closing reader...\n");
				is.close();
			}
		}
		catch(Exception e){e.printStackTrace();}
		bKeep=false;
	}
	
	public void run() {		        
        try
        {
        	byte[] tmp=new byte[1024];
        	while(bKeep){
        		while(is.available()>0){
        			int i=is.read(tmp, 0, 1024);
        			if(i<0)break;
        			/*for(int s=0; s<i; s++) 
        				if(tmp[s]==escapeCharacter)
        				{
        					// Stop interactive mode
        					
        				}*/
        			System.out.print(new String(tmp, 0, i));
        		}
        		if(channel.isClosed()){
        			if(is.available()>0) continue;
        			System.out.println("exit-status: "+channel.getExitStatus());
        			break;
        		}
        		try{Thread.sleep(1000);}catch(Exception ee){}
        	}
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
		//System.out.printf("\t[Test] Exit Globber thread (%s)...\n", type);
	}
}
