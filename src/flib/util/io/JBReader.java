package flib.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JBReader {
	public static String 	ENCODING = "big5";
	public static String 	OUT_ENCODING = null;
	public static int		BUFFER_SIZE = 1024;	
	public static boolean	REMOVE_END_SPECIAL_CHAR = true; // Check '\r' and '\n' only.
	
	private InputStream 	is = null;
	private byte 			buffer[] = null;
	private byte			prevByte = -1;
	private int				prevLastFrom=-1, prevLastEnd=-1;
	private List<Byte> 		strBuf = new ArrayList<Byte>();	
	
	public JBReader(){}
	public JBReader(InputStream is) {
		this.is = is;
		buffer = new byte[BUFFER_SIZE];
	}
	
	public JBReader(InputStream is, String encoding)
	{
		this(is);
		this.ENCODING = encoding;
	}

	public void assignIS(InputStream is) throws IOException
	{
		assignIS(is, false);
	}
	
	public void assignIS(InputStream is, boolean reallocBuffer) throws IOException
	{
		close();
		this.is = is;
		if(reallocBuffer || buffer == null) buffer = new byte[BUFFER_SIZE];
	}
	
	public void close() throws IOException{
		if(is!=null) {
			is.close();	
			buffer = null;
		}		
		strBuf.clear();
		prevByte = -1;
		prevLastFrom = prevLastEnd = -1;
	}
	
	public byte[] readLineInBinary() throws IOException
	{
		if(is!=null)
		{
			strBuf.clear();
			// Restore last binary from previous loop
			if(prevLastFrom>0)
			{
				for(int i=prevLastFrom; i<prevLastEnd; i++) strBuf.add(buffer[i]);
				prevLastFrom=prevLastEnd=-1;
			}
			
			int rs = -1;
			boolean isNewLine = false;
			
			// Loop to read binary data
			while((rs = is.read(buffer)) > 0)
			{
				for(int i=0; i<rs; i++)
				{
					strBuf.add(buffer[i]);
					if(prevByte == (byte)0x0d && buffer[i] == (byte)0x0a)
					{
						isNewLine = true;
						prevByte = -1;
						if(i+1< rs) {
							prevLastFrom=i+1;
							prevLastEnd=rs;
						}
						break;
					}
					else
					{						
						prevByte = buffer[i];
					}
				}
				if(isNewLine) break;
			}
			if(rs<0 && strBuf.size()==0) 
			{
				//System.out.printf("\t[Test] EOF!");
				return null;
			}
			
			// Process returned string
			byte binarys[] = new byte[strBuf.size()];
			for(int i=0; i<binarys.length; i++) binarys[i] = strBuf.get(i);
			return binarys;
		}
		return null;
	}
	
	protected String processRtnStr() throws IOException
	{
		byte binarys[] = null;		
		if(REMOVE_END_SPECIAL_CHAR)
		{
			List<Integer> scIdxList = new LinkedList<Integer>();
			for(int i=0; i<strBuf.size(); i++) 
			{
				byte cb = strBuf.get(i); 
				if(cb == (byte)0x0d || cb == (byte)0x0a) scIdxList.add(i);				
			}
			binarys = new byte[strBuf.size()-scIdxList.size()];
			int j=0;
			for(int i=0; i<strBuf.size(); i++) 
				if(!scIdxList.contains(i))
				{
					binarys[j++] = strBuf.get(i);
				}
				else
				{
					scIdxList.remove(new Integer(i));
				}
		}
		else
		{
			binarys = new byte[strBuf.size()];
			for(int i=0; i<binarys.length; i++) binarys[i] = strBuf.get(i);
		}
		
		//System.out.printf("\t[Test] %s\n", HexByteKit.byte2hex(binarys, "|"));
		if(OUT_ENCODING==null)
		{
			return new String(binarys, ENCODING);
		}
		else
		{
			return new String(new String(binarys, ENCODING).getBytes(OUT_ENCODING));
		}
	}
		
	public String readLine() throws IOException
	{
		if(is!=null)
		{
			strBuf.clear();
			// Restore last binary from previous loop
			if(prevLastFrom>0)
			{				
				//System.out.printf("\t[Test] Restore last binary from previous loop...\n");
				boolean isNewLine = false;
				for(int i=prevLastFrom; i<prevLastEnd; i++) 
				{
					strBuf.add(buffer[i]);
					if(prevByte == (byte)0x0d && buffer[i] == (byte)0x0a)
					{						
						isNewLine = true;
						prevByte = -1;
						if(i+1< prevLastEnd) {
							prevLastFrom=i+1;							
						} else {
							prevLastFrom=prevLastEnd=-1;
						}
						break;
					}
					else
					{						
						prevByte = buffer[i];
					}
				}
				if(isNewLine)
				{
					return processRtnStr();
				}
				prevLastFrom=prevLastEnd=-1;
			}
			
			int rs = -1;
			boolean isNewLine = false;
			
			// Loop to read binary data
			while((rs = is.read(buffer)) > 0)
			{
				for(int i=0; i<rs; i++)
				{
					strBuf.add(buffer[i]);
					if(prevByte == (byte)0x0d && buffer[i] == (byte)0x0a)
					{
						isNewLine = true;
						prevByte = -1;
						if(i+1< rs) 
						{
							//System.out.printf("\t[Test] Still has binary in buffer!\n");
							prevLastFrom=i+1;
							prevLastEnd=rs;
							//System.out.printf("\t[Test] From=%d; End=%d\n", prevLastFrom, prevLastEnd);
						}
						break;
					}
					else
					{						
						prevByte = buffer[i];
					}
				}
				if(isNewLine) break;
			}
			
			if(rs<0 && strBuf.size()==0) 
			{
				//System.out.printf("\t[Test] EOF!");
				return null;
			}
			
			// Process returned string
			return processRtnStr();
		}
		return null;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		File testf = new File("test.txt");
		FileInputStream fis = new FileInputStream(testf);
		//BufferedReader br = new BufferedReader(new InputStreamReader(fis, ENCODING));
		JBReader br = new JBReader(fis);
		String line = null; int lc = 0;
		while((line=br.readLine())!=null)
		{
			lc++;
			System.out.printf("\t[Info] Read : %s\n", line.trim());
		}
		System.out.printf("\t[Info] Total %d line(s).\n", lc);
		br.close();
	}
}
