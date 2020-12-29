package br.com.softblue.bluefood.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

	public static <T> List<T> of(@SuppressWarnings("unchecked") T... itens){
		
		if(itens == null || itens.length == 0)
			return new ArrayList<T>();
		
		List<T> list = new ArrayList<>();
		
		for(T t : itens)
			list.add(t);
		
		return list;
	}

}
