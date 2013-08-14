package gays.tools.enums;

public enum EArguQuantity {
	MULTIPLE("MULTIPLE"), /*More than one, including one.*/ 
	SINGLE("SINGLE"), /*Only one and no empty*/ 
	SIGN("SIGN"), /*Empty. Just as sign*/
	QUESTION("QUESTION"), /*One or empty*/
	QUESTIONS("QUESTIONS"); /*More than one or empty*/
	
	private String msg;
	private EArguQuantity(String msg){this.msg = msg;}
	@Override
	public String toString(){return msg;}
}
