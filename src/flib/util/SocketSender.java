package flib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketSender {
	static class RcvThd implements Runnable{
		BufferedReader br = null;
		public boolean isDone=false;
		
		public RcvThd(InputStream is)
		{
			br = new BufferedReader(new InputStreamReader(is));
		}

		public void run() {
			String msg=null;
			try
			{
				while((msg=br.readLine())!=null)
				{
					System.out.printf("Rcv: %s\n", msg);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			isDone=true;
		}
		
	}
	

	/**
	 * BD: Test Socket 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<2)
		{
			System.out.printf("\t[Info] Argu1=Listening Address; Argu2=Listening Port\n");
			return;
		}
		try {  			
            String hostname = args[0];  
            int port = Integer.valueOf(args[1]);
            InetAddress address = InetAddress.getByName(hostname);  
            Socket skt = new Socket(address, port);   
            // 連線表示有開啟 Port   
            System.out.printf("%nPort: %d Opened..", port);   
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            PrintStream sktStream = new PrintStream(skt.getOutputStream());
            RcvThd rcvThd = new RcvThd(skt.getInputStream());
            new Thread(rcvThd).start();
            String msg=null;
            while((msg=br.readLine())!=null)
            {
            	System.out.printf("Snd: %s\n", msg);
            	sktStream.printf("%s\n", msg);
            	if(msg.equalsIgnoreCase("bye")||msg.equalsIgnoreCase("quit")) break;
            }            
            while(!rcvThd.isDone) Thread.sleep(500);
            skt.close();
        }   
        catch(UnknownHostException e) {   
            e.printStackTrace();  
        }   
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(Exception e){e.printStackTrace();}
	}

}
