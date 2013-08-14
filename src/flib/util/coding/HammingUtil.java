package flib.util.coding;

/**
 * BD: Hamming Toolkit
 * Reference:
 * 		- Hamming distance
 * 		  http://en.wikipedia.org/wiki/Hamming_distance
 * 	    - Levenshtein distance
 *   	  http://en.wikipedia.org/wiki/Levenshtein_distance
 *   	- Java實例9 - 漢明距離Hamming Distance
 *   	  http://blog.csdn.net/kindterry/article/details/6581344
 *   
 * @author John
 *
 */
public class HammingUtil {
	/**
	 * BD: Calculate Hamming Distance between str1 and str2.
	 * @param str1: Input string1
	 * @param str2: Input string2
	 * @return Hamming distance
	 */
	public static int GetDistance(String str1, String str2)
	{
		
		if(str1.length()==str2.length())
		{
			int distance=0;
			for  ( int  i =  0 ; i < str1.length(); i++) {  
                if  (str1.charAt(i) != str2.charAt(i)) {  
                    distance++;  
                }  
            } 
			return distance;
		}
		else if(str1.length()>str2.length())
		{
			if(str1.indexOf(str2)>=0) return str1.length()-str2.length();
			else
			{
				return LevenshteinHNDistance2(str1, str2);
			}
		}
		else
		{
			if(str2.indexOf(str1)==0) return str2.length()-str1.length();
			else
			{
				return LevenshteinHNDistance2(str1, str2);
			}
		}
	}
	
	public static int LevenshteinHNDistance2(String s, String t)
	{
		int s_len = s.length(), t_len = t.length(); 				
		int fi = 0, ei = 0;
		while(fi<s_len && fi<t_len && s.charAt(fi)==t.charAt(fi)) fi++;		
		while(s_len-ei>0 && t_len-ei>0 && (s.charAt(s_len-ei-1) == t.charAt(t_len-ei-1))) ei++;
		if(ei>0) ei--;
		//System.out.printf("\t[Test] fi=%d; sei=%d, tei=%d...\n", fi, s_len-ei, t_len-ei);
		s = s.substring(fi, s_len-ei);
		t = t.substring(fi, t_len-ei);
		if(s.length()==0) return t.length();
		else if(t.length()==0) return s.length();
		if(s.length()>t.length())
		{
			StringBuffer bnkBuf = new StringBuffer("");
			for(int i=0; i<s.length()-t.length();i++) bnkBuf.append(" ");
			return Math.min(GetDistance(s, bnkBuf.toString()+t), GetDistance(s, t+bnkBuf.toString()));
		}
		else
		{
			StringBuffer bnkBuf = new StringBuffer("");
			for(int i=0; i<t.length()-s.length();i++) bnkBuf.append(" ");
			return Math.min(GetDistance(bnkBuf.toString()+s, t), GetDistance(s+bnkBuf.toString(), t));
		}
	}
	
	public static int LevenshteinHNDistance(String s, String t)
	{		
		int s_len = s.length(), t_len = t.length(); 				
		int fi = 0, ei = 0, cost=0;
		while(fi<s_len && fi<t_len && s.charAt(fi)==t.charAt(fi)) fi++;		
		while(s_len-ei>0 && t_len-ei>0 && (s.charAt(s_len-ei-1) == t.charAt(t_len-ei-1))) ei++;
		if(ei>0) ei--;
		//System.out.printf("\t[Test] fi=%d; sei=%d, tei=%d...\n", fi, s_len-ei, t_len-ei);
		s = s.substring(fi, s_len-ei);
		t = t.substring(fi, t_len-ei);
		if(s.length()==0) return t.length();
		else if(t.length()==0) return s.length();
		if(s.charAt(0)!=t.charAt(0)) cost=1;
		int acost =  LevenshteinHNDistance(s.substring(1), t.substring(1))+cost;
		//System.out.printf("\t[Test] %s | %s -> Cost=%d...\n", s, t, acost);
		return acost;
	}
	
	public static int LevenshteinDistance(String s, String t)
	{
		if(s.length()==0) return t.length();
		else if(t.length()==0) return s.length();
		
		int cost = 0;
		if(s.charAt(0) != t.charAt(0)) cost = 1;		
		return Math.min(LevenshteinDistance(s.substring(1), t)+1, 
				        Math.min(LevenshteinDistance(s, t.substring(1))+1, 
				        		LevenshteinDistance(s.substring(1), t.substring(1))+cost));
		//return LevenshteinDistance(s.substring(1), t.substring(1))+cost;
	}
	
	public static int GetWeight(int i)
	{
		int  n;  
        for  (n =  0 ; i >  0 ; n++) {  
            i &= (i -  1 );  
        }  
        return  n;
	}
	
	public  static  void  main(String[] args) {  		
        String str1 =  "facebook" ;  
        String str2 =  "facboak" ;   
        String str3 =  "toned";
        String str4 =  "roses";
        
        int  distance1 = HammingUtil.GetDistance(str1, str2);  
        int  distance2 = HammingUtil.GetDistance(str3, str4);
        System.out.printf("\t[Info] Hamming distance between '%s' and '%s' is %d...\n",str1, str2, distance1);
        System.out.printf("\t[Info] Hamming distance between '%s' and '%s' is %d...\n",str3, str4, distance2);
        int strInBinary = 255;
        int  weight = HammingUtil.GetWeight( strInBinary );  
        System.out.printf("\t[Info] Hamming weight of '%s' is %d...\n", Integer.toBinaryString(strInBinary), weight);  
    }
}
