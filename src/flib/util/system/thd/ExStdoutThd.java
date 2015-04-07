package flib.util.system.thd;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import flib.util.system.ISpawnProcess;

public class ExStdoutThd extends Thread{
	public boolean 				isHit=false;
	public boolean 				isStop=false;
	private ISpawnProcess		process;
	private List<String> 		lastExpect;
	private Pattern 			ptn;
	
	public ExStdoutThd(ISpawnProcess p, List<String> le, Pattern ptn)
	{
		this.process = p;
		this.lastExpect=le;
		this.ptn = ptn;
	}
	
	@Override
	public void run()
	{
		try
		{
			while(!isStop)
			{
				try
				{
					String line = process.readLine(500);
					if(line==null) break;
					lastExpect.add(line);
					//System.out.printf("\t[Test] Check Stdout: '%s'...\n", line);
					if(ptn.matcher(line).find()) 
					{
						isHit=true;
						break;
					}
				}
				catch(TimeoutException e){}
				//System.out.printf("\t[Stdout] testing...\n");
			}					
		}
		catch(Exception e){}
	}
}
