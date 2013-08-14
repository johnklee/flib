package flib.util;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * BD : Simple Kit for translation between byte array and hex string.
 * @author John-Lee
 */
public class HexByteKit {
    private static byte charToByte(char c){
        return (byte) "0123456789ABCDEF".indexOf(c);
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
     * @param endian: 0 is little-endian/otherwise big-endian
     * @return
     */
    public static String Byte2Hex(byte[] b, int offset, int len, String sep, int endian)
    {
    	StringBuffer hexStr= new StringBuffer("");
        String stmp = "";        
        if(endian==0)  // little-endian
        {
        	int bdy = offset+len<b.length?len:b.length;
        	for(int i=offset;i<bdy;i++) {
                stmp = (java.lang.Integer.toHexString(b[i] & 0xFF));
                if(stmp.length() == 1) {
                    hexStr.append("0"+stmp);
                } else {
                    hexStr.append(stmp);
                }    
                hexStr.append(sep);
            }
        }
        else // big-endian
        {
        	int bdy = b.length-offset-1;
        	for(int i=bdy; i>=0; i--)
        	{
        		stmp = (java.lang.Integer.toHexString(b[i] & 0xFF));
                if(stmp.length() == 1) {
                    hexStr.append("0"+stmp);
                } else {
                    hexStr.append(stmp);
                }    
                hexStr.append(sep);
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
    
    public static String Byte2Hex(byte[] b, String sep){return Byte2Hex(b, sep, 0);}
    
    /**
     * BD : Used to transfer byte array into hex string.
     * @param b: Byte array
     * @param endian: 0 is little-endian; otherwise big-endian
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
    	return Byte2Hex(b, 0);
    }

    public static void main(String args[]) {
        byte[] b = {(byte)0xaa,(byte)0x1f,(byte)0x2b,(byte)0x14};
        String hexStr = Byte2Hex(b); // Using little-endian
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
            System.out.println("Translation success");
        } else {
            System.err.println("Translation failed");
        }
    }
}
