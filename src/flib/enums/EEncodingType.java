/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package flib.enums;

/**
 *
 * @author John-Lee
 */
public enum EEncodingType {
    Big5("Big5"),UTF8("UTF8"),UTF_16BE("UTF-16BE"),UTF_16LE("UTF-16LE");
    private String str;
    private EEncodingType(String s){
        str = s;
    }

    public String toString(){
        return str;
    }
}
