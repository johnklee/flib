package gays.tools;

import flib.env.Envset;
import flib.proto.IDebug;
import flib.util.JDebug;
import gays.tools.enums.EArguQuantity;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * BD : Simple tool kit to parse the argument from console
 * 
 * @author John-Lee
 */
public class ArguParser {
	private HashMap<String, Argument> 	argsSet; // Defination of argument
	private boolean 					empty = true;	
	private Logger 						debugKit;
	public boolean 						isSuccessive = false;
	public List<String>					lastArgument = new LinkedList<String>();

	public ArguParser() {
		debugKit = JDebug.getLogger("ArguParser");
	}

	/**
	 * BD : Constructor to receive argument defination and actual argument from console.
	 * 
	 * @param def
	 *            : Defination of argument
	 * @param arg
	 *            : Argument from console
	 */
	public ArguParser(HashMap<String, Object> def, String[] arg) {
		this();
		List<String> argList = new LinkedList<String>(); // Console argument
															// list
		if (arg != null && arg.length > 0) {
			empty = false;
			for (String a : arg) {
				debugKit.info("Add argu from console:" + a);
				argList.add(a);
			}
		}
		
		argsSet = new HashMap<String, Argument>();
		Set<Entry<String, Object>> s = def.entrySet();
		Iterator<Entry<String, Object>> iter = s.iterator();
		while (iter.hasNext()) {
			Entry<String, Object> e = iter.next(); // defination of argument
			Object key = e.getValue();
			Argument argStruct = null;
			if (key instanceof String) {
				argStruct = new Argument(e.getKey(), (String) e.getValue());
			} else if (key instanceof ArguConfig) {
				argStruct = new Argument(e.getKey(), (ArguConfig) e.getValue());
			} else {
				debugKit.warning(String.format("Illegal argument setting! (%s)", key));
				continue;
			}
			argsSet.put(argStruct.getKey(), argStruct);

			/* Parsing argument from command line */
			if(!argStruct.parseArgu(argList))
			{
				/*Handle: Fail to parsing*/
				debugKit.warning(String.format("Parsing error on argument->%s!", argStruct));
				System.err.printf("\t[Error] Parsing error on argument->%s!\n", argStruct);
				//this.showArgus();
				return;
			}
			
			/*
			 * for (String a : argList) { if (argStruct.checkArgu(a)) {
			 * argList.remove(a); break; } }
			 */
		}
		for(String larg:argList) lastArgument.add(larg);
		isSuccessive = true;
	}

	/*
	 * BD : Show Argument messages.
	 */
	public String showArgus() {
		StringBuffer ssb = new StringBuffer("");
		StringBuffer dsb = new StringBuffer("");
		Set<String> keys = argsSet.keySet();
		Iterator<String> ikeys = keys.iterator();
		while (ikeys.hasNext()) {
			Argument a = argsSet.get(ikeys.next());
			//System.out.printf("%s: %s (%s)\r\n", a.getKey(), a.getDescript(), a.quantity);
			//System.out.println(a.getKey() + ": " + a.getDescript());
			if(a.getKey().startsWith("--")) dsb.append(String.format("%s: %s (%s)\r\n", a.getKey(), a.getDescript(), a.quantity));
			else ssb.append(String.format("%s: %s (%s)\r\n", a.getKey(), a.getDescript(), a.quantity));
		}
		System.out.println(String.format("%s%s", ssb.toString(), dsb.toString()));
		return String.format("%s%s", ssb.toString(), dsb.toString());
	}

	/**
	 * BD : Get argument object.
	 * @param arg
	 * @return
	 */
	public Argument getArgument(String arg)
	{
		return argsSet.get(arg);
	}
	
	/**
	 * BD : Getting all given argument from console.
	 * 
	 * @return ArrayList of Object:Argument which contains argument info.
	 */
	public ArrayList<Argument> getSettingArgu() {
		ArrayList<Argument> tmp = new ArrayList<Argument>();
		Set<Entry<String, Argument>> s = argsSet.entrySet();
		Iterator<Entry<String, Argument>> iter = s.iterator();
		while (iter.hasNext()) {
			Entry<String, Argument> e = iter.next();
			if (e.getValue().isSet()) {
				tmp.add(e.getValue());
			}
		}
		return tmp;
	}

	/**
	 * BD : Getting description of specific argument.
	 * 
	 * @param key
	 *            : argument key
	 * @return
	 */
	public String getArguDescrip(String key) {
		Argument argu = argsSet.get(key);
		if (argu != null) {
			return argu.getDescript();
		}
		return "Unknown Argument!";
	}

	/**
	 * 簡介 : 藉由參數的KEY獲取對應參數(-k)由Console設定的值(value). (-k=value) 
	 * @param key: 參數的Key
	 * @return
	 */
	public String getArguValue(String key) {
		Argument argu = argsSet.get(key);
		if (argu != null) {
			return argu.getValue();
		}		
		return null;
	}
	
	/**
	 * BD: 藉由參數的KEY獲取對應參數(-k)由Console設定的值(value). (-k=value) 並轉為整數型態.
	 * @param key: 參數的Key
	 * @return
	 */
	public Integer getArguIntValue(String key) 
	{
		Argument argu = argsSet.get(key);
		if (argu != null) {
			return argu.getIntValue();
		}		
		return null;
	}
	
	public List<Object> getArguValues(String key)
	{
		Argument argu = argsSet.get(key);
		if (argu != null) {
			return argu.getValues();
		}		
		return null;
	}
	
	public List<String> getArguStrValues(String key)
	{
		Argument argu = argsSet.get(key);
		if (argu != null) {
			return argu.getStringValues();
		}		
		return null;
	}

	/**
	 * BD : Check if specific argument is set. If the argument is given from
	 * console, return true. Vice versa.
	 * 
	 * @param key
	 *            : argument key
	 * @return
	 */
	public boolean isSet(String key) {
		Argument argu = argsSet.get(key);
		if (argu != null) {
			return argu.isSet();
		}
		return false;
	}

	/**
	 * BD : Check if the given argument is empty. If empty, return true. Vice
	 * versa.
	 * 
	 * @return boolean
	 */
	public boolean isEmpty() {
		return empty;
	}

	public static void main(String args[]) {
		/* The first mark of argument will be the unique key for searching */
		/* Step1 : Define argument meaning */
		String fargs[] = { "-s", "single value", "-a=test", "-b", "-m", "value1", "value2" , "value3", "-c" };
		HashMap<String, Object> defineOfArgu = new HashMap<String, Object>();
		defineOfArgu.put("-a,--auto", "Auto Setup.");
		defineOfArgu.put("-b,--block", "Block Until...");
		defineOfArgu.put("-d,-D,--decode", "Decode");
		defineOfArgu.put("-c,-C,--Check,-3", "Check Mail...");
		defineOfArgu.put("-e,-E,--eg", "Show Example");
		defineOfArgu.put("-m,--multiple", new ArguConfig("Multiple argument(s)", EArguQuantity.MULTIPLE));
		defineOfArgu.put("-s,--single", new ArguConfig("single", EArguQuantity.SINGLE));

		/*
		 * Step2 : Pass argument defination and actualy argu read from console
		 * into Class:ArguParser
		 */
		ArguParser parser = new ArguParser(defineOfArgu, fargs);

		/* Step3 : Fetch argument and analysing */
		if(parser.isSuccessive)
		{
			ArrayList<Argument> argSet = parser.getSettingArgu();
			for (Argument a : argSet) {
				System.out.print("You have give " + a.getKey() + " which means: " + a.getDescript());
				if (!a.getValue().isEmpty()) {
					System.out.println(" with value=" + a.getValue());
				} else if(a.getValues().size()>0){
					List<Object> vals = a.getValues();
					System.out.printf(" with value=%s", vals.get(0));
					for(int i=1; i<vals.size(); i++) System.out.printf(", %s", vals.get(i));
					System.out.println();
					
				} else {
					System.out.println();
				}
			}
		}
		System.out.println();
		parser.showArgus();
	}
}
