package flib.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

public class RandomExt {
	public static Object nextObj(Random rdm, HashMap<Object,Integer> objDist)
	{
		HashMap<Integer,Object> distMap = new HashMap<Integer,Object>();
		Iterator<Entry<Object,Integer>> iter = objDist.entrySet().iterator();
		int cnt = 0;
		while(iter.hasNext())
		{
			Entry<Object,Integer> ety = iter.next();
			for(int i=cnt; i<(ety.getValue()+cnt); i++) distMap.put(i, ety.getKey());
			cnt+=ety.getValue();
		}
		Object nextStat = distMap.get(rdm.nextInt(cnt));
		return nextStat;
	}
	
	public static Object nextObj(Random rdm, int range, double sum, HashMap<Object,Double> objDist)
	{
		HashMap<Integer,Object> distMap = new HashMap<Integer,Object>();
		Iterator<Entry<Object,Double>> iter = objDist.entrySet().iterator();
		int cnt = 0;
		while(iter.hasNext())
		{
			Entry<Object,Double> ety = iter.next();
			double rv = (ety.getValue()/sum)*range;
			if(rv<0) rv=1;
			for(int i=cnt; i<(rv+cnt); i++) distMap.put(i, ety.getKey());
			cnt+=rv;
		}
		
		Object nextStat = distMap.get(rdm.nextInt(cnt));
		return nextStat;
	}
	
	
	public static void main(String args[])
	{
		Random rdm = new Random();
		HashMap<Object,Double> probMap = new HashMap<Object,Double>();
		probMap.put(Integer.valueOf(1), 100.0);probMap.put(Integer.valueOf(2), 200.0); probMap.put(Integer.valueOf(3), 300.0); 
		probMap.put(Integer.valueOf(4), 400.0);
		double sum = 1000;
		int range = 100;
		HashMap<Object,Integer> countMap = new HashMap<Object,Integer>();
		for(int i=0; i<1000; i++)
		{
			Object o = RandomExt.nextObj(rdm, range, sum, probMap);
			System.out.printf("\t[Test] Round-%d: %d...\n", i, o);
			Integer v= countMap.get(o);
			if(v==null) countMap.put(o, 0);
			else countMap.put(o, v+1);
		}
		
		Iterator<Entry<Object,Integer>> iter = countMap.entrySet().iterator();
		while(iter.hasNext())
		{
			Entry<Object,Integer> e = iter.next();
			System.out.printf("\t[Test] %s -> %d\n", e.getKey(), e.getValue());
		}
	}
}
