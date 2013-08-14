/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package flib.util.inner.enums;

/**
 *
 * @author John-Lee
 */
public enum EStreamType {
    OUTPUT("STDOUT"),ERROR("STDERR");
    private String msg;
    private EStreamType(String m) {
        msg = m;
    }
    public String toString() {
        return msg;
    }
    public String getMsg() {
        return msg;
    }
}
