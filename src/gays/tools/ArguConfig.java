package gays.tools;

import gays.tools.enums.EArguQuantity;

public class ArguConfig {
	public ArguConfig(String desc, EArguQuantity quant) {this.description=desc; this.quantity=quant;}
	public ArguConfig(String desc, EArguQuantity quant, String dfv){this(desc, quant); this.defaultVal = dfv;}
	
	public EArguQuantity 	quantity = EArguQuantity.QUESTION;
	public String			description = "";
	public String			defaultVal = null;
}
