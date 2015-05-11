package flib.util.system.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import org.apache.commons.lang.StringUtils;

import flib.util.inner.enums.EStreamType;
import flib.util.system.ISpawnProcess;
import flib.util.system.thd.ByteStreamGobbler;
import flib.util.system.thd.StreamGobbler;
import flib.util.system.thd.WatchDog;
import static flib.util.system.JExpect.*;

public class ShellSpawnProcess implements ISpawnProcess{
	String 							username;
	String 							password;
	String	 						hostname;	
	private String					before=null;
	private String					after=null;
	int 							port=22;
	private Session 				session = null;
	private Channel 				channel = null;
	private StringBuffer 			infBuf = null;
	private ExecutorService 		executor = Executors.newFixedThreadPool(2);
	private boolean 				bSout = false;
	private boolean 				bSerr = false;
	private OutputStream 			termOS = null;
	private BufferedWriter 			termOSWriter = null;
	private InputStream 			termIS = null;
	private BufferedReader 			termISReader = null;
	private int 					lastExitValue=-1;
	private List<String> 			lastExpect = new ArrayList<String>();
	private ByteStreamGobbler 		infoGobbler=null;
	private boolean 				bShow = true;
	private BufferedReader 			sysISReader = null;
	private OutputStream			logfile=null;
	
	
	abstract class MyUserInfo2 implements UserInfo, UIKeyboardInteractive{
		public String getPassword(){
			return null;
		}
		public boolean promptYesNo(String str){
			return false;
		}
		public String getPassphrase(){
			return null;
		}
		public boolean promptPassphrase(String message){
			return false;
		}
		public boolean promptPassword(String message){
			return false;
		}
		public void showMessage(String message){
		}
		public String[] promptKeyboardInteractive(String destination,
				String name,
				String instruction,
				String[] prompt,
				boolean[] echo){
			return null;
		}
	}
	
	public ShellSpawnProcess(String h, String u, String p)
	{
		this.username = u;
		this.password = p;
		this.hostname = h;		
	}

	public void start() throws Exception {		
		infBuf = new StringBuffer();
		JSch jsch=new JSch();

		session=jsch.getSession(username, hostname, port);
		session.setPassword(password);
		
		UserInfo ui = new MyUserInfo2(){
			public void showMessage(String message){
				JOptionPane.showMessageDialog(null, message);
			}
			public boolean promptYesNo(String message){
				System.out.printf("\t[Info] Prompt Msg:\n'%s'...(Y/N)\n", message);
				if(message.endsWith("Are you sure you want to continue connecting?")) return true;
				return false;
			}

			// If password is not given before the invocation of Session#connect(),
			// implement also following methods,
			//   * UserInfo#getPassword(),
			//   * UserInfo#promptPassword(String message) and
			//   * UIKeyboardInteractive#promptKeyboardInteractive()

		};
		session.setUserInfo(ui);

		session.connect(SSH_CONNECT_TIMEOUT);   // making a connection with timeout.

		channel=session.openChannel("shell");
		termOS = channel.getOutputStream();
		termIS = channel.getInputStream();			
		channel.connect(3*1000);
		new Thread(new WatchDog(this, null)).start();
	}

	public void sendcontrol(char c) throws Exception {
		if(termOS!=null)
		{
			termOS.write(c);
			termOS.flush();
		}
	}

	public void send(String msg) throws Exception {
		if(termOSWriter==null) termOSWriter = new BufferedWriter(new OutputStreamWriter(termOS));
		termOSWriter.write(String.format("%s", msg));
		termOSWriter.flush();		
	}

	public void sendLine(String line) throws Exception {
		if(termOSWriter==null) termOSWriter = new BufferedWriter(new OutputStreamWriter(termOS));
		termOSWriter.write(String.format("%s\n", line));
		termOSWriter.flush();		
	}

	public List<String> readLines(int timeout) {
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

	public List<Byte> read(int size, int timeout) throws Exception {
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

	public String readLine() throws Exception {
		if(termISReader==null)
		{
			termISReader = new BufferedReader(new InputStreamReader(termIS));
		}
		return termISReader.readLine();
	}

	public String readLine(int timeout) throws Exception {
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

	public boolean expect(String pattern, int timeout) throws Exception {
		lastExpect.clear();
		before = after = null;
		Pattern ptn = Pattern.compile(pattern);
		try
		{
			 while(true)
			 {
				 String line = readLine(timeout);					 
				 lastExpect.add(line);
				 if(ptn.matcher(line).find()) 
				 {
					 after = line;
					 return true;
				 }
			 }
		}
		catch(Exception e){}			
		finally
		{
			before = StringUtils.join(lastExpect, NewLine);			
		}
		return false;
	}

	public boolean expect(String pattern) throws Exception {
		before = after = null;
		lastExpect.clear();
		String line;
		boolean bHit = false;
		Pattern ptn = Pattern.compile(pattern);
		while(!isDone())
		{
			line = readLine();							
			if(line==null) break;
			lastExpect.add(line);
			System.out.printf("\t[Test] Check '%s'...\n", line);
			if(ptn.matcher(line).find()) 
			{
				after = line;
				bHit=true;
				break;
			}
		}
		before = StringUtils.join(lastExpect, NewLine);		
		return bHit;
	}

	public boolean expect_exact(String pattern, int timeout) throws Exception {
		before = after = null;
		lastExpect.clear();			
		try
		{
			 while(true)
			 {
				 String line = readLine(timeout);					 
				 lastExpect.add(line);
				 if(line.equals(pattern))
				 {
					 after = line;
					 return true;
				 }
			 }
		}
		catch(Exception e){}	
		finally
		{
			before = StringUtils.join(lastExpect, NewLine);	
		}
		return false;
	}

	public boolean expect_exact(String pattern) throws Exception {
		before = after = null;
		lastExpect.clear();	
		String line;
		try
		{
			 while(true)
			 {
				 line = readLine();					 
				 lastExpect.add(line);
				 if(line.equals(pattern)) 
				 {
					 after = line;
					 return true;
				 }
			 }
		}
		catch(Exception e){}
		finally
		{
			before = StringUtils.join(lastExpect, NewLine);	
		}
		return false;
	}

	public void close() {
		try
		{
			if(session!=null) session.disconnect();
			if(channel!=null) channel.disconnect();
			if(sysISReader!=null) sysISReader.close();
		}
		catch(Exception e){e.printStackTrace();}
		executor.shutdownNow();
	}

	public int waitUtilEnd() throws Exception {
		infoGobbler = new ByteStreamGobbler(this, channel, infBuf);    	
    	//if(termISReader!=null) termISReader.close();
    	new Thread(infoGobbler).start();
    	while(!isDone()&&!channel.isClosed()) Thread.sleep(1000);
    	lastExitValue = channel.getExitStatus();
    	return lastExitValue;
	}

	public void interact() throws Exception
	{
		channel.setInputStream(System.in);
		channel.setOutputStream(System.out);		
		infoGobbler = new ByteStreamGobbler(this, channel, infBuf);    	
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
	    		sendLine(line);   		
	    	}
    	}
    	catch(Exception e)
    	{
    		//e.printStackTrace();
    	}
    	//sysISReader.close();    	
		while(!channel.isClosed()) Thread.sleep(1000);
		infoGobbler.close();
    	close();    	
	}
	
	public boolean isDone(){ return bSout&bSerr||channel.isClosed();}
    public void SOutDone()
    {
    	bSout=true;    	
    }
    public void SErrDone()
    {
    	bSerr=true;    	
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
	public boolean expect_async(String pattern, int timeout) throws Exception {
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public Future<Boolean> expect_asyn(String pattern)
			throws Exception {
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public int expect_exact(List<String> patterns, int timeout) throws Exception {
		before = after = null;
		lastExpect.clear();	
		String line;
		try
		{
			 while(true)
			 {
				 if(timeout>0) line = readLine(timeout);
				 else line = readLine();				 
				 lastExpect.add(line);
				 
				 for(int i=0; i<patterns.size(); i++)
				 {
					 if(line.equals(patterns.get(i))) 
					 {
						 after = line;
						 return i;
					 }
				 }				 
			 }
		}
		catch(Exception e){throw e;}
		finally
		{
			before = StringUtils.join(lastExpect, NewLine);	
		}
	}
}
