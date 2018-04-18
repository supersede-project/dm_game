package eu.supersede.orch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.orch.MethodOption.CARDINALITY;
import eu.supersede.orch.kb.DOM;
import eu.supersede.orch.util.SimulatedText;

public class MethodDefinition {
	
	private String						name;
	
	List<Condition>						preconditions = new ArrayList<>();
	
	List<Condition>						postconditions = new ArrayList<>();

	private String						objective;
	
	private Map<String,MethodOption>	options = new HashMap<>();

	private Map<String,MethodValue>		inputs = new HashMap<>();
	
	private List<String>				inputList = new ArrayList<>();
	private List<String>				outputList = new ArrayList<>();
	
	private DOM							outputTemplate = new DOM();
	
	private OptimalityModel				optimalityModel;
	
	
	public MethodDefinition( String name ) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public boolean satisfiesPreconditions( MethodDefinition other ) {
		return true;
	}
	
	public MethodInstance createRandomInstance() {
		MethodInstance instance = new MethodInstance();
		instance.md = this;
		for( MethodOption opt : options.values() ) {
			switch( opt.getDatatype() ) {
			case BOOLEAN:
				if( opt.getCardinality() == CARDINALITY.SINGLE ) {
					instance.setOption( opt.getName(), "" + (R.get().getRandom().nextInt( 2 ) > 0 ? true : false) );
				}
				else {
					for( int i = 0; i < R.get().getRandom().nextInt( 10 ); i++ ) {
						// TODO: add the value
					}
				}
				break;
			case NUMBER:
				if( opt.getCardinality() == CARDINALITY.SINGLE ) {
					instance.setOption( opt.getName(), "" + R.get().getRandom().nextInt() );
				}
				else {
					for( int i = 0; i < R.get().getRandom().nextInt( 10 ); i++ ) {
						// TODO: add the value
					}
				}
				break;
			case STRING:
				if( opt.getCardinality() == CARDINALITY.SINGLE ) {
					instance.setOption( opt.getName(), new SimulatedText( 10 ).toString() );
				}
				else {
					for( int i = 0; i < R.get().getRandom().nextInt( 10 ); i++ ) {
						// TODO: add the value
					}
				}
				break;
			}
//			instance.setOption( opt.getName(),  )
		}
		return instance;
	}

	public void setObjective( String name ) {
		this.objective = name;
	}
	
	public String getObjective() {
		return this.objective;
	}
	
	public void addOption( MethodOption option ) {
		options.put( option.getName(), option );
	}
	
	public Map<String,MethodOption> getOptions() {
		return options;
	}

	public void setOptions(Map<String,MethodOption> options) {
		this.options = options;
	}
	
	public MethodOption getOption( String name ) {
		return this.options.get( name );
	}

	public void addInputValue( MethodValue val ) {
		this.inputs.put( val.getName(), val );
		this.inputList.add( val.getName() );
	}
	
	public void addOutputValue( String name ) {
		this.outputList.add( name );
	}
	
	public Collection<String> getInputValues() {
		return this.inputs.keySet();
	}
	
	public MethodValue getInputValue( String valueName ) {
		return inputs.get( valueName );
	}
	
	public void setOptimalityModel(OptimalityModel model) {
		this.optimalityModel = model;
	}
	
	public OptimalityModel getOptimalityModel() {
		return this.optimalityModel;
	}
	
	public DOM getOutputTemplate() {
		return this.outputTemplate;
	}

	public List<String> getOutputValues() {
		return this.outputList;
	}
	
}
