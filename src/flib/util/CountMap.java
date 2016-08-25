package flib.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import flib.util.CountMap.Pair;

public class CountMap implements Iterable<Pair>, Serializable{
	public static class Pair implements Serializable{
		public Integer cnt=1;
		public Object key;
		public Pair(Object k){key=k;}
		public Pair(Object k, int c){key=k; cnt=c;}
		public void count(){cnt++;}
		public void count(int c){cnt+=c;}
		@Override
		public String toString(){return String.format("%s (%d)", key, cnt);}
	}
	
	
	public Set<Object> keys()
	{
		Set<Object> list = new TreeSet<Object>();
		for(Object o:cntMap.keySet()) list.add(o);
		return list;
	}
	
	public class Iter implements Iterator<Pair>, Serializable
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
	
	public int size()
	{
		int sum=0;
		for(Pair p:cntMap.values()) sum+=p.cnt;
		return sum;
	}
	
	public void clear()
	{
		cntMap.clear();
	}
	
	/**
	 * BD: Return category(s) with most count
	 * 
	 * @return List of category
	 */
	public List<Object> major()
	{
		List<Object> majors = new ArrayList<Object>();
		Object cate=null; int cnt=-1;
		Iterator<Pair> iter = this.iterator();
		while(iter.hasNext())
		{
			Pair p = iter.next();
			if(p.cnt>cnt) 
			{
				majors.clear();
				cnt=p.cnt;
				majors.add(p.key);
			}
			else if(p.cnt==cnt)
			{
				majors.add(p.key);
			}			
		}
		return majors;
	}
	
	/**
	 * BD: Reset Counting
	 */
	public void reset(){cntMap.clear();}
	
	/**
	 * BD: Collect Keys with count exceed lower bound (inclusive).
	 * @param lowerBound
	 * @return
	 */
	public List<Object> collectUpper(int lowerBound)
	{
		List<Object> list = new ArrayList<Object>();
		for(Pair p:cntMap.values()) if(p.cnt>=lowerBound) list.add(p.key);
		return list;
	}
	
	/**
	 * BD: Collect keys with count under upper bound (inclusive).
	 * @param upperBound
	 * @return
	 */
	public List<Object> collectLower(int upperBound)
	{
		List<Object> list = new ArrayList<Object>();
		for(Pair p:cntMap.values()) if(p.cnt<=upperBound) list.add(p.key);
		return list;
	}
	
	/**
	 * BD: Collect keys with count exceed lower bound and under upper bound (both inclusive)
	 * @param lowerBound
	 * @param upperBound
	 * @return
	 */
	public List<Object> collect(int lowerBound, int upperBound)
	{
		List<Object> list = new ArrayList<Object>();
		for(Pair p:cntMap.values()) if(p.cnt<=upperBound && p.cnt>=lowerBound) list.add(p.key);
		return list;
	}
	
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
		/*if(cntMap.size()==0) return "";
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
		}*/
		return toString("");
	}
	
	public String toString(String indent)
	{
		return toString(indent, "", -1);
	}
	
	public String toString(int top, String restLabel){return toString("", restLabel, top);}
	
	public String toString(String indent, String restLabel, int top)
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
			int count=0;
			for(Pair p:cntMap.values()) {
				pq.add(p); count+=p.cnt;
			}
			strBuf.append(String.format("Total %d Category (%d):\n", cntMap.size(), count));
			if(top>0 && pq.size()>top)
			{
				List<Pair> topList = new ArrayList<Pair>();
				for(int i=0; i<top-1; i++) topList.add(pq.poll());
				Pair rest = new Pair(restLabel, 0);
				while(!pq.isEmpty()) rest.cnt+=pq.poll().cnt;
				for(Pair p:topList) pq.add(p);
				pq.add(rest);
			}
			
			//for(Pair p:cntMap.values()) pq.add(p);
			while(!pq.isEmpty()) strBuf.append(String.format("%s%s\r\n", indent, pq.poll()));
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
		for(Object k:cntMap.keys()) System.out.printf("%s\n", k);
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
