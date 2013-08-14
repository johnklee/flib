package flib.util;

import java.io.*;

public class Test {

    public static void main(String args[]) {
        File f = new File("./john.ini");
        if(f.exists()) {
           try{
               BufferedReader br = new BufferedReader(new FileReader(f));
               String line;
               while((line=br.readLine())!=null) {
                   System.out.println(new String(line.getBytes("ASCII"),"UTF-8"));
               }
           }catch(IOException ioe) {
               ioe.printStackTrace();
           }
        }        
    }
}
