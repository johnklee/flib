package gays.tools;

import gays.tools.enums.EArguQuantity;

import java.util.*;

public class Argument {
	private ArrayList<String> markSign;
	private String descript;
	private String key;
    private String value="";
    private List<Object> values = new ArrayList<Object>();
	private boolean set = false;
	private ArguConfig config=null;
	public EArguQuantity quantity = EArguQuantity.QUESTION;
        
	public Argument(String m, ArguConfig config) {
		markSign = new ArrayList<String>();
		String[] ms = m.split(",");
		key = ms[0];
		for (String s : ms) {
			markSign.add(s);
		}
		quantity = config.quantity;
		descript = config.description;
		this.config = config;
	}
	
	public boolean isMine(String arg){return markSign.contains(arg);}
	
	public Argument(String m, String des) {
		descript = des;
		markSign = new ArrayList<String>();
		String[] ms = m.split(",");
		key = ms[0];
		for (String s : ms) {
			markSign.add(s);
		}
	}
	
	public void setOn(){
		set = true;
	}
	
	public boolean parseArgu(List<String> argList) {
		for (int i = 0; i < argList.size(); i++) {
			String arg = argList.get(i);
			switch (quantity) {
			case MULTIPLE:				
				if (arg.contains("=")) {
					String[] argItems = arg.split("=");
					if (markSign.contains(argItems[0].trim())) {
						values.add(argItems[1].trim());
						setOn();
						argList.remove(i);
						while (i<argList.size() && !argList.get(i).startsWith("-")) {
							values.add(argList.get(i));
							argList.remove(i);
						}
						return true;
					}
				} else {
					if (markSign.contains(arg)) {
						setOn();
						argList.remove(i);
						while (i<argList.size() && !argList.get(i).startsWith("-")) {
							values.add(argList.get(i));
							argList.remove(i);
						}
						return true;
					}
				}

				break;
			case SINGLE:
				if (arg.contains("=")) {
					String[] argItems = arg.split("=");
					if (markSign.contains(argItems[0].trim())) {
						value = argItems[1].trim();
						setOn();
						argList.remove(i);
						if (i<argList.size() && !argList.get(i).startsWith("-")) {
							return false;
						}
						return true;
					}
				} else {
					if (markSign.contains(arg)) {
						setOn();
						argList.remove(i);
						if (i<argList.size() && !argList.get(i).startsWith("-")) {
							value = argList.get(i);
							argList.remove(i);
							return true;
						}
						return false;
					}
				}
				
				break;
			case SIGN:
				if (markSign.contains(arg)) {
					setOn();
					argList.remove(i);
					if (i<argList.size() && !argList.get(i).startsWith("-")) {
						return false;
					}
					return true;
				}
				break;
			case QUESTION:
				if (arg.contains("=")) {
					String[] argItems = arg.split("=");
					if (markSign.contains(argItems[0].trim())) {
						value = argItems[1].trim();
						setOn();
						argList.remove(i);
						if (i<argList.size() && !argList.get(i).startsWith("-")) {
							return false;
						}
						return true;
					}
				} else {
					if (markSign.contains(arg)) {
						setOn();
						argList.remove(i);
						if (i<argList.size() && !argList.get(i).startsWith("-")) {
							value = argList.get(i);
							argList.remove(i);
							if(i>=argList.size() || argList.get(i).startsWith("-")) return true;
							
						} else {
							return true;
						}						
						return false;
					}
				}
				
				break;
			case QUESTIONS:
				if (arg.contains("=")) {
					String[] argItems = arg.split("=");
					if (markSign.contains(argItems[0].trim())) {
						values.add(argItems[1].trim());
						setOn();
						argList.remove(i);
						while (i<argList.size() && !argList.get(i).startsWith("-")) {
							values.add(argList.get(i));
							argList.remove(i);
						}
						return true;
					}
				} else {
					if (markSign.contains(arg)) {
						setOn();
						argList.remove(i);
						while (i<argList.size() && !argList.get(i).startsWith("-")) {
							values.add(argList.get(i));
							argList.remove(i);
						}
						return true;
					}
				}
				break;
			}
		}
		return true;
	}
	
	public boolean checkArgu(String arg){
		for (String mark : markSign) {
			if (arg.contains("=")) {
				String[] argItems = arg.split("=");
				// System.out.println(argItems[0].trim()+" ~ "+mark.trim());
				if (argItems[0].trim().equals(mark.trim())) {
					setOn();
					value = argItems[1];
					return true;
				}
			} else {
				if (mark.equals(arg.trim())) {
					setOn();
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList<String> getMarkSign() {
		return markSign;
	}

	public void setMarkSign(ArrayList<String> markSign) {
		this.markSign = markSign;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public boolean isSet() {
		return set;
	}

	public String getKey() {
		return key;
	}

	public List<Integer> getIntValues()
	{
		List<Integer> intValues = new LinkedList<Integer>();
		for(Object val:values) intValues.add(Integer.valueOf(String.valueOf(val)));		
		return intValues;
	}
	
	public List<String> getStringValues()
	{
		List<String> strValues = new LinkedList<String>();
		for(Object val:values) strValues.add(String.valueOf(val));		
		return strValues;
	}
	
	public List<Object> getValues()
	{
		return values;
	}
	
    /**
     * @return the value
     */
    public String getValue() {
        if(value.isEmpty()) return String.valueOf(config.defaultVal);
        else return value;
    }
    
    public int getIntValue()
    {
    	if(value.isEmpty()) return Integer.valueOf(config.defaultVal);
        else return Integer.valueOf(value);    	
    }
    
    @Override
    public String toString()
    {
    	StringBuffer strBuf = new StringBuffer("Argu(");
    	strBuf.append(String.format("%s:%s", key, descript));
    	if(config.defaultVal!=null) strBuf.append(String.format(",default=%s", config.defaultVal));
    	strBuf.append(")");
    	return strBuf.toString();
    }
}
