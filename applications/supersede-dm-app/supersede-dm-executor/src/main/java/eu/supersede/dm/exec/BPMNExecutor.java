package eu.supersede.dm.exec;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import eu.supersede.dm.DMTask;

public class BPMNExecutor {
	
	protected ProcessEngine			processEngine;
	
	protected RuntimeService		runtimeService;
	protected RepositoryService		repositoryService;
	protected TaskService			taskService;
	
	public BPMNExecutor() {
		processEngine = ProcessEngines.getDefaultProcessEngine();
		runtimeService = processEngine.getRuntimeService();
		repositoryService = processEngine.getRepositoryService();
		taskService = processEngine.getTaskService();
	}
	
	public void loadBPMN( String filename ) {
		repositoryService.createDeployment()
		.addInputStream( filename, BPMNExecutor.class.getResourceAsStream( "supersedeAHPDM.bpmn20.xml" ) )
				  .addClasspathResource( filename )
				  .deploy();
	}
	
	public String startBPMN( String name ) {
		ProcessInstance pi = runtimeService.startProcessInstanceByKey( name );
		return pi.getProcessInstanceId();
	}
	
	public List<DMTask> getActiveTasks( String pid ) {
		List<DMTask> dmtasks = new ArrayList<>();
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(pid).active().list();;
		for( Task t : tasks ) {
			DMTask dmtask = new DMTask( t.getTaskDefinitionKey() );
			dmtask.setUserData( "executionId", t.getId() );
			dmtasks.add( dmtask );
		}
		return dmtasks;
	}
	
	public void completeTask( String pid, DMTask task ) {
		ProcessInstance pi = getProcessInstance( pid );
		if( pi == null ) {
			return;
		}
		taskService.complete( task.getUserData( "executionId", "" ) );
	}
	
	ProcessInstance getProcessInstance( String pid ) {
		List<ProcessInstance> plist = runtimeService.createProcessInstanceQuery().processInstanceId( pid ).list();
		if( plist == null ) {
			return null;
		}
		if( plist.size() < 1 ) {
			return null;
		}
		return plist.get( 0 );
	}
	
	public boolean isProcessComplete( String pid ) {
		List<ProcessInstance> plist = runtimeService.createProcessInstanceQuery().processInstanceId( pid ).list();
		if( plist == null ) {
			return true;
		}
		if( plist.size() < 1 ) {
			return true;
		}
		ProcessInstance proc = plist.get( 0 );
		return proc.isEnded();
	}

}
