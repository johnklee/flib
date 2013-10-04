package flib.util;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * BD : Simple Kit for translation between byte array and hex string.
 * @author John-Lee
 */
public class HexByteKit {
	public static int BigEndian=0;
	public static int LittleEndian=1;
    private static byte charToByte(char c){
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * BD: Translate byte array into binary string.
     * @param b: Byte array
     * @param sep: Separator
     * @return
     */
    public static String Byte2Bin(byte[] b, String sep)
    {
    	return Byte2Bin(b, 0, b.length, sep, BigEndian);
    }
    
    public static String Byte2Bin(byte[] b)
    {
    	return Byte2Bin(b, 0, b.length, "", BigEndian);
    }
    
    /**
     * BD: Translate byte array into binary string.
     * REF:
     *   - How to convert a byte to its binary string representation
     *     http://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
     * @param b: Byte array
     * @param offset: Offset
     * @param len: Length 
     * @param sep: Separator 
     * @return
     */
    public static String Byte2Bin(byte[] b, int offset, int len, String sep, int endian)
    {
    	if(offset+len>b.length) return "";
    	StringBuffer binStr= new StringBuffer("");
    	if(endian==BigEndian)
    	{
    		int bdy = offset+len<b.length?len+offset:b.length; // boundary
    		binStr.append(String.format("%8s", Integer.toBinaryString(b[offset]&0xFF)).replaceAll(" ", "0"));
    		for(int i=offset+1;i<bdy;i++)
    		{
    			binStr.append(String.format("%s%8s", sep, Integer.toBinaryString(b[i]&0xFF)).replaceAll(" ", "0"));
    		}
    	}
    	else
    	{
    		int st = b.length-offset-1;
        	int end = st-len>=-1?st-len:-1;
        	binStr.append(String.format("%8s", Integer.toBinaryString(b[st]&0xFF)).replaceAll(" ", "0"));
        	for(int i=st-1; i>end; i--)
        	{
        		binStr.append(String.format("%s%8s", sep, Integer.toBinaryString(b[i]&0xFF)).replaceAll(" ", "0"));
        	}
    	}
    	return binStr.toString();
    }
    
    /**
     * <b>BD</b> : Used to transfer hex string into byte array. two hex string combines one byte. So that means the length of hex string
     *      should be even. Or the null will be returned.
     * @param hexStr
     * @return
     */
    public static byte[] Hex2Byte(String hexStr) {
        Logger logKit = JDebug.getLogger("HexByteKit");
        if(hexStr == null || hexStr.isEmpty() || (hexStr.length()%2>1)) {
            logKit.warning("Wrong format of Hex String!");
            return null;
        }
        String hexStrUp = hexStr.toUpperCase();
        int length = hexStrUp.length()/2;
        char[] hexChars = hexStrUp.toCharArray();
        byte[] resultByte = new byte[length];
        for(int i=0;i<length;i++) {
            int pos = i*2;
            resultByte[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos+1]));            
        }
        return resultByte;
    }
    
    /**
     * BD: Translate byte array into corresponding hex string.
     * @param b: Byte array.
     * @param offset: Offset from significant bit
     * @param len: Length of byte array <b>
     * @param sep: Separator char for each byte.
     * @return
     */
    public static String Byte2Hex(byte[] b, int offset, int len, String sep)
    {
    	return Byte2Hex(b, offset, len, sep, BigEndian);
    }
    
    /**
     * BD: Translate byte array into corresponding hex string.
     * @param b: Byte array.
     * @param offset: Offset from significant bit
     * @param len: Length of byte array <b>
     * @param sep: Separator char for each byte.
     * @param endian: 1 is little-endian/otherwise big-endian
     * @return
     */
    public static String Byte2Hex(byte[] b, int offset, int len, String sep, int endian)
    {
    	if(offset+len>b.length) return "";
    	StringBuffer hexStr= new StringBuffer("");
        String stmp = "";        
        if(endian==BigEndian)  // Big-endian
        {        	
        	int bdy = offset+len<b.length?len+offset:b.length;
        	stmp = (java.lang.Integer.toHexString(b[offset] & 0xFF));
            if(stmp.length() == 1) {
                hexStr.append("0"+stmp);
            } else {
                hexStr.append(stmp);
            }
        	for(int i=offset+1;i<bdy;i++) {
        		hexStr.append(sep);
        		stmp = (java.lang.Integer.toHexString(b[i] & 0xFF));
                if(stmp.length() == 1) {
                    hexStr.append("0"+stmp);
                } else {
                    hexStr.append(stmp);
                }                   
            }
        }
        else // little-endian
        {
        	int bdy = b.length-offset-1;
        	int end = bdy-len+1>=0?bdy-len+1:0;
        	stmp = (java.lang.Integer.toHexString(b[bdy] & 0xFF));
            if(stmp.length() == 1) {
                hexStr.append("0"+stmp);
            } else {
                hexStr.append(stmp);
            }
        	for(int i=bdy-1; i>=end; i--)
        	{
        		hexStr.append(sep);
        		stmp = (java.lang.Integer.toHexString(b[i] & 0xFF));
                if(stmp.length() == 1) {
                    hexStr.append("0"+stmp);
                } else {
                    hexStr.append(stmp);
                }                    
        	} 
        }
        return hexStr.toString().toUpperCase();
    }

    public static String Byte2Hex(byte[] b, int offset, int endian)
    {
    	return Byte2Hex(b, offset, b.length, "", endian);
    }
    
    public static String Byte2Hex(byte[] b, String sep, int endian)
    {
    	return Byte2Hex(b, 0, b.length, sep, endian);
    }
    
    public static String Byte2Hex(byte[] b, String sep){return Byte2Hex(b, sep, BigEndian);}
    
    /**
     * BD : Used to transfer byte array into hex string.
     * @param b: Byte array
     * @param endian: 1 is little-endian; otherwise big-endian
     * @return
     */
    public static String Byte2Hex(byte[] b, int endian){
    	return Byte2Hex(b, "", endian);
    }
    
    /**
     * BD: Using little-endian to translate byte array into corresponding hex string.
     * @param b: Byte array
     * @return
     */
    public static String Byte2Hex(byte[] b)
    {
    	return Byte2Hex(b, BigEndian);
    }

    public static void main(String args[]) {
        byte[] b = {(byte)0xaa,(byte)0x1f,(byte)0x2b,(byte)0x14};
        String hexStr = Byte2Hex(b); // Using Big-endian
        System.out.println("Hex string :"+hexStr);
        byte[] tmp = Hex2Byte(hexStr);
        boolean tres = true;
        if(b.length == tmp.length) {
            for(int i=0;i<tmp.length;i++) {
               if(b[i] != tmp[i]) {
                   tres = false;
                   break;
               }
            }
        }else {
            tres = false;
        }
        if(tres) {
            System.out.printf("Translation success: %s, %s\n", Byte2Hex(tmp, ":"), Byte2Hex(tmp, ":", LittleEndian));
        } else {
            System.err.printf("Translation failed: %s\n", Byte2Hex(tmp));
        }
    }
}
