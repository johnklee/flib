package flib.util.jsch;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.InputStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import flib.util.Tuple;

public class ExecWrapper {
	public static int			SSHPort=22;
	String 						host;
	String 						user;
	String 						pawd;
	
	int 							timeout=0;
	JSch 							jsch=new JSch();  
	Session 						session = null;
	Channel 						channel = null;
	
	public ExecWrapper(String h, String u, String p)
	{
		this.host=h; this.user=u; this.pawd=p;
	}

	public boolean connect()
	{
		try
		{
			session=jsch.getSession(user, host, SSHPort);
			UserInfo ui=new MyUserInfo(pawd);
		   session.setUserInfo(ui);
		   session.setTimeout(timeout);
		   session.connect();		  
		   return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public Tuple exe(String cmd) throws Exception {
		 channel=session.openChannel("exec");
		((ChannelExec) channel).setCommand(cmd);

		// X Forwarding
		// channel.setXForwarding(true);

		// channel.setInputStream(System.in);
		channel.setInputStream(null);

		// channel.setOutputStream(System.out);

		// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
		// ((ChannelExec)channel).setErrStream(fos);
		//((ChannelExec) channel).setErrStream(System.err);		
		// InputStream in = channel.getInputStream();
		

		StringBuffer stdinBuf = new StringBuffer();
		StringBuffer stderBuf = new StringBuffer();
		channel.connect();
		StdColtRunnable stdinClt = new StdColtRunnable(channel, channel.getInputStream(), stdinBuf);
		StdColtRunnable stderClt = new StdColtRunnable(channel, ((ChannelExec) channel).getErrStream(), stdinBuf);
		new Thread(stdinClt).start();
		new Thread(stderClt).start();
		try {
			while(true)
			{
				Thread.sleep(1000);
				if(stdinClt.isDone && stderClt.isDone) break;
			}
		} catch (Exception ee) {
		}
		channel.disconnect();
		return new Tuple(stdinClt.exitStatus, stdinBuf.toString(), stderBuf.toString());
	}
	
	public void close()
	{
		if(channel!=null) channel.disconnect();
	   if(session!=null) session.disconnect();
	   channel=null; session=null;
	}
	
	public static void main(String args[]) throws Exception
	{
		ExecWrapper ew = new ExecWrapper("mostique", "john", "john7810");
		ew.connect();
		Tuple t = ew.exe("dir");
		System.out.printf("\t[Info] ExitStatus=%d...\n", t.getInt(0));
		System.out.printf("\t[Info] Stdin:\n%s\n\n", t.getStr(1));
		System.out.printf("\t[Info] Stderr:\n%s\n\n", t.getStr(2));
		
		t = ew.exe("whoami");
		System.out.printf("\t[Info] ExitStatus=%d...\n", t.getInt(0));
		System.out.printf("\t[Info] Stdin:\n%s\n\n", t.getStr(1));
		System.out.printf("\t[Info] Stderr:\n%s\n\n", t.getStr(2));
		ew.close();
	}
	
	public static class StdColtRunnable implements Runnable{
		StringBuffer msgBuf;
		boolean isDone=false;
		InputStream in;
		int exitStatus = -1;
		Channel channel;
		
		public StdColtRunnable(Channel channel, InputStream in, StringBuffer msgBuf)
		{
			this.in=in; this.msgBuf=msgBuf;
			this.channel=channel;
		}

		@Override
		public void run() {
			isDone=false;
			try
			{
				byte[] tmp = new byte[1024];
				while (true) {
					while (in.available() > 0) {
						int i = in.read(tmp, 0, 1024);
						if (i < 0)
							break;
						msgBuf.append(new String(tmp, 0, i));
						// System.out.print(new String(tmp, 0, i));
					}
					if (channel.isClosed()) {
						if (in.available() > 0)
							continue;
						exitStatus = channel.getExitStatus();
						break;
					}
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			catch(Exception e){e.printStackTrace();}
			isDone=true;
		}
		
	}
	
	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
		
		public MyUserInfo(String pwd)
		{
			this.passwd=pwd;
		}
	    public String getPassword(){ return passwd; }
	    public boolean promptYesNo(String str){
	      return true;
	    }
	  
	    String passwd;
	    JTextField passwordField=(JTextField)new JPasswordField(20);

	    public String getPassphrase(){ return null; }
	    public boolean promptPassphrase(String message){ return true; }
	    public boolean promptPassword(String message){
	      return true;
	    }
	    public void showMessage(String message){
	      JOptionPane.showMessageDialog(null, message);
	    }
	    final GridBagConstraints gbc = 
	      new GridBagConstraints(0,0,1,1,1,1,
	                             GridBagConstraints.NORTHWEST,
	                             GridBagConstraints.NONE,
	                             new Insets(0,0,0,0),0,0);
	    private Container panel;
	    public String[] promptKeyboardInteractive(String destination,
	                                              String name,
	                                              String instruction,
	                                              String[] prompt,
	                                              boolean[] echo){
	      panel = new JPanel();
	      panel.setLayout(new GridBagLayout());

	      gbc.weightx = 1.0;
	      gbc.gridwidth = GridBagConstraints.REMAINDER;
	      gbc.gridx = 0;
	      panel.add(new JLabel(instruction), gbc);
	      gbc.gridy++;

	      gbc.gridwidth = GridBagConstraints.RELATIVE;

	      JTextField[] texts=new JTextField[prompt.length];
	      for(int i=0; i<prompt.length; i++){
	        gbc.fill = GridBagConstraints.NONE;
	        gbc.gridx = 0;
	        gbc.weightx = 1;
	        panel.add(new JLabel(prompt[i]),gbc);

	        gbc.gridx = 1;
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.weighty = 1;
	        if(echo[i]){
	          texts[i]=new JTextField(20);
	        }
	        else{
	          texts[i]=new JPasswordField(20);
	        }
	        panel.add(texts[i], gbc);
	        gbc.gridy++;
	      }

	      if(JOptionPane.showConfirmDialog(null, panel, 
	                                       destination+": "+name,
	                                       JOptionPane.OK_CANCEL_OPTION,
	                                       JOptionPane.QUESTION_MESSAGE)
	         ==JOptionPane.OK_OPTION){
	        String[] response=new String[prompt.length];
	        for(int i=0; i<prompt.length; i++){
	          response[i]=texts[i].getText();
	        }
		return response;
	      }
	      else{
	        return null;  // cancel
	      }
	    }
	  }
}
