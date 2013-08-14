package flib.util;

import java.util.ArrayList;
import java.util.List;

public class Tuple {
	public List<Object> datas = null;
	
	public Tuple(){datas = new ArrayList<Object>();}
	public Tuple(Object ...objs)
	{
		this();
		for(Object o:objs) datas.add(o);
	}
	
	public int size(){return datas.size();}
	public Object get(int i){return datas.get(i);}
	public void put(Object o){datas.add(o);}
	public Object get(int i, Object def){
		if(i<datas.size()) return get(i);
		else return def;
	}
	
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
