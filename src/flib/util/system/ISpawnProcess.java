package flib.util.system;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Future;

public interface ISpawnProcess {
	final static String NewLine = System.getProperty("line.separator");
	final static char CtrlBreak = (char)3;
	
	public void setLogfile(OutputStream os);
	public OutputStream getLogfile();
	public void start() throws Exception;
	public void sendcontrol(char c) throws Exception;
	public void send(String msg) throws Exception;
	
	/**
	 * Send input line to System.in.
	 * 
	 * @param line: Line to be sent to System.in
	 * @throws Exception
	 */
	public void sendLine(String line) throws Exception;
	public List<String> readLines(int timeout);
	public List<Byte> read(int size, int timeout) throws Exception;
	public String readLine() throws Exception;
	public String readLine(int timeout) throws Exception;
		
	public Future<Boolean> expect_asyn(String pattern) throws Exception;
	public boolean expect_async(String pattern, int timeout) throws Exception;
	public boolean expect(String pattern, int timeout) throws Exception;
	public boolean expect(String pattern) throws Exception;
	public boolean expect_exact(String pattern, int timeout) throws Exception;
	public boolean expect_exact(String pattern) throws Exception;
	
	public boolean isDone();
	public void close();
	public int waitUtilEnd() throws Exception;
	public void interact() throws Exception;
	public void SOutDone();
	public void SErrDone();
	public int getLastExitValue();
	public String getBefore();
	public String getAfter();
}
