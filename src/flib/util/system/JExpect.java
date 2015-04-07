package flib.util.system;

import flib.util.system.process.ExecSpawnProcess;
import flib.util.system.process.ShellSpawnProcess;

public class JExpect {		
	public static int SSH_CONNECT_TIMEOUT = 30000;
	public static int TIMTOUT=3000;
	
	/**
	 * BD: Spawn a Process by executing given command.
	 * 
	 * @param cmd: Command to execute in Spawn process.
	 * @return
	 * @throws Exception
	 */
	public static ISpawnProcess Spawn(String cmd) throws Exception
	{
		ExecSpawnProcess sp = new ExecSpawnProcess(cmd);
		sp.start();
		return sp;
	}
	
	/**
	 * SSH to connect remote machine.
	 * 
	 * @param host: Hostname of remote machine.
	 * @param user: User name to login.
	 * @param password: Password to Login
	 * @return
	 * 	Object which implemented ISpawnProcess interface. 
	 * @throws Exception
	 */
	public static ISpawnProcess SpawnShell(String host, String user, String password) throws Exception
	{
		ShellSpawnProcess sp = new ShellSpawnProcess(host, user, password);
		sp.start();
		return sp;
	}
	
	public static void main(String args[]) throws Exception
	{
			
	}
}
