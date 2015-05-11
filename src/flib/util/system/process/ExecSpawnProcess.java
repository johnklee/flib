package flib.util.system.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import flib.util.inner.enums.EStreamType;
import flib.util.system.ISpawnProcess;
import flib.util.system.thd.ExStderrThd;
import flib.util.system.thd.ExStdoutThd;
import flib.util.system.thd.StreamGobbler;
import flib.util.system.thd.WatchDog;

public class ExecSpawnProcess implements ISpawnProcess{
	private List<String> 			lastExpect = new ArrayList<String>();
	private StreamGobbler 			infoGobbler=null;
	private ExecutorService 		executor = Executors.newFixedThreadPool(2);
	private String 					command=null;
	private String					before=null;
	private String					after=null;
	private boolean 				bSout = false;
	private boolean 				bSerr = false;		
	private StringBuffer 			errBuf = null;		
	private StringBuffer			preErrBuf = new StringBuffer();
	private StringBuffer 			infBuf = null;
	private boolean 				bShow = true;
	private OutputStream 			termOS = null;
	private BufferedWriter 			termOSWriter = null;
	private InputStream 			termIS = null;
	private InputStream 			termER = null;
	private BufferedReader 			termISReader = null;
	private Process 				process = null;
	private int 					lastExitValue=-1;
	private StreamGobbler 			errorGobbler;
	private Thread 					errorGlobberThd=null;
	private BufferedReader 			sysISReader = null;	
	private Pattern					loginprompt = Pattern.compile("[$#>]");
	public  int 					pid=-1;
	private OutputStream			logfile=null;
	private WatchDog 				watchDog=null;
			
	public ExecSpawnProcess(String cmd){this.command=cmd;}

	public void start() throws Exception {
		errBuf = new StringBuffer();			
		infBuf = new StringBuffer();
		Runtime runtime = Runtime.getRuntime();
		process = runtime.exec(command);				
		if(process!=null)
		{
			if(process.getClass().getName().equals("java.lang.UNIXProcess"))
			{
				try {
					Field f = process.getClass().getDeclaredField("pid");
					f.setAccessible(true);
					pid = f.getInt(process);
				} catch (Throwable e) {
				}						
			}
		}

		// any error message?
		termER = process.getErrorStream();
		errorGobbler = new StreamGobbler(this, termER,
										 EStreamType.ERROR, 
										 bShow, 
										 errBuf);
		errorGlobberThd = new Thread(errorGobbler);
		errorGlobberThd.start();
		termOS = process.getOutputStream();
		termIS = process.getInputStream();	
		watchDog = new WatchDog(this, process);
		new Thread(watchDog).start();
	}
	
	public void sendcontrol(char c) throws Exception
	{
		if(process!=null)
		{
			termOS.write(c);
			termOS.flush();
		}
	}
				
	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void send(String msg) throws Exception
	{
		if(termOSWriter==null) termOSWriter = new BufferedWriter(new OutputStreamWriter(termOS));
		termOSWriter.write(String.format("%s", msg));
		termOSWriter.flush();
	}
	
	public void sendLine(String line) throws Exception
	{
		if(termOSWriter==null) termOSWriter = new BufferedWriter(new OutputStreamWriter(termOS));
		//System.out.println(line);
		termOSWriter.write(String.format("%s\r\n", line));
		termOSWriter.flush();
	}
	
	public List<String> readLines(int timeout)
	{
		List<String> lines = new ArrayList<String>();
		try
		{
			while(true) lines.add(readLine(timeout));
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return lines;
	}
	
	public List<Byte> read(int size, int timeout) throws Exception
	{
		List<Byte> bytes = new ArrayList<Byte>();
		Callable<Byte> readTask = new Callable<Byte>() {		        
	        public Byte call() throws Exception {
	            return new Byte((byte)termIS.read());
	        }
	    };
		
	    try
	    {
	    	if(size>0)
	    	{
	    		for(int i=0; i<size; i++)
		    	{
		    		Future<Byte> future = executor.submit(readTask);
		    		bytes.add(future.get());
		    	}
	    	}
	    	else
	    	{
	    		while(true) bytes.add(executor.submit(readTask).get());
	    	}
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
		
		return bytes;
	}
	
	public String readLine() throws Exception
	{
		if(termISReader==null)
		{
			termISReader = new BufferedReader(new InputStreamReader(termIS));
		}
		return termISReader.readLine();
	}
	
	public String readLine(int timeout) throws Exception
	{			
		if(termISReader==null)
		{
			termISReader = new BufferedReader(new InputStreamReader(termIS));
		}
		Callable<String> readTask = new Callable<String>() {		        
	        public String call() throws Exception {
	            return termISReader.readLine();
	        }
	    };
	    Future<String> future = executor.submit(readTask);
        return future.get(timeout, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public Future<Boolean> expect_asyn(String pattern)
			throws Exception {
		final Pattern ptn = Pattern.compile(pattern);	
		if(termISReader==null) termISReader = new BufferedReader(new InputStreamReader(termIS));
		
		lastExpect.clear();		
		Callable<Boolean> readTask = new Callable<Boolean>() {		        
	        public Boolean call() throws Exception {
	            while(true)
	            {
	            	if(ptn.matcher(errBuf.toString()).find())
        			{        				
        				preErrBuf.delete(0, preErrBuf.length());
        				preErrBuf.append(errBuf.toString());
        				errBuf.delete(0, errBuf.length());
        				return true;
        			}
        			try{Thread.sleep(200);}catch(Exception e){}
        			
        			
        			String line = termISReader.readLine();
					if(line==null) continue;
					lastExpect.add(line);					
					if(ptn.matcher(line).find()) return true;															
	            }
	        }
	    };
	    
		return executor.submit(readTask);
	}
	
	public boolean expect_async(String pattern, int timeout) throws Exception
	{
		final Pattern ptn = Pattern.compile(pattern);	
		boolean isHit=false;
		lastExpect.clear();
		ExStderrThd watchStderrThd = new ExStderrThd(errBuf, preErrBuf, ptn);		
		ExStdoutThd watchStdoutThd = new ExStdoutThd(this, lastExpect, ptn);
		
		watchStdoutThd.start();
		watchStderrThd.start();
		
		//System.out.printf("\t[Test] Start asynchronized threads...\n");
		long st = System.currentTimeMillis();
		while(true)
		{
			if(watchStdoutThd.isHit || watchStderrThd.isHit)
			{				
				isHit=true;
				break;
			}
			else if(((System.currentTimeMillis()-st)/1000)>timeout) break;
			//System.out.printf("\t[Test] Stderr:\n%s\n", errBuf.toString());
			Thread.sleep(500);
		}		
		watchStderrThd.isStop = watchStdoutThd.isStop = true;
		
		return isHit;
	}
	
	public boolean expect(String pattern, int timeout) throws Exception
	{	
		lastExpect.clear();
		Pattern ptn = Pattern.compile(pattern);
		try
		{
			 while(true)
			 {
				 // Check stderr
				 //System.out.printf("\t[Test] Check Stderr:\n'%s'\n", errBuf.toString());
				 if(ptn.matcher(errBuf.toString()).find()) return true;
				 
				 // Check stdout
				 String line = readLine(timeout);					 
				 lastExpect.add(line);
				 //System.out.printf("\t[Test] Check Stdout: '%s'...\n", line);
				 if(ptn.matcher(line).find()) return true;
			 }
		}
		catch(Exception e){}			
		return false;
	}
	
	public boolean expect(String pattern) throws Exception
	{
		lastExpect.clear();
		String line;
		Pattern ptn = Pattern.compile(pattern);
		while(!isDone())
		{
			// Check stderr
			//System.out.printf("\t[Test] Check Stderr:\n'%s'\n", errBuf.toString());
			if(ptn.matcher(errBuf.toString()).find()) return true;
			 
			// Check stdout
			line = readLine();							
			if(line==null) break;
			//lastExpect.add(line);
			System.out.printf("\t[Test] Check Stdout: '%s'...\n", line);
			if(ptn.matcher(line).find()) return true;
		}
		return false;
	}
	
	public boolean expect_exact(String pattern, int timeout) throws Exception
	{
		lastExpect.clear();			
		try
		{
			 while(true)
			 {
				 String line = readLine(timeout);					 
				 lastExpect.add(line);
				 if(line.equals(pattern)) return true;
			 }
		}
		catch(Exception e){}			
		return false;
	}
	
	public boolean expect_exact(String pattern) throws Exception
	{
		lastExpect.clear();	
		String line;
		try
		{
			 while(true)
			 {
				 line = readLine();					 
				 lastExpect.add(line);
				 if(line.equals(pattern)) return true;
			 }
		}
		catch(Exception e){throw e;}			
	}
	
	public boolean isDone(){ return (bSout&bSerr);}
    public void SOutDone()
    {
    	bSout=true;
    	//if(bSerr && sysISReader!=null) try{System.in.close();}catch(Exception e){e.printStackTrace();} 
    }
    public void SErrDone()
    {
    	bSerr=true;
    	//if(bSout && sysISReader!=null) try{System.in.close();}catch(Exception e){e.printStackTrace();}
    }
    
    public void terminate(){this.close();}
    
    public void close() 
    {
    	if(process!=null)
    	{
    		try
    		{	    		
    			
    			if (termISReader==null) infoGobbler = new StreamGobbler(this, termIS, EStreamType.OUTPUT, bShow, infBuf);
    	    	else infoGobbler = new StreamGobbler(this, termISReader, EStreamType.OUTPUT, bShow, infBuf);
    			new Thread(infoGobbler).start();
    			
    			process.destroy();	    			
	    		
	    		lastExitValue = process.waitFor();		    		
	    		if(termOSWriter!=null) 
	    		{		    			
	    			termOSWriter.close();
	    		}
	    		if(termISReader!=null) 
	    		{
	    			//this.readLines(2000);
	    			termISReader.close();
	    		}
	    		
	    		termOS.close();
	    		termIS.close();
	    		termER.close();
	    		//System.out.printf("\t[Tset] Close error gobbler...\n");
	    		
	    		if(errorGobbler!=null) errorGobbler.close();		    		
	    		if(infoGobbler!=null) 
	    		{
	    			//System.out.printf("\t[Tset] Close info gobbler...\n");
	    			infoGobbler.close();
	    		}
	    		if(sysISReader!=null) sysISReader.close();
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}	    	
    	
    	lastExitValue=-1;
    	executor.shutdownNow();
    	
    	//System.out.printf("\t[Info] Bye...(%d)\n", process.exitValue());
    	/*while(!isDone()) {
    		System.out.printf("bSout=%s; bSerr=%s...\n", bSout, bSerr);
    		try{Thread.sleep(2000);}catch(Exception e){}
    	}*/
    }
    
    public int waitUtilEnd() throws Exception
    {
    	
    	if (termISReader==null) infoGobbler = new StreamGobbler(this, termIS, EStreamType.OUTPUT, bShow, infBuf);
    	else infoGobbler = new StreamGobbler(this, termISReader, EStreamType.OUTPUT, bShow, infBuf);
    	//if(termISReader!=null) termISReader.close();
    	new Thread(infoGobbler).start();
    	lastExitValue = process.waitFor();
    	return lastExitValue;
    }
    
    public void interact() throws Exception
    {
    	if (termISReader==null) infoGobbler = new StreamGobbler(this, termIS, EStreamType.OUTPUT, bShow, infBuf);
    	else infoGobbler = new StreamGobbler(this, termISReader, EStreamType.OUTPUT, bShow, infBuf);
    	new Thread(infoGobbler).start();
    	sysISReader = new BufferedReader(new InputStreamReader(System.in));
    	try
    	{
    		String line = null;
	    	while(true)
	    	{
	    		while(!sysISReader.ready() && !isDone()) Thread.sleep(200);
	    		if(isDone()) break;
	    		line = sysISReader.readLine();
	    		if(line==null) break;
	    		this.sendLine(line);
	    	}
    	}
    	catch(Exception e)
    	{
    		//e.printStackTrace();
    	}
    	sysISReader.close();
    	close();
    }

	public void setLogfile(OutputStream os) {
		this.logfile = os;
		
	}

	public OutputStream getLogfile() {
		return this.logfile;		
	}

	public int getLastExitValue() { return lastExitValue;}
	public String getBefore(){return before;}
	public String getAfter(){return after;}

	@Override
	public int expect_exact(List<String> patterns, int timeout) throws Exception {
		lastExpect.clear();	
		String line;
		try
		{
			 while(true)
			 {
				 if(timeout>0) line = readLine(timeout);
				 else line = readLine();		
				 lastExpect.add(line);
				 for(int i=0; i<patterns.size(); i++) if(line.equals(patterns.get(i))) return i;
			 }
		}
		catch(Exception e){throw e;}			
	}	
}
