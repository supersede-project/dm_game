/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package eu.supersede.dm.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import eu.supersede.dm.DMSolution;
import eu.supersede.dm.OrchestratorUtil;

@RestController
@RequestMapping("/orchestration")
public class OrchestrationRest
{
	@Autowired
	private OrchestratorUtil orchestratorDemo;

	@RequestMapping(value = "/plan", method = RequestMethod.GET)
	public String doPlan(@RequestParam(defaultValue = "2") Integer accuracy)
	{
		DMSolution sol = orchestratorDemo.plan(accuracy);
		Gson gson = new Gson();
		String ret = gson.toJson(sol);
		System.out.println(ret);
		return ret;
	}

}
