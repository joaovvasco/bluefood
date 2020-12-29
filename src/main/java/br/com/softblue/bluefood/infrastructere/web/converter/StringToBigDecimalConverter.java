package br.com.softblue.bluefood.infrastructere.web.converter;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.softblue.bluefood.util.StringUtils;

@Component
public class StringToBigDecimalConverter implements Converter<String, BigDecimal>{

	@Override
	public BigDecimal convert(String source) {
		
		if(StringUtils.isBlank(source))
			return null;
		
		source = source.trim().replaceAll(",", ".");
		
		return BigDecimal.valueOf(Double.valueOf(source));
	}

	

}
