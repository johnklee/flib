package flib.util;

import flib.env.Envset;
import flib.proto.IDebug;
import java.util.*;
import java.io.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BD : Simple Tool kit for reading configuration.
 * @author John-Lee
 */
public class Config{
    private HashMap<String,String> configSet; // configuration setting map
    private String path;
    //private IDebug debugKit;

    /**
     * Useing empty parameter constructor, will search the current path with configuration file name as 'john.ini'.
     */
    public Config(){
        this(System.getProperty("user.dir")+File.separator+"john.ini");
    }

    public Config(File configFile) {
        //debugKit = new JDebug();
        //debugKit.debug("Config Path: "+cp);
        path = configFile.getAbsolutePath();
        configSet = new HashMap<String,String>();
        //File configFile = new File(cp);
        if(configFile.exists()) {
            try{
                Properties p = new Properties();
                p.load(new FileReader(configFile));
                Set<Entry<Object,Object>> itset = p.entrySet();
                Iterator<Entry<Object,Object>> ite = itset.iterator();
                while(ite.hasNext()) {
                    Entry<Object,Object> e = ite.next();
                    //debugKit.debug("Key:"+e.getKey());
                    configSet.put(e.getKey().toString().trim(), e.getValue().toString());
                }
            }catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Constructor to tell where to look for configuration file.
     * @param cp : Configuration file path
     */
    public Config(String cp) {
        this(new File(cp));
    }

    /**
     * Get value from specific key.
     * @param key
     * @return
     */
    public String get(String key) {
        return configSet.get(key);
    }

    /*
     * Get key setting which match reaular expression as input argument
     */
    public ArrayList<String> getKeyWithReg(String reg){
        Pattern p = Pattern.compile(reg);
        ArrayList<String> matchList = new ArrayList<String>();
        Set<String> itset = configSet.keySet();
        Iterator<String> ite = itset.iterator();
        Matcher m;
        while(ite.hasNext()) {
            String key = ite.next();
            //System.out.println(key+"="+get(key));
            m = p.matcher(key);
            if(m.find()){
                matchList.add(key);
            }
        }
        return matchList;
    }



    /**
     * Get Int value from specific key.
     * @param key
     * @return
     */
    public int getInt(String key){
        String tmp = configSet.get(key);
        try{
            return Integer.valueOf(tmp);
        }catch(Exception e) {
            return -1;
        }
    }

    /**
     * Obtains key list from configuration file.
     * @return
     */
    public Set<String> keySet() {
        return configSet.keySet();
    }

    /**
     * List out all setting in configuration file.
     */
    public void list(){
        System.out.println("Read config from "+path+"...");
        Set<String> itset = configSet.keySet();
        Iterator<String> ite = itset.iterator();
        while(ite.hasNext()) {
            String key = ite.next();
            System.out.println(key+"="+get(key));
        }
    }

    public static void main(String args[]) {
        Config config = new Config();
        config.list();
        System.out.println("中文");
    }   
}
