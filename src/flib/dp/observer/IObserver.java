package flib.dp.observer;

public interface IObserver {	
	public void setChanged(boolean b);	
	public void update(boolean result,int state);
        public void update(String msg);
}
