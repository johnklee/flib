package flib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JList<T> implements List<T>{
	List<T> list;
	
	public JList(List<T> list){this.list = list;}
	public JList(){list = new ArrayList<T>();}

	public String join(String sep){return join(sep, "");}
	public String join(String sep, String pad)
	{
		StringBuffer outBuf = new StringBuffer("");
		if(list.size()>0)
		{
			outBuf.append(String.format("%s%s", list.get(0), pad));
			for(int i=1; i<list.size(); i++) outBuf.append(String.format("%s%s%s%s", sep, pad, list.get(i), pad));
		}
		return outBuf.toString().trim();
	}
	
	public boolean add(T t) {
		return list.add(t);
	}

	public void add(int i, T t) {
		list.add(i, t);		
	}

	public boolean addAll(Collection<? extends T> ts) {
		return list.addAll(ts);
	}

	public boolean addAll(int i, Collection<? extends T> ts) {
		return list.addAll(i, ts);
	}

	public void clear() {
		list.clear();		
	}

	public boolean contains(Object t) {
		return list.contains(t);
	}

	public boolean containsAll(Collection<?> ts) {
		return list.containsAll(ts);
	}

	public T get(int i) {
		return list.get(i);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<T> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	public ListIterator<T> listIterator(int i) {
		return list.listIterator(i);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public T remove(int i) {
		return list.remove(i);
	}

	public boolean removeAll(Collection<?> ts) {
		return list.removeAll(ts);
	}

	public boolean retainAll(Collection<?> ts) {
		return list.retainAll(ts);
	}

	public T set(int i, T t) {
		return list.set(i, t);
	}

	public int size() {
		return list.size();
	}

	public List<T> subList(int i, int j) {
		return list.subList(i, j);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] array) {
		return list.toArray(array);
	}
	
}
