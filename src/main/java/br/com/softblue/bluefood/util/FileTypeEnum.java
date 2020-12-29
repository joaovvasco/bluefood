package br.com.softblue.bluefood.util;

import java.util.Arrays;

public enum FileTypeEnum {
	PNG("image/png","png"),
	JPG("image/jpeg", "jpg");
	
	
	private String mimetype;
	
	private String extension;

	private FileTypeEnum(String mimetype, String extension) {
		this.mimetype = mimetype;
		this.extension = extension;
	}

	public String getMimeType() {
		return mimetype;
	}

	public String getExtension() {
		return extension;
	}
	
	public boolean sameOf(String mimeType) {
		return this.mimetype.equalsIgnoreCase(mimetype);
	}
	
	public static FileTypeEnum of(String mimetype) {
		
		return Arrays.asList(values())
			.stream()
			.filter(p -> p.sameOf(mimetype))
			.findFirst()
			.orElse(null);
	}
}
