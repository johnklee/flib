/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package flib.util;

import flib.enums.EEncodingType;
import java.io.*;

/**
 *
 * @author John-Lee
 */
public class FileEncodeReferee {
    private File file;

	public FileEncodeReferee(File f){
		file = f;
	}

	public FileEncodeReferee(String path) {
		file = new File(path);
	}

	public static EEncodingType getCharset(File f){
		EEncodingType charset = EEncodingType.Big5;
		byte[] first3Bytes = new byte[3];
		BufferedInputStream bis = null;
		try {
			// boolean checked = false;
			bis = new BufferedInputStream(new FileInputStream(f));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
				return charset;
			}
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = EEncodingType.UTF_16LE;
				// checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = EEncodingType.UTF_16BE;
				// checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = EEncodingType.UTF8;
				// checked = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return charset;

	}

	public String getCharset() {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		BufferedInputStream bis = null;
		try {
			// boolean checked = false;
			bis = new BufferedInputStream(new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
				return charset;
			}
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				// checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				// checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				// checked = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return charset;
	}

	public static void main(String args[]) {
		File utf8f = new File("E:/Temp/TestData/utf8.txt");
		File big5f = new File("E:/Temp/TestData/big5.txt");

        System.out.println(utf8f.getAbsolutePath()+" charset="+FileEncodeReferee.getCharset(utf8f).toString());
        System.out.println(big5f.getAbsolutePath()+" charset="+FileEncodeReferee.getCharset(big5f).toString());
	}
}
