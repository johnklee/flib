package flib.util.jsch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.ArrayUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class ShellWrapper {
	public static int			SSHPort=22;
	String 						host;
	String 						user;
	String 						pawd;
	
	int 						timeout=30000;
	JSch 						jsch=new JSch();  
	Session 					session = null;
	Channel 					channel = null;
	PipedOutputStream 			pin=null;
	BufferedStdout 				stdout=null;

	public ShellWrapper(String h, String u, String p)
	{
		this.host=h; this.user=u; this.pawd=p;
	}

	public boolean connect(){return connect(null);}
	public boolean connect(OutputStream os)
	{
		try
		{
			session=jsch.getSession(user, host, SSHPort);
			UserInfo ui=new CommUserInfo(pawd);
		   session.setUserInfo(ui);		   
		   session.connect(timeout);		  
		   channel=session.openChannel("shell");
		  
		   if(os!=null) channel.setOutputStream(os);
		   else {
			   stdout = new BufferedStdout();			   
			   channel.setOutputStream(stdout);
		   }
		   InputStream in = new PipedInputStream();
		   pin = new PipedOutputStream((PipedInputStream) in);		   
		   channel.setInputStream(in);
		   channel.connect(timeout);
		   return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean sendline(String cmd) throws Exception
	{
		if(channel!=null)
		{
			pin.write(String.format("%s\n", cmd).getBytes());
			return true;
		}
		return false;
	}
	
	public String sendlineWithOut(String cmd, int timeOut) throws Exception {
		if (channel != null) {
			pin.write(String.format("%s\n", cmd).getBytes());
			
			String input = null;
			while(stdout.sleepTime()<timeOut) {Thread.sleep(100);}				
			List<Byte> content = stdout.getContent(true);
			if(content!=null)
			{
				input = new String(ArrayUtils.toPrimitive(content.toArray(new Byte[content.size()])));		
			}
			return input;
		}
		else
		{
			System.err.printf("\t[Error] Please execute connect() first!\n");
		}
		return null;
	}
	
	/**
	 * Close shell connection.
	 */
	public void close()
	{
		if(channel!=null) channel.disconnect();
	   if(session!=null) session.disconnect();
	   if(pin!=null) try{pin.close();}catch(Exception e){}
	   if(stdout!=null) try{stdout.close();}catch(Exception e){}
	   channel=null; session=null; pin=null;
	}
	
	public static void main(String args[]) throws Exception
	{
		ShellWrapper sw = new ShellWrapper("mostique", "john", "john7810");
		System.out.printf("\t[Info] Login...%s\n", sw.connect());
		System.out.printf("cd:\n%s\n", sw.sendlineWithOut("cd ATFReport", 3000));
		System.out.printf("pwd:\n%s\n", sw.sendlineWithOut("dir", 3000));		
		System.out.printf("\t[Info] Bye!\n");
		sw.close();
	}
	
	public static class BufferedStdout extends OutputStream{
		List<Byte> 	content = new ArrayList<Byte>();
		long 			st=System.currentTimeMillis();

		@Override
		public void write(int b) throws IOException {
			content.add((byte)b);			
			st=System.currentTimeMillis();
		}
		
		public List<Byte> getContent(boolean bClean)
		{
			List<Byte> _cnt = new ArrayList<Byte>();
			_cnt.addAll(content);
			if(bClean) content.clear();
			st=System.currentTimeMillis();
			return _cnt;
		}
		public void resetSleepTime(){st=System.currentTimeMillis();}
		public long sleepTime(){return System.currentTimeMillis()-st;}
	}
	
	public static class ReadTask implements Callable
	{
		PipedInputStream 			stdout;
		
		public ReadTask(PipedInputStream stdout){this.stdout=stdout;}
		
		@Override
		public Object call() throws Exception {
			List<Byte> bytes = new ArrayList<Byte>();
			try {  
		        // wait until we have data to complete a readLine()  
		        byte b;
		        while((b=(byte)stdout.read())!=-1) bytes.add(b);
		      } catch (Exception e) {  
		        System.out.println("ConsoleInputReadTask() cancelled");
		        e.printStackTrace();
		        return null;  
		      } 
			return bytes;
		}
		
	}
}
