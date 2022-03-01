package com.springboot.h2.ctrl;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.h2.model.Student;
import com.springboot.h2.serv.StudentService;

@RestController		// Useful to create the RESTful webservices.
public class StudentController {

	private final Logger log = LoggerFactory.getLogger(this.getClass()); 

	// @Autowired annotation provides the automatic dependency injection.
	@Autowired
	StudentService service;

	// Save student entity in the h2 database.
	// @PostMapping annotation handles the http post request matched with the given uri.
	// @RequestBody annotation binds the http request body to the domain object.
	// @Valid annotation validates a model after binding the user input to it.
	@PostMapping(value= "/student/save")
	public int save(final @RequestBody @Valid Student student) {
		log.info("Saving student details in the database.");
		service.save(student);
		return student.getId();
	}

	// Get all students from the h2 database.
	// @GetMapping annotation handles the http get request matched with the given uri.
	@GetMapping(value= "/student/getall", produces= "application/vnd.jcg.api.v1+json")
	public List<Student> getAll() {
		log.info("Getting student details from the database.");
		return service.getAll();
	}
}
