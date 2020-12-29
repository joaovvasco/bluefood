package br.com.softblue.bluefood.infrastructere.web.validation;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import br.com.softblue.bluefood.util.FileTypeEnum;

public class UploadValidator implements ConstraintValidator<UploadConstraint, MultipartFile>{
	
	private List<FileTypeEnum> acceptedFileTypes;
	
	@Override
	public void initialize(UploadConstraint constraintAnnotation) {
		acceptedFileTypes = Arrays.asList(constraintAnnotation.acceptedTypes());
	}
	
	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
		if(multipartFile == null)
			return true;
		
		return acceptedFileTypes.stream().anyMatch(p -> p.sameOf(multipartFile.getContentType()));
	}

}
