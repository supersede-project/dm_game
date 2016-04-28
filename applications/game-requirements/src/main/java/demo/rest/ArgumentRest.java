package demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import demo.jpa.ArgumentsJpa;
import demo.model.Argument;

@RestController
@RequestMapping("/argument")
public class ArgumentRest {
	
	@Autowired
    private ArgumentsJpa arguments;
	
	// get all the arguments
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Argument> getArguments() {
		return arguments.findAll();
	}
	
	// get number of arguments
	@RequestMapping("/count")
	public Long count() {
		return arguments.count();
	}
	
	// post for the creation of a argument
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createArgument(@RequestBody Argument argument) {
		
		argument = arguments.save(argument);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(argument.getArgumentId()).toUri());
		return new ResponseEntity<>(argument, httpHeaders, HttpStatus.CREATED);
	}
	
	// post for the creation of a argument
	@RequestMapping(value = "/judge", method = RequestMethod.POST)
	public ResponseEntity<?> createJudgeArgument(@RequestBody Argument argument) {
		
		argument = arguments.save(argument);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(argument.getArgumentId()).toUri());
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
	}	
}