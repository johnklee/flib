package flib.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class JMap<K,V> implements Map<K,V>{
	Map<K,V> map = null;
	V dev;
	
	public JMap(Map<K,V> map, V v)
	{
		this.map = map;
		this.dev = v;
	}

	public void clear() 
	{
		map.clear();		
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object val) {
		return map.containsValue(val);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	public V get(Object key) {
		V val = map.get(key);
		if(val==null)
		{
			map.put((K)key, dev);
			return dev;
		}
		return val;
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public V put(K k, V v) {
		return map.put(k, v);
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		this.map.putAll(map);		
	}

	public V remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<V> values() {
		return map.values();
	}
}
