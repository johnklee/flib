package flib.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tuple implements Serializable{
	private static final long serialVersionUID = 1L;
	public List<Object> datas = null;
	
	public Tuple(){datas = new ArrayList<Object>();}
	public Tuple(Object ...objs)
	{
		this();
		for(Object o:objs) datas.add(o);
	}
	
	@Override
	public int hashCode(){
		int hash=0;
		for(int i=0; i<size(); i++) hash+=get(i).hashCode();
		return hash;
	}
	public int size(){return datas.size();}
	public Object get(int i){return datas.get(i);}
	public Object set(int i, Object o){return datas.set(i, o);}
	public void put(Object o){datas.add(o);}
	public void puts(Object ...objs){for(Object o:objs) datas.add(o);}
	public Object get(int i, Object def){
		if(i<datas.size()) return get(i);
		else return def;
	}
	
	public String getStr(int i){return (String)datas.get(i);}
	public int getInt(int i){return (Integer)datas.get(i);}
	
	@Override
	public String toString()
	{
		StringBuffer strBuf = new StringBuffer("");
		if(size()==0) return "[]";
		else
		{
			strBuf.append("[");
			strBuf.append(String.format("'%s'", get(0)));
			for(int i=1; i<size(); i++) strBuf.append(String.format(",'%s'", get(i)));
			strBuf.append("]");
		}
		return strBuf.toString();
	}	
}
