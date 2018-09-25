package com.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/customers/{id}/photo")
public class CustomerProfilePhotoRestController {

	private File root;
	private final CustomerRepository customerRepository;
	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	public CustomerProfilePhotoRestController(
		   @Value("${upload.dir:${user.home}/images}") String uploadDir,
		   CustomerRepository customerRepository) {
		this.root = new File(uploadDir);
		this.customerRepository = customerRepository;
		Assert.isTrue(root.exists() || root.mkdirs(),String.format("The path '%s' must exist", root.getAbsolutePath()));
	}
	
	@GetMapping
	ResponseEntity<Resource> read(@PathVariable Long id) {
		return customerRepository.findById(id).map(c -> {
			File file = fileFor(c);
			Assert.isTrue(file.exists(),String.format("file-not-found %s", file.getAbsolutePath()));
			Resource fileSystemResource = new FileSystemResource(file);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(fileSystemResource);
		}).orElseThrow(() -> new CustomerNotFoundException(id));
	}
	
	private ResponseEntity<?> copy(Long id, MultipartFile file) {
		return customerRepository.findById(id).map(c -> {
     	   File fileForCustomer = fileFor(c);
     	   try (InputStream in = file.getInputStream(); OutputStream out = new FileOutputStream(fileForCustomer)) {
     		   FileCopyUtils.copy(in, out);
     	   } catch (IOException e) {
     		   throw new RuntimeException(e);
     	   }
     	   URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(id).toUri();
     	   log.info(String.format("upload finish /customers/%s/photo (%s)", id, location));
     	   return ResponseEntity.created(location).build();
		}).orElseThrow(() -> new CustomerNotFoundException(id));
	}
	
	@RequestMapping(method = {RequestMethod.POST,RequestMethod.PUT})
	Callable<ResponseEntity<?>> write(@PathVariable Long id, @RequestParam MultipartFile file) {
		log.info(String.format("upload-start /customers/%s/photo (%s bytes)", id, file.getSize()));
		return () -> copy(id,file);
	}

	private File fileFor(Customer person) {
		return new File(root,Long.toString(person.getId()));
	}
	
}
