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
}
