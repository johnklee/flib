package flib.env;

import flib.util.Config;
import flib.util.JDebug;
import java.util.logging.Logger;
import flib.util.JDebug.EFormatType;
import java.util.logging.Level;

public class Envset {
    public static String 		breakLine = "\r\n";
    private static boolean 		envCheckFlag = false;
    private static boolean 		debugFlag = false;
    private static boolean 		debugFileFlag = false;
    private static String 		debugFilePath = "envset.log";
    private static EFormatType 	FormatType = EFormatType.Simple;
    private static int			LogFileSizeLimit = 1024000;
    private static Level		LogFileHandlerLevel = Level.INFO;
    

    static{
        String osType = System.getProperty("os.name");
        if(osType.contains("Windows")) {
            breakLine = "\r\n";
        } else {
            breakLine = "\n";
        }
    }

    public static void checkEnv(){
        if(!isEnvCheckFlag()) {
            Config config = new Config();
            //Logger logger = Logger.getLogger("flib.util.Config");
            //logger.info(System.getProperty("user.dir"));
            /*Check debug*/
            String debg = config.get("debug");
            if(debg!=null && debg.equalsIgnoreCase("true")) {
                setDebugFlag(true);
            } else {
                //logger.info("Debug setting:"+debg);
            }
            String debgFilePath = config.get("debug_file");
            if(debgFilePath!=null && !debg.isEmpty()) {
                setDebugFilePath(debgFilePath);
                setDebugFileFlag(true);
                String debugFormat = config.get("debug_file_format");
                if(debugFormat!=null){
                	if(debugFormat.equalsIgnoreCase("xml"))FormatType = EFormatType.XML;
                	else if(debugFormat.equalsIgnoreCase("simple")) FormatType = EFormatType.Simple;
                }
                String debugFileSizeLimit = config.get("debug_file_limit");
                if(debugFileSizeLimit!=null) {try{LogFileSizeLimit = Integer.valueOf(debugFileSizeLimit);}catch(Exception e){}}
                String debugFileLevel = config.get("debug_file_level");
                if(debugFileLevel!=null) {
                	if(debugFileLevel.equalsIgnoreCase("info")) LogFileHandlerLevel = Level.INFO;
                	else if(debugFileLevel.equalsIgnoreCase("config")) LogFileHandlerLevel = Level.CONFIG;
                	else if(debugFileLevel.equalsIgnoreCase("off")) LogFileHandlerLevel = Level.OFF;
                	else if(debugFileLevel.equalsIgnoreCase("warning")) LogFileHandlerLevel = Level.WARNING;
                	else if(debugFileLevel.equalsIgnoreCase("severe")) LogFileHandlerLevel = Level.SEVERE;
                }
            } else {
            	setDebugFilePath(null);
            }
            setEnvCheckFlag(true);
        }
    }

    /**
     * @return the envCheckFlag
     */
    public static boolean isEnvCheckFlag() {
        return envCheckFlag;
    }

    /**
     * @param aEnvCheckFlag the envCheckFlag to set
     */
    public static void setEnvCheckFlag(boolean aEnvCheckFlag) {
        envCheckFlag = aEnvCheckFlag;
    }

    /**
     * @return the debugFlag
     */
    public static boolean isDebugFlag() {
        return debugFlag;
    }

    /**
     * @param aDebugFlag the debugFlag to set
     */
    public static void setDebugFlag(boolean aDebugFlag) {
        debugFlag = aDebugFlag;
    }

    /**
     * @return the debugFileFlag
     */
    public static boolean isDebugFileFlag() {
        return debugFileFlag;
    }

    /**
     * @param aDebugFileFlag the debugFileFlag to set
     */
    public static void setDebugFileFlag(boolean aDebugFileFlag) {
        debugFileFlag = aDebugFileFlag;
    }

    /**
     * @return the debugFilePath
     */
    public static String getDebugFilePath() {
        return debugFilePath;
    }

    /**
     * @param aDebugFilePath the debugFilePath to set
     */
    public static void setDebugFilePath(String aDebugFilePath) {
        debugFilePath = aDebugFilePath;
    }

	public static String getBreakLine() {
		return breakLine;
	}

	public static JDebug.EFormatType getFormatType() {
		return FormatType;
	}

	public static int getLogFileSizeLimit() {
		return LogFileSizeLimit;
	}

	public static void setLogFileSizeLimit(int logFileSizeLimit) {
		LogFileSizeLimit = logFileSizeLimit;
	}

	public static Level getLogFileHandlerLevel() {
		return LogFileHandlerLevel;
	}

	public static void setLogFileHandlerLevel(Level logFileHandlerLevel) {
		LogFileHandlerLevel = logFileHandlerLevel;
	}     	
}
