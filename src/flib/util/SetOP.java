package flib.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class SetOP<E> {
	public static Class SetClass = HashSet.class;
	
	/**
	 * BD: Another version of DIFF. Keep elements only in sa or sb
	 * @param sa
	 * @param sb
	 * @return (sa ∪ sb) - (sb ∩ sa)
	 */
	public Set<E> DIFF2(Set<E> sa, Set<E> sb)
	{
		Set<E> s1 = UNION(sa,sb);
		Set<E> s2 = INTERSECT(sa,sb);
		s1.removeAll(s2);
		return s1;
	}
	
	/**
	 * BD: Two sets can also be "subtracted". The relative complement of B in A (also called the set-theoretic difference of A and B), 
	 *     denoted by A \ B (or A − B), is the set of all elements that are members of A but not members of B. 
	 *     Note that it is valid to "subtract" members of a set that are not in the set, such as removing the element green from 
	 *     the set {1, 2, 3}; doing so has no effect.c
	 * REF: http://en.wikipedia.org/wiki/Set_(mathematics)#Complements
	 * @param sa
	 * @param sb
	 * @return (sa ∪ sb) - sb
	 */
	public Set<E> DIFF(Set<E> sa, Set<E> sb)
	{
		Set<E> s1 = UNION(sa,sb);		
		s1.removeAll(sb);
		return s1;
	}
	
	/**
	 * BD: Two sets can also be "subtracted". The relative complement of B in A (also called the set-theoretic difference of A and B), 
	 *     denoted by A \ B (or A − B), is the set of all elements that are members of A but not members of B. 
	 *     Note that it is valid to "subtract" members of a set that are not in the set, such as removing the element green from 
	 *     the set {1, 2, 3}; doing so has no effect.
	 * REF: http://en.wikipedia.org/wiki/Set_(mathematics)#Complements
	 * @param sa
	 * @param sb
	 * @return sa - sb
	 */
	public Set<E> COMPLEMENT(Set<E> sa, Set<E> sb)
	{
		return DIFF(sa,sb);
	}
	
	
	protected Set<E> newSet()
	{
		Set<E> s = null;
		try {
			s = (Set<E>)SetClass.newInstance();
		} catch (Exception e) {
			s = new HashSet<E>();
			e.printStackTrace();
		} 
		return s;
	}
	
	/**
	 * BD: A new set can also be constructed by determining which members two sets have "in common". 
	 *     The intersection of A and B, denoted by A ∩ B, is the set of all things that are members of both A and B. 
	 *     If A ∩ B = ∅, then A and B are said to be disjoint.
	 * REF: http://en.wikipedia.org/wiki/Set_(mathematics)#Intersections
	 * @param sa
	 * @param sb
	 * @return sa ∩ sb
	 */
	public Set<E> INTERSECT(Set<E> sa, Set<E> sb)
	{
		Set<E> s = newSet();
			
		if(sa.size()>sb.size())
		{
			s.addAll(sa);
			s.retainAll(sb);
		}
		else
		{
			s.addAll(sb);
			s.retainAll(sa);
		}
		return s;
	}
	
	/**
	 * BD: Two sets can be "added" together. The union of A and B, denoted by A ∪ B, 
	 *     is the set of all things that are members of either A or B.
	 * REF: http://en.wikipedia.org/wiki/Set_(mathematics)#Unions
	 * @param sa
	 * @param sb
	 * @return sa ∪ sb
	 */
	public Set<E> UNION(Set<E> sa, Set<E> sb)
	{
		Set<E> s =   newSet();
		if(sa.size()>sb.size())
		{
			s.addAll(sa);
			s.addAll(sb);
		}
		else
		{
			s.addAll(sb);
			s.addAll(sa);
		}
		return s;
	}
	
	public void ADD(Set<E> set, E...elms)
	{
		for(E e:elms) set.add(e);
	}
	
	public String PrintSet(Set<E> set)
	{
		StringBuffer strBuf = new StringBuffer("[");
		Iterator<E> iter = set.iterator();
		E s = null;
		while(iter.hasNext())
		{
			if(s!=null) strBuf.append(",");
			strBuf.append(String.format("%s", s=iter.next()));
		}
		strBuf.append("]");
		return strBuf.toString();
	}
	
	public static void main(String args[])
	{
		SetOP.SetClass = TreeSet.class;
		Set<Integer> sa = new HashSet<Integer>();
		Set<Integer> sb = new HashSet<Integer>();
		SetOP<Integer> sop = new SetOP<Integer>();
		sop.ADD(sa, 1,2,3,4);
		sop.ADD(sb, 3,4,5,6);
		Set<Integer> so = null;
		System.out.printf("\t[Info] Set sa: %s\n", sop.PrintSet(sa));
		System.out.printf("\t[Info] Set sb: %s\n", sop.PrintSet(sb));
		
		System.out.printf("\t[Info] Union of sa,sb: ");
		so = sop.UNION(sa, sb);
		if(so.size()==0) System.out.printf("\t[]\n");
		else
		{
			Integer elms[] = new Integer[so.size()];
			so.toArray(elms);
			System.out.printf(" [%d", elms[0]);
			for(int i=1; i<elms.length; i++) System.out.printf(",%d", elms[i]);
			System.out.println("]");
		}
		System.out.printf("\t[Info] Difference of sa,sb: ");
		so = sop.DIFF(sa, sb);
		if(so.size()==0) System.out.printf(" []\n");
		else
		{
			Integer elms[] = new Integer[so.size()];
			so.toArray(elms);
			System.out.printf(" [%d", elms[0]);
			for(int i=1; i<elms.length; i++) System.out.printf(",%d", elms[i]);
			System.out.println("]");
		}
		System.out.printf("\t[Info] Intersection of sa,sb: ");
		so = sop.INTERSECT(sa, sb);
		if(so.size()==0) System.out.printf(" []\n");
		else
		{
			Integer elms[] = new Integer[so.size()];
			so.toArray(elms);
			System.out.printf(" [%d", elms[0]);
			for(int i=1; i<elms.length; i++) System.out.printf(",%d", elms[i]);
			System.out.println("]");
		}
	}
}
