package flib.util;

import java.util.Calendar;
import java.util.Date;

public class TimeStr {
	public long 			timeElapse = 0;
	public static TimeStr 	instance = null;
	public static boolean	ShowSec = true;
	public static boolean 	ShowMin = true;
	public static boolean	ShowHr = true;
	public static boolean 	ShowDay = true;
	public static long		HourInMS = 60*60*1000; 
	
	public enum Unit{
		SECOND, MINUTE, HOUR
	}
	
	public TimeStr(long ms)
	{
		this.timeElapse = ms;
	}
	
	public static TimeStr getInstance(){
		if(instance==null) 
			instance = new TimeStr(0);
		return instance;
	}
	
	public long totSec(){ return timeElapse/1000;}
	public long sec(){return totSec()%60;}
	public long totMin(){return totSec()/60;}
	public long min(){return totMin()%60;}
	public long totHr(){return totMin()/60;}
	public long hr(){return totHr()%24;}
	public long totDy(){return totHr()/24;}
	
	public synchronized long timeDiff(Date start, Date end, Unit u)
	{
		long timeDiff = end.getTime() - start.getTime();
		if(timeDiff>0)
		{
			this.timeElapse = timeDiff;
			switch(u)
			{
			case MINUTE:
				return totMin();
			case HOUR:
				return totHr();
			default:
				return totSec();
			}
		}
		return 0;
	}
	
	@Override
	public String toString()
	{
		return toString(false);
	}
	
	public String toString(boolean withMS){		
		long mSec = timeElapse%1000;
		long totSec = timeElapse/1000;
		long sec = totSec%60;
		long totMin = totSec/60;
		long min = totMin%60;
		long totHr = totMin/60;
		long hr = totHr%24;
		long totDy = totHr/24;
		StringBuffer sb = new StringBuffer("");
		if(totDy>0 && ShowDay) sb.append(String.format("%d day%s", totDy, totDy>1?"s":""));
		if(hr>0 && ShowHr) sb.append(String.format(" %d hr%s", hr, totHr>1?"s":""));
		if(min>0 && ShowMin) sb.append(String.format(" %d min%s", min, totMin>1?"s":""));
		if(sec>0 && ShowSec) sb.append(String.format(" %d sec", sec));
		if(sb.toString().trim().isEmpty()||withMS) return String.format("%s %d ms", sb.toString(), mSec).trim();		
		return sb.toString().trim();
	}
	
	public static String toStringFrom(long sts){return ToStringFrom(sts, false);}
	public static String ToStringFrom(long sts){return ToStringFrom(sts, false);}
	public static String ToStringFrom(long sts, boolean hasMS){return new TimeStr(System.currentTimeMillis()-sts).toString(hasMS);}
	
	public static String ToString(long ts){return new TimeStr(ts).toString();}
	public static String ToString(long ts, boolean hasMS){return new TimeStr(ts).toString(hasMS);}
	
	public static Tuple HourDiff(Date from ,Date to)
	{
		if(from.before(to))
		{
			long diff = to.getTime() - from.getTime();
			long hour = diff/HourInMS;
			long last = diff%HourInMS;
			return new Tuple(hour, last);
		}
		return new Tuple(0);
	}
	
	public static long Daydiff(Date from, Date to)
	{
		if(from.before(to))
		{
			Calendar fromCal = Calendar.getInstance();
			Calendar toCal = Calendar.getInstance();
			fromCal.setTime(from);
			toCal.setTime(to);
			if(fromCal.get(Calendar.YEAR)==toCal.get(Calendar.YEAR))
			{
				if(fromCal.get(Calendar.MONTH)==toCal.get(Calendar.MONTH))
				{
					int toDayOfMonth = toCal.get(Calendar.DAY_OF_MONTH);
					int fromDayOfMonth = fromCal.get(Calendar.DAY_OF_MONTH); 
					if(toDayOfMonth==fromDayOfMonth)
					{
						return 0;
					}
					else
					{
						return toDayOfMonth-fromDayOfMonth;
					}
				}
				else
				{
					return toCal.get(Calendar.DAY_OF_YEAR)-fromCal.get(Calendar.DAY_OF_YEAR);
				}
			}
			else
			{
				long timeDiff = to.getTime()-from.getTime();
				long dayCnt = timeDiff/(24*60*60*1000);
				long rsd = timeDiff%(24*60*60*1000);
				int od = toCal.get(Calendar.DAY_OF_YEAR);
				toCal.add(Calendar.MILLISECOND, (int)rsd);
				if(toCal.get(Calendar.DAY_OF_YEAR)==od) return dayCnt;
				else return dayCnt+1;
			}
		}
		return 0;
	}
	
	public static void main(String args[]) throws Exception
	{
		TimeStr ts = new TimeStr(123000456);
		System.out.printf("\t[Test] Time elapse %s\n", TimeStr.ToString(45678, true));
		System.out.printf("\t[Test] ts=%s\n", ts);
		Date now = new Date();
		Thread.sleep(50);
		ts = TimeStr.getInstance();
		System.out.printf("\t[Test] ts=%s\n", ts.timeDiff(now, new Date(), Unit.SECOND));
		long st = System.currentTimeMillis();
		Thread.sleep(100);
		System.out.printf("\t[Test] %s\n", TimeStr.ToStringFrom(st));
	}
}

