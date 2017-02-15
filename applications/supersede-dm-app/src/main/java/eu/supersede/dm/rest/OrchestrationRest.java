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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMGuiManager;
import eu.supersede.dm.DMLibrary;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMSolution;
import eu.supersede.dm.IDMGui;
import eu.supersede.dm.OrchestratorUtil;
import eu.supersede.dm.ProcessManager;
import eu.supersede.dm.SimulatedProcess;

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

	public static class ActivityEntry {
		
		String methodName;
		String entryUrl;
		
		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public void setEntryUrl(String entryUrl) {
			this.entryUrl = entryUrl;
		}
		
		public String getEntryUrl() {
			return this.entryUrl;
		}
		
	}
	
	@RequestMapping(value = "/available_activities", method = RequestMethod.GET)
	public List<ActivityEntry> getNextActivities( Long procId ) {
		
		return findNextActivities( new SimulatedProcess( procId ) );
		
	}
	
	public List<ActivityEntry> findNextActivities( ProcessManager status ) {
		List<ActivityEntry> list = new ArrayList<ActivityEntry>();
		
		for( DMMethod m : DMLibrary.get().methods() ) {
			boolean match = true;
			for( DMCondition cond : m.preconditions() ) {
				if( !cond.isTrue( status ) ) {
					match = false;
				}
			}
			if( match == true ) {
				// TODO: configure the activity entry
				IDMGui gui = DMGuiManager.get().getGui( m.getName() );
				if( gui == null ) {
					continue;
				}
				ActivityEntry ae = new ActivityEntry();
				ae.setMethodName( m.getName() );
				ae.setEntryUrl( gui.getEntryUrl() );
				list.add( ae );
			}
		}
		
		return list;
	}
	
}
