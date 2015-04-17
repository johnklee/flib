package unitest.flib.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import flib.util.CountMap;
import flib.util.CountMap.Pair;

public class TestCountMap {
	CountMap cntMap;
	
	@Before  
    public void setUp() {  
		cntMap = new CountMap();
		cntMap.count("a", 4);
		cntMap.count("a");
		cntMap.count("a");
		cntMap.count("b");
		cntMap.count("b");
		cntMap.count("c");
    }  
  
    @After  
    public void tearDown() {  
    	cntMap = null;  
    }
    
    @Test
    public void testIter()
    {
    	int i=1;
    	for(Pair p:cntMap)
    	{
    		switch(i)
    		{
    		case 1:
    			assertEquals(Integer.valueOf(6), p.cnt);
    			assertEquals("a", p.key);
    			break;
    		case 2:
    			assertEquals(Integer.valueOf(2), p.cnt);
    			assertEquals("b", p.key);
    			break;
    		case 3:
    			assertEquals(Integer.valueOf(1), p.cnt);
    			assertEquals("c", p.key);
    			break;
    		default:
    			fail("Too many items!");
    		}
    		i++;
    	}
    	cntMap.asc();
    	i=3;
    	for(Pair p:cntMap)
    	{
    		switch(i)
    		{
    		case 1:
    			assertEquals(Integer.valueOf(6), p.cnt);
    			assertEquals("a", p.key);
    			break;
    		case 2:
    			assertEquals(Integer.valueOf(2), p.cnt);
    			assertEquals("b", p.key);
    			break;
    		case 3:
    			assertEquals(Integer.valueOf(1), p.cnt);
    			assertEquals("c", p.key);
    			break;
    		default:
    			fail("Too many items!");
    		}
    		i--;
    	}
    }
    
    @Test
    public void testFAT()
    {    	
		assertEquals(6, cntMap.getCount("a"));
		assertEquals(2, cntMap.getCount("b"));
		assertEquals(1, cntMap.getCount("c"));
		String[] cs = cntMap.toString().split("\n");
		assertEquals("a (6)", cs[1].trim());
		assertEquals("b (2)", cs[2].trim());
		assertEquals("c (1)", cs[3].trim());
		cntMap.asc();
		cs = cntMap.toString().split("\n");
		assertEquals("a (6)", cs[3].trim());
		assertEquals("b (2)", cs[2].trim());
		assertEquals("c (1)", cs[1].trim());
    }
}
