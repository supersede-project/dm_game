//package eu.supersede.dm;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class DMGuiManager {
//	
//	private static DMGuiManager instance = new DMGuiManager();
//	
//	public static DMGuiManager get() {
//		return instance;
//	}
//	
//	Map<String,IDMGui> guiMap = new HashMap<>();
//	
//	public void registerGui( String methodName, IDMGui gui ) {
//		this.guiMap.put( methodName, gui );
//	}
//	
//	public IDMGui getGui( String methodName ) {
//		return this.guiMap.get( methodName );
//	}
//	
//}
