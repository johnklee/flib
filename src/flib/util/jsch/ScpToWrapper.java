package flib.util.jsch;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

public class ScpToWrapper {
	public static int SSHPort = 22;
	String host;
	String user;
	String pawd;

	boolean ptimestamp = false;
	int timeout=0;
	JSch jsch = new JSch();
	Session session = null;
	Channel channel = null;

	public ScpToWrapper(String h, String u, String p) {
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

	public boolean scpTo(File local, String remotePath) throws Exception {
		if (session != null) {
			FileInputStream fis = null;
			String command = "scp " + (ptimestamp ? "-p" : "") + " -t "
					+ remotePath;
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();
			int rt = -1;
			if ((rt = _checkAck(in)) != 0) {
				System.err.printf("\t[Error] Unknown Error1 (%d)\n", rt);
				return false;
			}

			if (ptimestamp) {
				command = "T " + (local.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (local.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if ((rt = _checkAck(in)) != 0) {
					System.err.printf("\t[Error] Unknown Error2 (%d)\n", rt);
					return false;
				}
			}

			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = local.length();
			command = "C0644 " + filesize + " ";
			if (local.getAbsolutePath().lastIndexOf('/') > 0) {
				command += local.getAbsolutePath().substring(
						local.getAbsolutePath().lastIndexOf('/') + 1);
			} else {
				command += local.getAbsolutePath();
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if ((rt = _checkAck(in)) != 0) {
				System.err.printf("\t[Error] Unknown Error3 (%d)\n", rt);
				return false;
			}

			// send a content of lfile
			fis = new FileInputStream(local);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if ((rt = _checkAck(in)) != 0) {
				System.err.printf("\t[Error] Unknown Error4 (%d)\n", rt);
				return false;
			}
			out.close();

			channel.disconnect();
			return true;
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

	/**
	 * http://stackoverflow.com/questions/22226440/mtime-sec-is-not-present
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		ScpToWrapper scpw = new ScpToWrapper("mostique", "john", "john7810");
		System.out.printf("\t[Info] Connecting...%s\n", scpw.connect());
		if (scpw.scpTo(new File("JSchLab.jar"), "~/ATFReport/JSchLab.jar")) {
			System.out.printf("\t[Info] Done!\n");
		} else {
			System.err.printf("\t[Error] Fail!\n");
		}
		scpw.close();
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		public MyUserInfo(String pwd) {
			this.passwd = pwd;
		}

		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			Object[] options = { "yes", "no" };
			int foo = JOptionPane.showOptionDialog(null, str, "Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			return foo == 0;
		}

		String passwd;
		JTextField passwordField = (JTextField) new JPasswordField(20);

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0);
		private Container panel;

		public String[] promptKeyboardInteractive(String destination,
				String name, String instruction, String[] prompt, boolean[] echo) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(null, panel, destination + ": "
					+ name, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}
	}
}
