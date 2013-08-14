package flib.proto;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface IDebug {
    public void debug(Object msg);
    public void debug(Object msg, Level level);    
}
