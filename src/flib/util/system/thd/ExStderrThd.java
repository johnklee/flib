package flib.util.system.thd;

import java.util.regex.Pattern;

public class ExStderrThd extends Thread{
	public boolean 					isHit=false;
	public boolean 					isStop=false;
	private StringBuffer 			errBuf = null;		
	private StringBuffer			preErrBuf = new StringBuffer();
	private Pattern 				ptn;
	
	public ExStderrThd(StringBuffer eb, StringBuffer peb, Pattern ptn)
	{
		this.errBuf= eb; this.preErrBuf=peb; this.ptn = ptn;
	}
	
	@Override
	public void run()
	{
		while(!isStop)
		{
			if(ptn.matcher(errBuf.toString()).find())
			{
				isHit=true;
				preErrBuf.delete(0, preErrBuf.length());
				preErrBuf.append(errBuf.toString());
				errBuf.delete(0, errBuf.length());
				break;
			}
			try{Thread.sleep(500);}catch(Exception e){}
			//System.out.printf("\t[Stderr] testing...\n");
		}
	}
}
