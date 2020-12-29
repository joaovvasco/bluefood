package br.com.softblue.bluefood.application;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.softblue.bluefood.util.IOUtils;

@Service
public class ImageService {

	@Value("${bluefood.files.logotipo}")
	private String logotiposDir;
	
	@Value("${bluefood.files.categoria}")
	private String categoriasDir;
	
	@Value("${bluefood.files.comida}")
	private String comidasDir;
	
	public void uploadLogotipo(MultipartFile multipartFile, String fileName) {
		try {
			IOUtils.copy(multipartFile.getInputStream(), fileName, logotiposDir);
		} catch (IOException e) {
			throw new ApplicationServiceException(e);
		}
	}
	
	public void uploadComidas(MultipartFile multipartFile, String fileName) {
		try {
			IOUtils.copy(multipartFile.getInputStream(), fileName, comidasDir);
		} catch (IOException e) {
			throw new ApplicationServiceException(e);
		}
	}
	
	public byte[] getBytes(String type, String imgName) {
		
		try {
			
			String dir = null;
			
			switch(type) {
				case "categorias":
					dir = categoriasDir;
					break;
				case "comidas":
					dir = comidasDir;
					break;
				case "logotipos":
					dir = logotiposDir;
					break;
				default:
					throw new ApplicationServiceException("Tipo de imagem inválida: "+type+System.lineSeparator());
			}
			
			
			return IOUtils.getBytes(dir, imgName);
		}catch(Exception e) {
			throw new ApplicationServiceException(e);
		}
	}
}
