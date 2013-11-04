package flib.util;

import java.nio.ByteBuffer;
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
    
    private static char _IntToHex(int i)
    {
    	return "0123456789ABCDEF".charAt(i);
    }

    public static Integer BitExtend(int val, int ob, boolean signed)
    {
    	if(ob<32)
    	{
    		StringBuffer bitStrBuf = new StringBuffer();
        	String valInBin = Integer.toBinaryString(val);
        	valInBin = valInBin.substring(valInBin.length()-ob, valInBin.length());
        	if(signed)
        	{
        		for(int i=0; i<32-ob; i++) bitStrBuf.append(valInBin.charAt(0));
        	}
        	else
        	{
        		for(int i=0; i<32-ob; i++) bitStrBuf.append("0");
        	}
        	bitStrBuf.append(valInBin);
        	System.out.printf("\t[Test] %s\n", bitStrBuf.toString());
        	return Bin2Int(bitStrBuf.toString());
    	}
    	return null;
    }
    
    /**
     * BD: Translate Integer to Byte Array.
     * REF:
     * 		- http://stackoverflow.com/questions/2183240/java-integer-to-byte-array
     * @param val: Integer value
     * @param endian: BigEndian=0; LittleEndian=1
     * @return
     */
    public static byte[] Int2Byte(int val, int endian)
    {    	
    	byte[] bs = ByteBuffer.allocate(4).putInt(val).array();;
    	if(endian!=BigEndian)
    	{
    		byte b;
    		for(int i=0; i<bs.length/2; i++)
    		{
    			b = bs[i];
    			bs[i] = bs[bs.length-1-i];
    			bs[bs.length-1-i] = b;
    		}
    		return bs;
    	}
    	return bs;
    }
    
    public static byte[] Int2Byte(int val){return Int2Byte(val, BigEndian);}
    
    public static Integer Bin2Int(String binStr)
    {
    	try
    	{
    		boolean isNeg=false;
    		if(binStr.length()<32)
    		{
    			StringBuffer binStrBuf = new StringBuffer();
    			for(int i=0; i<32-binStr.length();i++) binStrBuf.append("0");
    			binStr = binStrBuf.toString()+binStr;
    		}
    		else if(binStr.length()>32)
    		{
    			return null;
    		}
    		
    		if(binStr.startsWith("1")) {
    			isNeg = true;
    			StringBuffer binStrBuf = new StringBuffer();
    			for(char c:binStr.toCharArray())
    			{
    				if(c=='0') binStrBuf.append("1");
    				else binStrBuf.append("0");
    			}
    			binStr = binStrBuf.toString();
    		}
    		
    		int sum=0;
    		for(int i=1;i<32;i++) if(binStr.charAt(i)=='1') sum+=(int)Math.pow(2, 31-i);
    		return isNeg?-1*(sum+1):sum;
    	}
    	catch(Exception e){e.printStackTrace(); return null;}
    }
    
    public static String Bin2Hex(String binStr)
    {    	
    	StringBuffer hexStrBuf = new StringBuffer();
    	if(binStr.length()<=4) return String.valueOf(_IntToHex(Integer.valueOf(binStr,2)));
    	else
    	{
    		//binStr = new StringBuffer(binStr).reverse().toString();
    		int r = (int)Math.ceil(((double)binStr.length())/4);
    		for(int i=0; i<r; i++)
    		{
    			hexStrBuf.append(String.valueOf(_IntToHex(Integer.valueOf(binStr.substring(Math.max(0, binStr.length()-(i+1)*4), binStr.length()-i*4),2))));
    		}
    	}
    	return hexStrBuf.reverse().toString();
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
     * BD: Translate byte array into Integer.
     * @param b: Byte array
     * @param endian: BigEndian=0; LittleEndian=1
     * @return Integer 
     * @throws Exception
     */
    public static int Byte2Int(byte[] b, int endian) throws Exception
    {
    	if(b.length>4) throw new Exception(String.format("Illegal Byte Length=%d", b.length));
    	byte[] intByte=null;
    	if(b.length<4)
    	{
    		intByte = new byte[4];
    		for(int i=0;i<4;i++) intByte[i]=0;
    		if(endian==BigEndian)
    		{
    			for(int i=0; i<b.length; i++) intByte[3-i]=b[b.length-1-i];
    		}
    		else
    		{
    			for(int i=0; i<b.length; i++) intByte[i] = b[i];
    		}
    	}
    	else intByte = b;
    	
    	int sum = 0;		
		if(endian==BigEndian)
		{
			if(intByte[0]<0)
			{
				// Convert 2's complement byte					
				for(int i=0; i<4; i++) 
				{
					sum+=(byte)(~intByte[i]) << (3-i)*8;
				}			
				sum+=1;
				sum*=-1;
			}
			else
			{									
			    for (int i=0; i<4; i++) {			        
			        sum += (intByte[i] & 0x000000FF) << ((3 - i) * 8);
			    }		    
			}
		}
		else
		{
			if(intByte[3]<0)
			{
				for(int i=0; i<4; i++) 
				{
					sum+=(byte)(~intByte[i]) << i*8;
				}			
				sum+=1;
				sum*=-1;
			}
			else
			{
				for (int i=0; i<4; i++) {
			        sum += (intByte[i] & 0x000000FF) << (i*8);
			    }
			}
		}
		return sum;
    }
    
    /**
     * BD: Translate byte array into Integer.
     * @param b: Byte array
     * @return Integer 
     * @throws Exception
     */
    public static int Byte2Int(byte[] b) throws Exception{ return Byte2Int(b, BigEndian);}
    public static int Hex2Int(String hexStr) throws Exception{ return Byte2Int(Hex2Byte(hexStr));}
    
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
        if(hexStr.startsWith("0x")) hexStr = hexStr.substring(2, hexStr.length());
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
