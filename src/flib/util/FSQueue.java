package flib.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class FSQueue<T> implements Queue<T>{
	private Queue<T> 	queue = new LinkedList<T>();
	private int 		size_of_queue = 10;
	public T 			lastPopup = null;
	
	public FSQueue(){}
	public FSQueue(int size)
	{
		this.size_of_queue = size;
	}
	
	public T access(int i){return ((LinkedList<T>)queue).get(i);}

	public boolean addAll(Collection<? extends T> objs) {
		return queue.addAll(objs);
	}

	public void clear() {queue.clear();}

	public boolean contains(Object obj) {
		return queue.contains(obj);
	}

	public boolean containsAll(Collection<?> objs) {
		return queue.containsAll(objs);
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public Iterator<T> iterator() {
		return queue.iterator();
	}

	public boolean remove(Object obj) {
		return queue.remove(obj);
	}

	public boolean removeAll(Collection<?> objs) {
		return queue.removeAll(objs);
	}

	public boolean retainAll(Collection<?> objs) {
		return queue.retainAll(objs);
	}

	public int size() {
		return queue.size();
	}
	
	public Object[] toArray() {
		return queue.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	public boolean add(T obj) {
		if(!isFull()) return queue.add(obj);
		else
		{
			lastPopup = queue.poll();
			return queue.add(obj);
		}
	}

	public T element() {
		if(!isEmpty()) return queue.element();
		return null;
	}

	public boolean isFull() {return (queue.size()>=size_of_queue);}
	public boolean offer(T obj) {
		if(!isFull()) return queue.offer(obj);
		else 
		{
			lastPopup = queue.poll();
			return queue.offer(obj);
		}
	}

	public T peek() {
		return queue.peek();
	}

	public T poll() {
		lastPopup = queue.poll();
		return lastPopup;
	}

	public T remove() {
		return queue.remove();
	}
	
	public String lastN(int topN)
	{
		StringBuffer strBuf = new StringBuffer("");
		if(topN>size()) return "[]";
		else
		{
			Object obs[] = this.toArray();
			int sp = obs.length-topN;
			strBuf.append(String.format("[%s", obs[sp]));
			for(int i=sp+1; i<obs.length; i++) strBuf.append(String.format(",%s", obs[i]));
			strBuf.append("]");
		}
		return strBuf.toString();
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FSQueue<Integer> fsQue = new FSQueue<Integer>(10);
		for(int i=1; i<20; i++) 
		{
			fsQue.offer(i);
			System.out.printf("\t[Test] Add %d and lastPopup=%d...\n", i, fsQue.lastPopup);
			System.out.printf("\t[Test] Queue=%s\n", fsQue);
		}
		System.out.printf("\t[Test] fsQue.size()=%d\n", fsQue.size());
		System.out.printf("\t[Test] fsQue.element()=%d\n", fsQue.element());
		System.out.printf("\t[Test] Queue=%s\n", fsQue);
		System.out.printf("\t[Test] Queue last 5=%s\n", fsQue.lastN(5));
		while(!fsQue.isEmpty())
		{
			System.out.printf("\t[Test] fsQue.poll()=%d\n", fsQue.poll());
		}
	}
}
