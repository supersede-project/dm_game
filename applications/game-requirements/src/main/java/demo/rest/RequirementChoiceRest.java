package demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.RequirementsChoicesJpa;
import demo.model.RequirementChoice;

@RestController
@RequestMapping("/requirementchoice")
public class RequirementChoiceRest {

	@Autowired
    private RequirementsChoicesJpa requirementsChoices;
	
	// all the RequirementChoice
	@RequestMapping("")
	public List<RequirementChoice> getRequirementsChoices() {
		return requirementsChoices.findAll();
	}
}
