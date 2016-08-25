package flib.util.math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Perm {	
	public static List<List<Integer>> PERM_PICK_M_FROM_N(int m, int n)
	{
		if(m>n) return null;
		List<Integer> perms = new ArrayList<Integer>();
		List<Integer> rlist = new ArrayList<Integer>();
		for(int i=0; i<n; i++) perms.add(i);
		return _RECV_PICK_M_FROM_N(perms, rlist, m);
	}
	
	protected static List<List<Integer>> _RECV_PICK_M_FROM_N(List<Integer> list, List<Integer> rlist, int m)
	{
		List<List<Integer>> comb_list = new ArrayList<List<Integer>>();
		if(m==list.size()) 
		{
			List<Integer> list_copy = new ArrayList<Integer>();
			list_copy.addAll(list);
			list_copy.addAll(rlist);
			comb_list.add(list_copy);
			return comb_list;
		}
		else if(m==0)
		{
			List<Integer> list_copy = new ArrayList<Integer>();
			list_copy.addAll(rlist);
			comb_list.add(list_copy);
			return comb_list;
		}
		else
		{
			List<Integer> list_copy = new ArrayList<Integer>();
			List<Integer> rlist_copy = new ArrayList<Integer>();
			list_copy.addAll(list);			
			rlist_copy.addAll(rlist);
			rlist_copy.add(list_copy.remove(0));
			comb_list.addAll(_RECV_PICK_M_FROM_N(list_copy, rlist_copy, m-1));
			comb_list.addAll(_RECV_PICK_M_FROM_N(list_copy, rlist, m));
			return comb_list;
		}
	}
				
	public static List<List<String>> PERM_FROM_SETS(List<Set<String>> setList)
	{
		List<List<String>> permList = new ArrayList<List<String>>();
		_RECV_PERM_FROM_SETS(setList, permList, 0, null);
		return permList;
	}
	
	public static List<List<String>> PERM_FROM_SET(Set<String> set)
	{
		List<List<String>> permList = new ArrayList<List<String>>();
		
		return permList;
	}
	
	protected static void _RECV_PERM_FROM_SETS(List<Set<String>> setList, List<List<String>> permList, int level, List<String> seq)
	{
		if(level == setList.size()-1) // Final level
		{		
			for(String t:setList.get(level)) 
			{
				List<String> f = new ArrayList<String>();
				if(seq!=null) f.addAll(seq);
				f.add(t);
				permList.add(f);
			}
		}
		else
		{
			for(String t:setList.get(level))
			{
				List<String> f = new ArrayList<String>();
				if(seq!=null) f.addAll(seq);
				f.add(t);
				_RECV_PERM_FROM_SETS(setList, permList, level+1, f);
			}
		}
	}
	
	public static void TestPERM_PICK_M_FROM_N()
	{
		int m = 1, n = 5;
		System.out.printf("\t[Info] Pickup %d from %d:\n", m ,n);
		List<List<Integer>> comb_list = Perm.PERM_PICK_M_FROM_N(m, n);
		for(List<Integer> list:comb_list)
		{
			for(Integer v:list) System.out.printf("%d ", v);
			System.out.println();
		}
	}
	
	public static void main(String args[])
	{
		Set<String> set1 = new HashSet<String>();
		set1.add("A"); set1.add("B");
		Set<String> set2 = new HashSet<String>();
		set2.add("1"); set2.add("2");
		Set<String> set3 = new HashSet<String>();
		set3.add("一"); set3.add("二");
		List<Set<String>> setlist = new ArrayList<Set<String>>();
		setlist.add(set1); setlist.add(set2); setlist.add(set3);
		
		List<List<String>> list = Perm.PERM_FROM_SETS(setlist);
		for(List<String> seq:list)
		{
			System.out.printf("\t[Test] Seq: ");
			for(String t:seq) System.out.printf("%s\t", t);
			System.out.println();
		}
	}
}
