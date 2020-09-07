package org.bunge.showdown;

public class UnescapeTest {

	public static void main(String[] args) {
		String aRod2001 = "	397 	1st 	Alex Rodriguez\r\n" + 
				"Mariners 	550 	'01 	10 	B 	SS+4\r\n" + 
				"	R 		\r\n" + 
				"SO 	GB 	FB 	W 	S 	S+ 	DB 	TR 	HR\r\n" + 
				"1-2 	3- 	- 	4-10 	11-14 	15- 	16-17 	- 	18-20";
		String delino2001 = "	51 	1st 	Delino Deshields\r\n" + 
				"Orioles 	320 	'01 	9 	A 	2B+2\r\n" + 
				"LF-RF+1 	L 		\r\n" + 
				"SO 	GB 	FB 	W 	S 	S+ 	DB 	TR 	HR\r\n" + 
				"1- 	2-4 	5- 	6-9 	10-14 	15-16 	17-19 	- 	20+";
		String aRod2004 = "	333 	UL 	Alex Rodriguez\r\n" + 
				"Rangers 	620 	'04 	13 	16 	SS+4\r\n" + 
				"	R 	V HR G S 	\r\n" + 
				"SO 	GB 	FB 	W 	S 	S+ 	DB 	TR 	HR\r\n" + 
				"1-2 	3-4 	5-6 	7-11 	12-15 	16- 	17- 	- 	18+";
		String o4Pitchers = "6 	UL 	Ramon Ortiz\r\n" + 
				"Angels 	40 	'04 	2 	6 	Starter\r\n" + 
				"	R 		\r\n" + 
				"PU 	SO 	GB 	FB 	W 	S 	S+ 	DB 	TR 	HR\r\n" + 
				"1-3 	4-6 	7-12 	13-16 	17- 	18-19 	- 	20-23 	- 	24+\r\n" + 
				"	9 	UL 	Scot Shields\r\n" + 
				"Angels 	320 	'04 	4 	5 	Starter\r\n" + 
				"	R 		\r\n" + 
				"PU 	SO 	GB 	FB 	W 	S 	S+ 	DB 	TR 	HR\r\n" + 
				"1-3 	4-7 	8-14 	15-16 	17- 	18-19 	- 	20-25 	- 	26+";
		System.out.println(unEscapeString(aRod2001));
		System.out.println(unEscapeString(delino2001));
		System.out.println(unEscapeString(aRod2004));
		System.out.println();
		System.out.println(unEscapeString(o4Pitchers));
		System.out.println(unEscapeString(" 	1 	UL 	Garrett Anderson\r\n" + 
				"Angels 	530 	'04 	11 	11 	LF-RF+2\r\n" + 
				"	L 	S 	\r\n" + 
				"SO 	GB 	FB 	W 	S 	S+ 	DB 	TR 	HR\r\n" + 
				"1- 	2-4 	5-6 	7-8 	9-15 	- 	16-18 	- 	19+"));
	}
	
	public static String unEscapeString(String s){
	    StringBuilder sb = new StringBuilder();
	    for (int i=0; i<s.length(); i++)
	        switch (s.charAt(i)){
	            case '\n': sb.append("\\n"); break;
	            case '\t': sb.append("\\t"); break;
	            // ... rest of escape characters
	            default: sb.append(s.charAt(i));
	        }
	    return sb.toString();
	}

}
