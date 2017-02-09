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

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.DMObjective;
import eu.supersede.gr.jpa.ProcessesJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.model.HProcess;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;

@RestController
@RequestMapping("processes")
public class ProcessRest
{
    @Autowired ProcessesJpa jpaProcesses;
    
    @Autowired RequirementsJpa jpaRequirements;

    @RequestMapping(value = "new", method = RequestMethod.POST)
    public Long newProcess()
    {
    	HProcess proc = new HProcess();
    	proc.setObjective( DMObjective.PrioritizeRequirements.name() );
    	proc.setStartTime( new Date( System.currentTimeMillis() ) );
    	jpaProcesses.save( proc );
    	return proc.getId();
    }
    
    public void addRequirements( Long procId, List<Long> reqList ) {
    	for( Long reqId : reqList ) {
    		Requirement r = jpaRequirements.findOne( reqId );
    		if( r == null ) continue;
    		if( r.getProcessId() != -1 ) continue;
    		r.setProcessId( procId );
    		jpaRequirements.save( r );
    	}
    }
    
    public void setRequirementsStatus( Long procId, List<Long> reqList, RequirementStatus status ) {
    	for( Long reqId : reqList ) {
    		Requirement r = jpaRequirements.findOne( reqId );
    		if( r == null ) continue;
    		RequirementStatus oldStatus = RequirementStatus.valueOf( r.getStatus() );
    		if( RequirementStatus.next( oldStatus ).contains( status ) ) {
    			r.setStatus( status.getValue() );
    			jpaRequirements.save( r );
    		}
    	}
    }
    
}
