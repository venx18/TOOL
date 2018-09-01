package com.javasampleapproach.uploadfile.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.javasampleapproach.uploadfile.storage.StorageService;

@RestController
@RequestMapping("/api")
public class RestUploadController {

	@Autowired
	StorageService storageService;
	
	List<String> files = new ArrayList<String>();

    // Multiple file upload
    @PostMapping("/uploadfile")
    public String uploadFileMulti(
            @RequestParam("uploadfile") MultipartFile file) throws Exception {

    	try {
			storageService.store(file);
			files.add(file.getOriginalFilename());
			return "You successfully uploaded - " + file.getOriginalFilename();
		} catch (Exception e) {
			throw new Exception("FAIL! Maybe You had uploaded the file before or the file's size > 500KB");
		}
    }
    
    @PostMapping("/convertXmlToJson")
    public ResponseEntity<Resource> convertXmltoJsonFileMulti(
            @RequestParam("uploadfile") MultipartFile file) throws Exception {
    	String data = "";
    	try {
    		
			//storageService.store(file);
			//files.add(file.getOriginalFilename());
			data = FileUtils.readFileToString(convert(file), "UTF-8");
			// Create a new XmlMapper to read XML tags
            XmlMapper xmlMapper = new XmlMapper();
            
            //Reading the XML
            JsonNode jsonNode = xmlMapper.readTree(data.getBytes());
            
            //Create a new ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            
            String value = objectMapper.writeValueAsString(jsonNode);
            
            System.out.println("*** Converting XML to JSON ***");
            System.out.println(value);
            System.out.println("You successfully uploaded - " + file.getOriginalFilename());
            //FileOutputStream outputStream = new FileOutputStream(file.getOriginalFilename()+".json");
           // byte[] strToBytes = value.getBytes();
           // outputStream.write(strToBytes);
         
           // outputStream.close();
            Resource filereturn = new ByteArrayResource(value.getBytes());
    		return ResponseEntity.ok()
    				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"response.json\"")
    				.body(filereturn);
			//return "You successfully uploaded - " + file.getOriginalFilename();
		} catch (Exception e) {
			throw new Exception("FAIL! Maybe You had uploaded the file before or the file's size > 500KB");
		}
    }
    
	@GetMapping("/getallfiles")
	public List<String> getListFiles() {
		List<String> lstFiles = new ArrayList<String>();
		
		try{
			lstFiles = files.stream()
					.map(fileName -> MvcUriComponentsBuilder
							.fromMethodName(RestUploadController.class, "getFile", fileName).build().toString())
					.collect(Collectors.toList());	
		}catch(Exception e){
			throw e;
		}
		
		return lstFiles;
	}
	
	

	@GetMapping("/files/{filename:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = storageService.loadFile(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	private File convert(MultipartFile file) throws IOException {
	    File convFile = new File(file.getOriginalFilename());
	    convFile.createNewFile();
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
	}

}
