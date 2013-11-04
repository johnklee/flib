package flib.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import flib.util.CountMap.Pair;

public class CountMap implements Iterable<Pair>{
	public static class Pair{
		public Integer cnt=1;
		public Object key;
		public Pair(Object k){key=k;}
		public Pair(Object k, int c){key=k; cnt=c;}
		public void count(){cnt++;}
		public void count(int c){cnt+=c;}
		@Override
		public String toString(){return String.format("%s(%d)", key, cnt);}
	}
	
	public class Iter implements Iterator<Pair>
	{
		PriorityQueue<Pair> pq;
		
		public Iter(PriorityQueue<Pair> pq){this.pq = pq;}
		
		public boolean hasNext() {
			return !pq.isEmpty();
		}

		public Pair next() {
			return pq.poll();
		}

		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}
	private EOrder order=EOrder.Dsc;
	private HashMap<Object,Pair> cntMap = new HashMap<Object,Pair>();
	public enum EOrder{
		Dsc,Asc;
	}
	
	Comparator<Pair> dscCmp = new Comparator<Pair>() {
		public int compare(Pair o1, Pair o2) {
			return o2.cnt.compareTo(o1.cnt);
		}

	};
	Comparator<Pair> ascCmp = new Comparator<Pair>() {
		public int compare(Pair o1, Pair o2) {
			return o1.cnt.compareTo(o2.cnt);
		}

	};
	
	public CountMap(){}
	
	/**
	 * BD: Reset Counting
	 */
	public void reset(){cntMap.clear();}
	
	/**
	 * BD: Count Input Object <o>
	 * @param o
	 */
	public void count(Object o)
	{
		Pair p = cntMap.get(o);
		if(p==null) cntMap.put(o, new Pair(o));
		else p.count();
	}
	
	/**
	 * BD: Count Input Object
	 * @param o: Object to count
	 * @param cnt: Count <cnt>
	 */
	public void count(Object o, int cnt)
	{
		Pair p = cntMap.get(o);
		if(p==null) cntMap.put(o, new Pair(o, cnt));
		else p.count(cnt);
	}
	
	/**
	 * BD: Get Count Of Input Object <o>
	 * @param o
	 * @return
	 */
	public int getCount(Object o)
	{
		Pair p = cntMap.get(o);
		if(p==null) return 0;
		else return p.cnt;
	}
	
	@Override
	public String toString()
	{
		if(cntMap.size()==0) return "";
		else
		{
			StringBuffer strBuf = new StringBuffer();
			PriorityQueue<Pair> pq;
			switch(order)
			{
			case Dsc:
				pq = new PriorityQueue<Pair>(10,dscCmp);
				break;
			default:
				pq = new PriorityQueue<Pair>(10,ascCmp);				
			}
			for(Pair p:cntMap.values()) pq.add(p);
			while(!pq.isEmpty()) strBuf.append(String.format("%s\r\n", pq.poll()));
			return strBuf.toString();
		}
	}
	
	/**
	 * BD: Change display order to desc
	 */
	public void desc(){order=EOrder.Dsc;}
	
	/**
	 * BD: Change display order to asc
	 */
	public void asc(){order=EOrder.Asc;}
	
	public static void main(String args[])
	{
		CountMap cntMap = new CountMap();
		cntMap.count("a", 4);
		cntMap.count("a");
		cntMap.count("a");
		cntMap.count("b");
		cntMap.count("b");
		cntMap.count("c");
		System.out.printf("\t[Info] Desc:\n%s\n", cntMap);
		cntMap.asc();
		System.out.printf("\t[Info] Asc:\n%s\n", cntMap);
	}

	public Iterator<Pair> iterator() {
		PriorityQueue<Pair> pq;
		switch(order)
		{
		case Dsc:
			pq = new PriorityQueue<Pair>(10,dscCmp);
			break;
		default:
			pq = new PriorityQueue<Pair>(10,ascCmp);				
		}
		for(Pair p:cntMap.values()) pq.add(p);
		return new Iter(pq);
	}
}
