package br.com.softblue.bluefood.util;

import java.util.Collection;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class StringUtils {

	public static boolean isBlank(String str) {
		if (str == null)
			return true;

		return str.trim().length() == 0;
	}
	
	public static String encrypt(String rawString) {
		if(isBlank(rawString))
			return null;
		
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return encoder.encode(rawString);
	}
	
	public static String concatenate(Collection<String> c) {
		
		return c.stream()
				.reduce((anterior,posterior) -> anterior+", "+posterior)
				.get();
	}
}
