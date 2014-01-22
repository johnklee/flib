package flib.util.os;

import flib.util.Tuple;

public class MonitorInfoBean {
	/** */  
    /** 可使用內存. */  
    private long totalJVMMemory;  
  
    /** */  
    /** 剩餘內存. */  
    private long freeJVMMemory;  
  
    /** */  
    /** 最大可使用內存. */  
    private long maxMemory;  
  
    /** */  
    /** 操作系統. */  
    private String osName;  
  
    /** */  
    /** 總物理內存. */  
    private long totalPhyscailMemorySize;  
  
    /** */  
    /** 剩餘的物理內存. */  
    private long freePhysicalMemorySize;  
  
    /** */  
    /** 已使用的物理內存. */  
    private long usedPhysicalMemory;  
  
    /** */  
    /** 線程總數. */  
    private int totalThread;  
  
    /** */  
    /** cpu使用率. */  
    private double cpuRatio;  
  
    public long getFreeJVMMemory() {  
        return freeJVMMemory;  
    }  
  
    public void setFreeJVMMemory(long freeMemory) {  
        this.freeJVMMemory = freeMemory;  
    }  
  
    public long getFreePhysicalMemorySize() {  
        return freePhysicalMemorySize;  
    }  
  
    public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {  
        this.freePhysicalMemorySize = freePhysicalMemorySize;  
    }  
  
    public long getMaxMemory() {  
        return maxMemory;  
    }  
  
    public void setMaxMemory(long maxMemory) {  
        this.maxMemory = maxMemory;  
    }  
  
    public String getOsName() {  
        return osName;  
    }  
  
    public void setOsName(String osName) {  
        this.osName = osName;  
    }  
  
    public long getTotalJVMMemory() {  
        return totalJVMMemory;  
    }  
  
    public void setTotalJVMMemory(long totalMemory) {  
        this.totalJVMMemory = totalMemory;  
    }  
  
    public long getTotalPhyscailMemorySize() {  
        return totalPhyscailMemorySize;  
    }  
  
    public void setTotalPhyscailMemorySize(long totalMemorySize) {  
        this.totalPhyscailMemorySize = totalMemorySize;  
    }  
  
    public int getTotalThread() {  
        return totalThread;  
    }  
  
    public void setTotalThread(int totalThread) {  
        this.totalThread = totalThread;  
    }  
  
    public long getUsedPyhsicalMemory() {  
        return usedPhysicalMemory;  
    }  
  
    public void setUsedPhysicalMemory(long usedMemory) {  
        this.usedPhysicalMemory = usedMemory;  
    }  
  
    public double getCpuRatio() {  
        return cpuRatio;  
    }  
  
    public void setCpuRatio(double cpuRatio) {  
        this.cpuRatio = cpuRatio;  
    }  
    
    public static Tuple _calcMemInStr(long size)
    {
    	if(Math.abs(size)<1024) return new Tuple("B", (double)size);
    	else
    	{
    		double kb = ((double)size)/1024;
    		if(Math.abs(kb)<1024) return new Tuple("KB", kb);
    		else
    		{
    			double mb = kb/1024;
    			if(Math.abs(mb)<1024) return new Tuple("MB", mb);
    			else
    			{
    				double gb = mb/1024;
    				return new Tuple("GB", gb);
    			}
    		}
    	}
    }
    
    public String diffUsedJVMSize(MonitorInfoBean lastBean)
    {
    	long diff = (lastBean.getTotalJVMMemory()-lastBean.getFreeJVMMemory())-(getTotalJVMMemory()-getFreeJVMMemory());
    	Tuple t = _calcMemInStr(diff);
    	return String.format("%.01f %s", t.get(1), t.get(0));
    }
    
    public String diffFreeJVMSize(MonitorInfoBean lastBean)
    {
    	Tuple t = _calcMemInStr(lastBean.getFreeJVMMemory()-getFreeJVMMemory());
    	return String.format("%.01f %s", t.get(1), t.get(0));
    }
    
    public String diffTotalJVMSize(MonitorInfoBean lastBean)
    {
    	Tuple t = _calcMemInStr(lastBean.getTotalJVMMemory()-getTotalJVMMemory());
    	return String.format("%.01f %s", t.get(1), t.get(0));
    }
}
