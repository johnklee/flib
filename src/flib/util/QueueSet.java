package flib.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class QueueSet<E> implements Queue<E> {
	Set<E> set = new HashSet<E>();
	Queue<E> queue = new LinkedList<E>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public boolean addAll(Collection<? extends E> colls) {
		for(E e:colls)
		{
			if(set.add(e)) queue.add(e);
		}
		return true;
	}

	public void clear() {
		set.clear();
		queue.clear();
	}

	public boolean contains(Object e) {
		return set.contains(e);
	}

	public boolean containsAll(Collection<?> coll) {
		return set.containsAll(coll);
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public Iterator<E> iterator() {
		return queue.iterator();
	}

	public boolean remove(Object e) {
		if(set.remove(e)) return queue.remove(e);
		return false;
	}

	public boolean removeAll(Collection<?> colls) {
		for(Object e:colls) if(set.remove(e)) queue.remove(e);
		return true;
	}

	public boolean retainAll(Collection<?> colls) {
		return set.retainAll(colls);
	}

	public int size() {
		return set.size();
	}

	public Object[] toArray() {
		return set.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return set.toArray(a);
	}

	public boolean add(E e) {
		if(set.add(e)) 
		{
			queue.add(e);
			return true;
		}
		return false;
	}

	public E element() {
		return queue.element();
	}

	public boolean offer(E e) {
		if(set.add(e))
		{
			return queue.offer(e);
		}
		return false;
	}

	public E peek() {
		return queue.peek();
	}

	public E poll() {
		E e = queue.poll();
		if(e!=null) set.remove(e);
		return e;
	}

	public E remove() {
		E e = queue.remove();
		if(e!=null) set.remove(e);
		return e;
	}

}
