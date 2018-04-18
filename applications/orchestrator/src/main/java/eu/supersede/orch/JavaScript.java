/*
   (C) Copyright 2013-2016 The RISCOSS Project Consortium
   
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

package eu.supersede.orch;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScript {
	private ScriptEngineManager		mgr			= new ScriptEngineManager();
	private ScriptEngine			engine		= mgr.getEngineByName("JavaScript");
	
	private static final JavaScript instance = new JavaScript();
	
	public static JavaScript get() {
		return instance;
	}

	public Object eval( String code ) throws ScriptException {
		return engine.eval( code );
	}
	
	public void reset() {
		engine.setBindings( engine.createBindings(), ScriptContext.ENGINE_SCOPE );
	}
	
	public void put( String key, Object object ) {
//		System.out.println( "JS: " + key + " = " + object );
		engine.put( key, object );
	}
	
}
