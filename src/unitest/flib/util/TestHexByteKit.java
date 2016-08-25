package unitest.flib.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import flib.util.HexByteKit;

public class TestHexByteKit {
	HexByteKit kit;
	
	@Before  
    public void setUp() {  
		kit = new HexByteKit();  
    }  
  
    @After  
    public void tearDown() {  
    	kit = null;  
    }  
    
    @Test
    public void testBitExtend()
    {
    	//assertEquals(Integer.valueOf(0), HexByteKit.BitExtend(0, 16, false));
    }
    
    @Test
    public void testInt2Byte()
    {
    	assertEquals("00000001", HexByteKit.Byte2Hex(HexByteKit.Int2Byte(1)));
    	assertEquals("00000002", HexByteKit.Byte2Hex(HexByteKit.Int2Byte(2)));
    	assertEquals("0000000A", HexByteKit.Byte2Hex(HexByteKit.Int2Byte(10)));
    	assertEquals("FFFFFFFF", HexByteKit.Byte2Hex(HexByteKit.Int2Byte(-1)));
    }
    
    @Test
    public void testBin2Hex()
    {    	
    	//System.out.printf("%s\n", HexByteKit.Bin2Hex(binStr));
    	assertEquals("BC", HexByteKit.Bin2Hex("10111100"));
    	assertEquals("5", HexByteKit.Bin2Hex("101"));
    	assertEquals("15", HexByteKit.Bin2Hex("10101"));    	
    }
    
    @Test
    public void testByte2Int()
    {
    	byte[] bs = new byte[4];
    	for(int i=0; i<4; i++) bs[i]=(byte)i;
    	try
    	{
    		String hexStr = HexByteKit.Byte2Hex(bs);
    		//System.out.printf("%s=%d(%d)\n", hexStr, HexByteKit.Byte2Int(bs), Integer.valueOf(hexStr, 16));
    		assertEquals(66051, HexByteKit.Byte2Int(bs));
    		for(int i=0; i<4; i++) bs[i]=(byte)0xFF;
    		hexStr = HexByteKit.Byte2Hex(bs);
    		//System.out.printf("%s=%d(%s)\n", hexStr, HexByteKit.Byte2Int(bs), Integer.toHexString(-1));
    		assertEquals(-1, HexByteKit.Byte2Int(bs));
    		bs = new byte[3];
    		for(int i=0; i<3; i++) bs[i]=(byte)(i+1);
    		assertEquals(66051, HexByteKit.Byte2Int(bs));
    	}
    	catch(Exception e){e.printStackTrace();}
    }
    
    @Test
    public void testByte2Bin()
    {
    	byte[] bs = new byte[4];
    	for(int i=0; i<4; i++) bs[i]=(byte)i;
    	assertEquals("00000000-00000001-00000010-00000011",HexByteKit.Byte2Bin(bs, "-"));
    	assertEquals("00000001",HexByteKit.Byte2Bin(bs, 1, 1, "", HexByteKit.BigEndian));
    	assertEquals("00000001-00000010",HexByteKit.Byte2Bin(bs, 1, 2, "-", HexByteKit.BigEndian));
    	assertEquals("00000001-00000010-00000011",HexByteKit.Byte2Bin(bs, 1, 3, "-", HexByteKit.BigEndian));
    	assertEquals("00000010",HexByteKit.Byte2Bin(bs, 1, 1, "", HexByteKit.LittleEndian));
    	assertEquals("00000010-00000001",HexByteKit.Byte2Bin(bs, 1, 2, "-", HexByteKit.LittleEndian));
    	assertEquals("00000010-00000001-00000000",HexByteKit.Byte2Bin(bs, 1, 3, "-", HexByteKit.LittleEndian));
    	//System.out.printf("%s\n", HexByteKit.Byte2Bin(bs, 1, 3, "-", HexByteKit.LittleEndian));
    }
    
    @Test
    public void testHex2Bin()
    {
    	String hexStrs[] = {"12F"};
    	String binStrs[] = {"000100101111"};
    	try
    	{
    		for(int i=0; i<hexStrs.length; i++)
        	{
    			
    			String hxs = hexStrs[i];
    			String bns = binStrs[i];
    			String obs = kit.Hex2Bin(hxs);
        		//System.out.printf("%s(%d)=%s\n", hxs, Integer.parseInt(hxs, 16), obs);
        		assertEquals(bns, obs);
        	}
    	}
    	catch(Exception e)
    	{
    		fail(String.format("Exception: %s", e));
    	}
    }
}
