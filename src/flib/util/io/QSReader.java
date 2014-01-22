package flib.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import flib.util.io.enums.EFileType;

public class QSReader implements Iterator<String>, Iterable<String>{
	public static String 		ENCODING = "utf-8";
	public EFileType 			FILE_TYPE = EFileType.TXT;
	public boolean 				skipEmptyLine=false;
	public boolean 				skipCommentLine=false;	
	public File 				inFile = null;
	public BufferedReader 		br = null;
	public String 				nextStr = null;
	private static final int 	BOM_SIZE = 4;

	public QSReader(String fn){this(new File(fn));}
	
	public QSReader(File file)
	{
		this.inFile = file;
	}
	
	public QSReader(File file, EFileType type)
	{
		this(file);
		this.FILE_TYPE = type;
	}
	
	public String line() throws IOException
	{
		if(br!=null) return br.readLine();
		return null;
	}
	
	public QSReader(String fn, EFileType type){this(new File(fn), type);}
	
	private class InnerIterator implements Iterator<String> {
        private String nextStr = null;
        private BufferedReader br = null;
        
        public InnerIterator(BufferedReader br) {this.br = br;}
        
        public boolean hasNext() {
        	try
    		{
    			nextStr = null;
    			if(br!=null) 
    			{
    				nextStr = br.readLine();
    				//System.out.printf("\t[Test] Test line=%s...\n", nextStr);
    				if(nextStr!=null && (skipEmptyLine||skipCommentLine))
    				{    					
    					String tNextStr = nextStr.trim();
    					while(tNextStr!=null && 
    						  ((tNextStr.isEmpty() && skipEmptyLine)||
    						   (tNextStr.startsWith("#") && skipCommentLine)))
    					{
    						nextStr = br.readLine();
    						if(nextStr!=null) tNextStr = nextStr.trim();
    						else tNextStr = null;
    					}
    					//System.out.printf("\t[Test] Pass skip empty line=%s...\n", nextStr);
    				}    				
    			}
    		}
    		catch(IOException e){e.printStackTrace();}
    		return nextStr!=null;
        }

        public String next() {
            return nextStr;
        }

        public void remove() {throw new java.lang.UnsupportedOperationException("Not support remove action!");}
    }
	
	protected InputStream _detectCoding() throws IOException
	{
		//System.out.printf("\t[Info] Detect encoding...");
		InputStream is = new FileInputStream(inFile);
		byte bom[] = new byte[BOM_SIZE];  
        int unread;  
        PushbackInputStream pushbackStream = new PushbackInputStream(is, BOM_SIZE);  
        int n = pushbackStream.read(bom, 0, bom.length); 
        // Read ahead four bytes and check for BOM marks.
        if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {  
        	ENCODING = "UTF-8";  
            unread = n - 3;  
        } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {  
        	ENCODING = "UTF-16BE";  
            unread = n - 2;  
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {  
        	ENCODING = "UTF-16LE";  
            unread = n - 2;  
        } else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {  
        	ENCODING = "UTF-32BE";  
            unread = n - 4;  
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {  
        	ENCODING = "UTF-32LE";  
            unread = n - 4;  
        } else {  
            //encoding = defaultEncoding;  
            unread = n;  
        } 
        //System.out.printf("%s (%d/%d)\n", ENCODING, unread, n);
        // Unread bytes if necessary and skip BOM marks.  
        if (unread > 0) {  
            pushbackStream.unread(bom, (n - unread), unread);  
        } else if (unread < -1) {  
            pushbackStream.unread(bom, 0, 0);  
        }                
        return pushbackStream;
	}
	
	public void open(String encoding) throws IOException
	{
		Reader decoder = null;
		if(encoding.equalsIgnoreCase("big5")||encoding.equalsIgnoreCase("big-5")){			
			switch(FILE_TYPE)
			{
			case TXT:
				br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ENCODING));
				break;
			case GZ:
				GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(inFile));
				decoder = new InputStreamReader(gzipInputStream, ENCODING);
				br = new BufferedReader(decoder);
				break;
			case BZ2:
				BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(new FileInputStream(inFile));
				decoder = new InputStreamReader(bzIn, ENCODING);
				br = new BufferedReader(decoder);
				break;
			default:
				br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ENCODING));
			}
		}
		else
		{
			switch(FILE_TYPE)
			{
			case TXT:
				br = new BufferedReader(new InputStreamReader(_detectCoding(), ENCODING));
				break;
			case GZ:
				GZIPInputStream gzipInputStream = new GZIPInputStream(_detectCoding());
				decoder = new InputStreamReader(gzipInputStream, ENCODING);
				br = new BufferedReader(decoder);
				break;
			case BZ2:
				BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(_detectCoding());
				decoder = new InputStreamReader(bzIn, ENCODING);
				br = new BufferedReader(decoder);
				break;
			default:
				br = new BufferedReader(new InputStreamReader(_detectCoding(), ENCODING));
			}			
		}
	}
	
	public static long DetectLineCount(File f)
	{
		BufferedReader br = null;
		try
		{
			long lc = 0;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(f), ENCODING));
			while(br.readLine()!=null) lc++;
			br.close();
			return lc;
		}
		catch(Exception e){e.printStackTrace();}		
		return -1;
	}
	
	public void open() throws IOException{
		this.open(ENCODING);
	}

	public void reopen(File file, EFileType type) throws Exception
	{
		this.FILE_TYPE = type;
		reopen(file);
	}
	
	public void reopen(File file) throws Exception
	{
		if(br!=null) 
		{
			br.close();			
		}
		inFile = file;
		//br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), ENCODING));
		open();
	}
	
	public boolean hasNext() {
		try
		{
			nextStr = null;
			if(br!=null) 
			{
				nextStr = br.readLine();				
				if(nextStr!=null && (skipEmptyLine||skipCommentLine))
				{    					
					nextStr = nextStr.trim();
					while(nextStr!=null && 
						  ((nextStr.isEmpty() && skipEmptyLine)||
						   (nextStr.startsWith("#") && skipCommentLine)))
					{
						nextStr = br.readLine();
						if(nextStr!=null) nextStr = nextStr.trim();
						
					}
					//System.out.printf("\t[Test] Pass skip empty line=%s...\n", nextStr);
				} 
			}
		}
		catch(IOException e){e.printStackTrace();}
		return nextStr!=null;
	}

	public String next() {
		return nextStr;
	}

	public void remove() {
		throw new java.lang.UnsupportedOperationException("Not support remove action!");		
	}
	
	public Iterator<String> iterator() {
		if(br!=null)
		{
			return new InnerIterator(br);
		}
		return null;
	}
	
	public void close()throws IOException{if(br!=null){br.close(); br=null;}}
	
	public static void main(String args[]) throws Exception
	{
		QSReader qsr = new QSReader(new File("country.txt"));
		qsr.skipCommentLine=true;
		qsr.skipEmptyLine=true;
		qsr.open();
		for(String line:qsr)
		{
			System.out.printf("\t[Test] Read line=%s\n", line);
			//if(line.equals("This is utf8.")) System.out.printf("...Pass!\n");
			//else System.out.printf("...Fail!\n");
		}
		qsr.close();
	}

	
}
