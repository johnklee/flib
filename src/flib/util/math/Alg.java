package flib.util.math;

import flib.util.TimeStr;

public class Alg {
	public static int Levenshtein1(String str1, String str2)
	{
		int str1_len = str1.length();
		int str2_len = str2.length();
		if(str1_len==0) return str2.length();
		if(str2_len==0) return str1.length();
		int cost = 0;
		if(str1.charAt(0) != str2.charAt(0)) cost=1;
		return Math.min(Math.min(Levenshtein1(str1.substring(1, str1_len), str2)+1,
				                 Levenshtein1(str1, str2.substring(1, str2_len))+1),
				        Levenshtein1(str1.substring(1, str1.length()), str2.substring(1, str2_len)))+cost;
	}
	
	public static int Levenshtein2(String str1, String str2)
	{
		int str1_len = str1.length();
		int str2_len = str2.length();
		if(str1_len==0) return str2.length();
		if(str2_len==0) return str1.length();
		if(str1.equals(str2)) return 0;
		int cost = 0;
		if(str1.charAt(0) != str2.charAt(0)) cost=1;
		return Math.min(Math.min(Levenshtein2(str1.substring(1, str1_len), str2)+1,
				                 Levenshtein2(str1, str2.substring(1, str2_len))+1),
				        Levenshtein2(str1.substring(1, str1.length()), str2.substring(1, str2_len)))+cost;
	}
	
	public static int Levenshtein3(String str1, String str2)
	{		
		// 1) Trim front same sub string
		int diff = -1;
		for(int i=0; i<Math.min(str1.length(), str2.length()); i++)
		{
			if(str1.charAt(i)!=str2.charAt(i))
			{
				diff=i;
				break;
			}
		}
		if(diff>0)
		{
			str1 = str1.substring(diff, str1.length());
			str2 = str2.substring(diff, str2.length());
		}
		// 2) Trim back same sub string
		for(int i=0; i<Math.min(str1.length(), str2.length()); i++)
		{
			if(str1.charAt(str1.length()-i-1)!=str2.charAt(str2.length()-i-1))
			{
				diff=i;
				break;
			}
		}
		if(diff>0)
		{
			str1 = str1.substring(0, str1.length()-diff);
			str2 = str2.substring(0, str2.length()-diff);
		}
		
		int str1_len = str1.length();
		int str2_len = str2.length();
		if(str1_len==0) return str2.length();
		if(str2_len==0) return str1.length();
		
		return Math.min(Math.min(Levenshtein3(str1.substring(1, str1_len), str2)+1,
                Levenshtein3(str1, str2.substring(1, str2_len))+1),
                Levenshtein3(str1.substring(1, str1.length()), str2.substring(1, str2_len)))+1;		
	}
	
	public static void main(String args[])
	{
		String str1 = "abcdefgh";
		String str2 = "hgfedcba";
		long st = System.currentTimeMillis();
		System.out.printf("Levenshtein distance1=%d (%d ms)\n", Levenshtein1(str1, str2), System.currentTimeMillis()-st);
		st = System.currentTimeMillis();
		System.out.printf("Levenshtein distance2=%d (%d ms)\n", Levenshtein2(str1, str2), System.currentTimeMillis()-st);
		st = System.currentTimeMillis();
		System.out.printf("Levenshtein distance3=%d (%d ms)\n", Levenshtein3(str1, str2), System.currentTimeMillis()-st);
	}
}
