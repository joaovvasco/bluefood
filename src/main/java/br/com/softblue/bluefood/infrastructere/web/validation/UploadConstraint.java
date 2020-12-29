package br.com.softblue.bluefood.infrastructere.web.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.softblue.bluefood.util.FileTypeEnum;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = UploadValidator.class)
public @interface UploadConstraint {

	String message() default "Arquivo inválido";
	
	FileTypeEnum[] acceptedTypes();
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
