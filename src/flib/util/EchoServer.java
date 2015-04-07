package flib.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		if(args.length<1)
		{
			System.out.printf("\t[Info] Argu1=Listening Port\n");
			return;
		}
		
		final int port = Integer.valueOf(args[0]);
		ServerSocket serverSkt;
		Socket skt;
		BufferedReader sktReader;
		String msg;
		PrintStream sktStream;

		serverSkt = new ServerSocket(port);
		while(true)
		{
			System.out.printf("Listening Port=%d...\n", port);
			skt = serverSkt.accept();
			System.out.printf("New Connection from %s...\n", skt.getInetAddress());
			sktReader = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			while((msg=sktReader.readLine())!=null)
			{				
				System.out.printf("Client: %s\n", msg);
				sktStream = new PrintStream(skt.getOutputStream());
				sktStream.printf("echo '%s'\n", msg);
				if(msg.equals("bye"))
				{
					System.out.println("Bye!");
					skt.close();
					break;
				}
			}
		}
	}
}
