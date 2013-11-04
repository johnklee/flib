package unitest.flib.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import flib.util.CountMap;

public class TestCountMap {
	CountMap cntMap;
	
	@Before  
    public void setUp() {  
		cntMap = new CountMap();
    }  
  
    @After  
    public void tearDown() {  
    	cntMap = null;  
    }
    
    @Test
    public void testFAT()
    {
    	cntMap.count("a", 4);
		cntMap.count("a");
		cntMap.count("a");
		cntMap.count("b");
		cntMap.count("b");
		cntMap.count("c");
		assertEquals(6, cntMap.getCount("a"));
		assertEquals(2, cntMap.getCount("b"));
		assertEquals(1, cntMap.getCount("c"));
		String[] cs = cntMap.toString().split("\n");
		assertEquals("a(6)", cs[0].trim());
		assertEquals("b(2)", cs[1].trim());
		assertEquals("c(1)", cs[2].trim());
		cntMap.asc();
		cs = cntMap.toString().split("\n");
		assertEquals("a(6)", cs[2].trim());
		assertEquals("b(2)", cs[1].trim());
		assertEquals("c(1)", cs[0].trim());
    }
}
