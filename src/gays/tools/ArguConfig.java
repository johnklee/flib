package gays.tools;

import gays.tools.enums.EArguQuantity;
import gays.tools.enums.EArguRestrict;

public class ArguConfig {
	public ArguConfig(String desc, EArguQuantity quant) {this.description=desc; this.quantity=quant;}
	public ArguConfig(String desc, EArguQuantity quant, EArguRestrict res) {this.description=desc; this.quantity=quant;this.restrict = res;}
	public ArguConfig(String desc, EArguQuantity quant, String dfv){this(desc, quant); this.defaultVal = dfv;}
	
	
	public EArguQuantity 	quantity = EArguQuantity.QUESTION;
	public EArguRestrict	restrict = EArguRestrict.Optional;
	public String			description = "";
	public String			defaultVal = null;
}
