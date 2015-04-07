package flib.util.system.thd;

import flib.util.system.ISpawnProcess;

public class WatchDog implements Runnable{
	private ISpawnProcess 	process;
	private Process			proc;
	private int 			checkPeriod=1000;
	private boolean			bStop=false;
	
	public WatchDog(ISpawnProcess p, Process proc){this.process=p;this.proc=proc;}

	@Override
	public void run()
	{
		try
		{
			if(proc!=null)
			{
				proc.waitFor();
			}
			else
			{
				while(!process.isDone() && !bStop) 
				{
					try 
					{
						Thread.sleep(checkPeriod);					
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
				}				
			}
		}
		catch(Exception e)
		{
			
		}
		//System.out.printf("\t[Info] Press 'Enter' to exit!\n");
		process.close();
	}

}
