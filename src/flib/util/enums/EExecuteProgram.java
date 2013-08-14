/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package flib.util.enums;

/**
 *
 * @author John-Lee
 */
public enum EExecuteProgram {
    Perl("Perl","pl"),Null("",""),Jar("java -jar","jar");
    private String prgn;
    private String subFileName;
    private EExecuteProgram(String pn, String subf) {
        prgn = pn;
        subFileName = subf;
    }
    public static EExecuteProgram getExecuteProgram(String subfn) {
        if(subfn.trim().equalsIgnoreCase(EExecuteProgram.Perl.getSubName())) {
            return EExecuteProgram.Perl;
        } else if(subfn.trim().equalsIgnoreCase(EExecuteProgram.Jar.getSubName())){
            return EExecuteProgram.Jar;
        } else {
            return EExecuteProgram.Null;
        }
    }

    public static boolean isSupport(String proge) {
        EExecuteProgram[] ees = EExecuteProgram.values();
        for(EExecuteProgram e:ees) {
            if(e.toString().equalsIgnoreCase(proge)) {
                return true;
            }
        }
        return false;
    }

    public String getSubName(){
        return subFileName;
    }

    public String toString(){
        return prgn;
    }
}
