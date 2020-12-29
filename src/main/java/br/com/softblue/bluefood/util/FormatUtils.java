package br.com.softblue.bluefood.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

	private static Locale LOCALE_BRAZIL = new Locale("pt","BR");
	
	public static NumberFormat newCurrencyFormat() {
		NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_BRAZIL);
		
		return  nf;
	}
	
	public static String convert(BigDecimal number) {
		
		if(number == null)
			return null;
		
		NumberFormat nf = newCurrencyFormat();
		
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		nf.setGroupingUsed(false);
		
		return nf.format(number);
	}
	
}
