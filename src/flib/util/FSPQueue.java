package flib.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * BD: Fixed size priority queue
 * @author John
 *
 */
public class FSPQueue<T> implements Queue<T>, Serializable{
	private static final long 	serialVersionUID = 1L;
	private int 				size_of_queue = -1;
	private PriorityQueue<T> 	pq;
	public T 					lastPopup = null;
	
	public FSPQueue(){ pq = new PriorityQueue<T>();}
	public FSPQueue(int size)
	{
		this();
		this.size_of_queue = size;
	}
	
	public FSPQueue(Comparator<T> cmp, int size)
	{
		this.size_of_queue = size;
		pq = new PriorityQueue<T>(size, cmp);
	}

	public void changeSize(int size)
	{
		size_of_queue = size;
	}
	
	public boolean addAll(Collection<? extends T> colt) {
		return pq.addAll(colt);
	}

	public void clear() {
		pq.clear();
		
	}

	public boolean contains(Object o) {
		return pq.contains(o);
	}

	public boolean containsAll(Collection<?> colt) {
		return pq.containsAll(colt);
	}

	public boolean isEmpty() {
		return pq.isEmpty();
	}

	public Iterator<T> iterator() {
		return pq.iterator();
	}

	public boolean remove(Object o) {
		return pq.remove(o);
	}

	public boolean removeAll(Collection<?> colt) {
		return pq.removeAll(colt);
	}

	public boolean retainAll(Collection<?> colt) {
		return pq.containsAll(colt);
	}

	public int size() {
		return pq.size();
	}

	public Object[] toArray() {
		return pq.toArray();
	}

	public <T> T[] toArray(T[] t) {
		return pq.toArray(t);
	}

	public boolean add(T t) {
		synchronized(pq)
		{
			boolean rst = pq.add(t);
			if(size_of_queue>0 && pq.size()>size_of_queue)
			{
				lastPopup=pq.poll();
			}
			return rst;
		}
	}

	public T element() {
		return pq.element();
	}

	public boolean offer(T t) {
		return pq.offer(t);
	}

	public T peek() {
		return pq.peek();
	}

	public T poll() {
		return pq.poll();
	}

	public T remove() {
		return pq.poll();
	}

	@Override
	public String toString()
	{
		StringBuffer strBuf = new StringBuffer("");
		if(size()>0)
		{
			Object obs[] = this.toArray();
			strBuf.append(String.format("[%s", obs[0]));
			for(int i=1; i<obs.length; i++) strBuf.append(String.format(",%s", obs[i]));
			strBuf.append("]");
		}
		else
		{
			strBuf.append("[]");
		}
		return strBuf.toString();
	}
	
	public String toString(String sep)
	{
		StringBuffer strBuf = new StringBuffer("");
		if(size()>0)
		{
			Object obs[] = this.toArray();
			strBuf.append(obs[0]);
			for(int i=1; i<obs.length; i++) strBuf.append(String.format("%s%s", sep, obs[i]));
		}
		return strBuf.toString();
	}
	
	public static void main(String args[])
	{
		Comparator<Integer> cmp = new Comparator<Integer>()
		{
			public int compare(Integer i1, Integer i2) {return i2.compareTo(i1);}
		};
		FSPQueue<Integer> fsPQ = new FSPQueue<Integer>(5);
		for(int i=0; i<10; i++) fsPQ.add(i);
		fsPQ.add(1);
		fsPQ.add(1);
		fsPQ.add(1);
		fsPQ.add(12);
		System.out.printf("%s (%d)\n", fsPQ, fsPQ.size());
		fsPQ.add(7);
		System.out.printf("%s (%d)\n", fsPQ, fsPQ.size());
	}
}
