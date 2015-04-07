package flib.util.jsch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import flib.util.Tuple;

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

	public boolean connect(OutputStream os) {
		try {
			session = jsch.getSession(user, host, SSHPort);
			UserInfo ui = new CommUserInfo(pawd);
			session.setUserInfo(ui);
			session.connect(timeout);
			channel = session.openChannel("shell");

			if (os != null)
				channel.setOutputStream(os);
			else {
				stdout = new BufferedStdout();
				channel.setOutputStream(stdout);
			}
			InputStream in = new PipedInputStream();
			pin = new PipedOutputStream((PipedInputStream) in);
			channel.setInputStream(in);
			channel.connect(timeout);
			return true;
		} catch (Exception e) {
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
	
	public Tuple sendlineWithOut(String cmd, Pattern bkPtn, int timeOut) throws Exception
	{
		if (channel != null) 
		{
			pin.write(String.format("%s\n", cmd).getBytes());
			StringBuffer outBuf = new StringBuffer();
			String line;
			Tuple rt=null;
			while(true)
			{
				rt = stdout.readLine2(timeOut);
				line = rt.getStr(1);
				outBuf.append(line);
				if(bkPtn.matcher(line).find()) return new Tuple(true, outBuf.toString());
				if(!rt.getBoolean(0)) break;
			}
			return new Tuple(false, outBuf.toString());
		}
		else
		{
			System.err.printf("\t[Error] Please execute connect() first!\n");
		}
		return null;
	}
	
	/**
	 * Send command [cmd] to shell channel and wait for [timeOut] to collect output.
	 * @param cmd: Command sent to shell channel.
	 * @param timeOut: Timeout to wait for output.
	 * @return Output string or Null if connection is not ready.
	 * @throws Exception
	 */
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
		ShellWrapper sw = new ShellWrapper("192.168.140.129", "john", "john7810");
		System.out.printf("\t[Info] Login...%s\n", sw.connect());
		
		// 1) Sending command 'dir' and wait 3 sec to collect output.
		System.out.printf("pwd:\n%s\n\n", sw.sendlineWithOut("dir", 3000));
		
		// 2) Sending command and collect output until pattern matching.
		// Tuple(Flag,Output)
		//   - Flag(Boolean): 	True means pattern match; Otherwise False.
		//   - Output(String): 	Console output
		Tuple rt = sw.sendlineWithOut("ps aux", Pattern.compile("john@route"), 1000);
		if(rt.getBoolean(0))
		{
			System.out.printf("\t[Info] Match pattern with output:\n%s\n", rt.getStr(1));			
		}
		else
		{
			System.out.printf("\t[Info] Output:\n%s\n", rt.getStr(1));	
		}
			
		System.out.printf("\t[Info] Bye!\n");
		sw.close();
	}
	
	public static class BufferedStdout extends OutputStream{
		List<Byte> 		content = new ArrayList<Byte>();
		long 			st=System.currentTimeMillis();
		int				_readLineOffset=0;

		public Tuple readLine2(int timeOut) throws IOException
		{
			String line=null;
			while(true)
			{
				Tuple rt = _readLine(_readLineOffset);
				if(rt.getInt(0)!=_readLineOffset)
				{
					_readLineOffset=rt.getInt(0);
					line=rt.getStr(1);
					break;
				}
				else 
				{
					if(this.sleepTime()<timeOut) try{Thread.sleep(500);}catch(Exception e){}
					else 
					{
						return new Tuple(false, rt.getStr(1));						
					}
				}
			}
			return new Tuple(true, line);
		}
		
		public String readLine(int timeOut) throws IOException
		{
			String line=null;
			while(true)
			{
				Tuple rt = _readLine(_readLineOffset);
				if(rt.getInt(0)!=_readLineOffset)
				{
					_readLineOffset=rt.getInt(0);
					line=rt.getStr(1);
					break;
				}
				else 
				{
					if(this.sleepTime()<timeOut) try{Thread.sleep(500);}catch(Exception e){}
					else break;
				}
			}
			return line;
		}
		
		public Tuple _readLine(int ofs) throws IOException
		{
			byte[] nb = System.lineSeparator().getBytes();
			LinkedList<Byte> _content = new LinkedList<Byte>();
			boolean hasLS=false; // Has line separator
			int nbi=0, nofs=ofs;
			for(int i=ofs; i<content.size(); i++)
			{
				Byte cb = content.get(i);
				_content.add(cb);
				if(cb.equals(nb[nbi]))
				{
					nbi++;
					if(nbi==nb.length)
					{
						hasLS=true;
						break;
					}
				}
				nofs=i;
			}
			
			if(hasLS)
			{
				for(int i=0; i<nb.length; i++) _content.removeLast();
				nofs+=1;
			}
			else nofs=ofs;
			
			return new Tuple(nofs, new String(ArrayUtils.toPrimitive(_content.toArray(new Byte[_content.size()]))));
		}
		
		@Override
		public void write(int b) throws IOException {
			content.add((byte)b);			
			st=System.currentTimeMillis();
		}
		
		public void clearContent(){content.clear();}
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
