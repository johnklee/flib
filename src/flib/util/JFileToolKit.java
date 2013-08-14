/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package flib.util;

import java.io.*;

/**
 *
 * @author John-Lee
 */
public class JFileToolKit {
    public static int BufferSize = 1024;

    public static String FileSizeInStr(long size)
    {
    	long b = size%1024;
    	long kb = size/1024;
    	long mb = 0;
    	long gb = 0;
    	if(kb>1024) {
    		mb=size/(1024*1024);
    		kb=kb%1024;
    		if(mb>1024) {
    			gb = size/(1024*1024*1024);
    			mb = (size/(1024*1024))%1024;
    		}
    	}
    	if(gb>0)
    	{
    		return String.format("%3d.%03d Gbytes", gb, mb);
    	}
    	else if(mb>0)
    	{
    		return String.format("%3d.%03d Mbytes", mb, kb);
    	}
    	else if(kb>0)
    	{
    		return String.format("%3d.%03d Kbytes", kb, b);
    	}
    	else
    	{
    		return String.format("%3d bytes", size);
    	}
    }
    
    public static synchronized boolean CopyTo(File srcFile, File destFile) {
        try {
            InputStream in = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);

            byte[] buf = new byte[BufferSize];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            //System.out.println("File copied.");
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static boolean CopyTo(String src, String dest) {
        return CopyTo(new File(src), new File(dest));
    }

    public static boolean MoveTo(String src, String dest){
       return MoveTo(new File(src), new File(dest));
    }

    public static synchronized boolean MoveTo(File src, File dest) {
        return src.renameTo(dest);
    }
    
    protected static boolean _delete(File file)
    {
    	if(file.exists())
    	{
    		if(file.isDirectory())
    		{
    			File fs[] = file.listFiles();
    			for(File f:fs) if(!_delete(f)) return false;
    		}
    		file.delete();    		
    	}
    	return true;
    }
    
    public static boolean delete(File f)
    {
    	return _delete(f);
    }
    
    public static void main(String args[])
    {
    	File fdr = new File("E:/tmp");
    	File fs[] = fdr.listFiles();
    	for(File f:fs)
    	{
    		if(f.isFile()) System.out.printf("\t[Test] File(%s) size=%s...\n", f.getAbsolutePath(), JFileToolKit.FileSizeInStr(f.length()));
    	}
    }
}
