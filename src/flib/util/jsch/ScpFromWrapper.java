package flib.util.jsch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class ScpFromWrapper {
	public static int SSHPort = 22;
	String host;
	String user;
	String pawd;

	boolean ptimestamp = false;
	int timeout=0;
	JSch jsch = new JSch();
	Session session = null;
	Channel channel = null;
	
	public ScpFromWrapper(String h, String u, String p) {
		this.host = h;
		this.user = u;
		this.pawd = p;
	}
	
	protected int _checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}
	
	public boolean scpFrom(String remotePath, File local) throws Exception {
		if (session != null) {
			FileOutputStream fos = null;
			try {
				// exec 'scp -f rfile' remotely
				String command = "scp -f " + remotePath;
				Channel channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);

				// get I/O streams for remote scp
				OutputStream out = channel.getOutputStream();
				InputStream in = channel.getInputStream();

				channel.connect();

				byte[] buf = new byte[1024];

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				while (true) {
					int c = _checkAck(in);
					if (c != 'C') {
						break;
					}

					// read '0644 '
					in.read(buf, 0, 5);

					long filesize = 0L;
					while (true) {
						if (in.read(buf, 0, 1) < 0) {
							// error
							break;
						}
						if (buf[0] == ' ')
							break;
						filesize = filesize * 10L + (long) (buf[0] - '0');
					}
					//System.out.printf("\t[Test] filesize=%d\n", filesize);

					String file = null;
					for (int i = 0;; i++) {
						in.read(buf, i, 1);
						if (buf[i] == (byte) 0x0a) {
							file = new String(buf, 0, i);
							break;
						}
					}

					// System.out.println("filesize="+filesize+", file="+file);

					// send '\0'
					buf[0] = 0;
					out.write(buf, 0, 1);
					out.flush();

					// read a content of lfile
					fos = new FileOutputStream(local);
					int foo;
					while (true) {
						if (buf.length < filesize)
							foo = buf.length;
						else
							foo = (int) filesize;
						foo = in.read(buf, 0, foo);
						if (foo < 0) {
							// error
							break;
						}
						fos.write(buf, 0, foo);
						filesize -= foo;
						if (filesize == 0L)
							break;
					}
					fos.close();
					fos = null;

					if (_checkAck(in) != 0) {
						System.exit(0);
					}

					// send '\0'
					buf[0] = 0;
					out.write(buf, 0, 1);
					out.flush();
				}

				channel.disconnect();
				return true;
			} catch (Exception e) {
				System.out.println(e);
				try {
					if (fos != null)
						fos.close();
				} catch (Exception ee) {
				}
			}
		} else {
			System.err.printf("\t[Error] Please execution connect() first!\n");
		}
		return false;
	}
	
	public boolean connect() {
		try {
			session = jsch.getSession(user, host, SSHPort);
			UserInfo ui = new CommUserInfo(pawd);
			session.setUserInfo(ui);
			session.setTimeout(timeout);
			session.connect();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void close() {
		if (channel != null)
			channel.disconnect();
		if (session != null)
			session.disconnect();
		channel = null;
		session = null;
	}
	
	public static void main(String args[]) throws Exception
	{
		ScpFromWrapper scpw = new ScpFromWrapper("mostique", "john", "john7810");
		
		System.out.printf("\t[Info] Connecting...");
		if(scpw.connect())
		{
			System.out.println("Done!\n");
			scpw.scpFrom("~/ATFReport/JSchLab.jar", new File("test.jar"));
		}
		else
		{
			System.out.printf("Fail!\n");
		}
		scpw.close();
	}
}
