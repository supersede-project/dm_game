//package eu.supersede.dm.methods;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import org.reflections.Reflections;
//
//import eu.supersede.dm.DMMethod;
//import eu.supersede.dm.IDMPlanner;
//import eu.supersede.dm.planners.Ref;
//
//public class DMLibrary {
//
//	private static final DMLibrary instance = new DMLibrary();
//
//	Map<String,DMMethod> methods = new HashMap<>();
//	Set<IDMPlanner> planners = new HashSet<>();
//
//	private DMLibrary() {
//
//		{
//			Reflections reflections = new Reflections( DMLibrary.class.getPackage().getName() );
//			
//			Set<Class<? extends DMMethod>> subTypes = reflections.getSubTypesOf( DMMethod.class );
//
//			for( Class<? extends DMMethod> cls : subTypes ) {
//				try {
//					DMMethod method = (DMMethod)cls.newInstance();
//					methods.put( method.getName(), method );
//				}
//				catch( Exception ex ) {
//					ex.printStackTrace();
//				}
//			}
//		}
//		
//		{
//			Reflections reflections = new Reflections( Ref.class.getPackage().getName() );
//			
//			Set<Class<? extends IDMPlanner>> subTypes = reflections.getSubTypesOf( IDMPlanner.class );
//			
//			for( Class<? extends IDMPlanner> cls : subTypes ) {
//				try {
//					IDMPlanner planner = (IDMPlanner)cls.newInstance();
//					planners.add( planner );
//				}
//				catch( Exception ex ) {}
//			}
//		}
//		
//	}
//
//	public static DMLibrary get() {
//		return instance;
//	}
//
//	public Collection<DMMethod> methods() {
//		return this.methods.values();
//	}
//
//	public DMMethod getMethod( String methodName ) {
//		return methods.get( methodName );
//	}
//	
//	public void addMethod( DMMethod method ) {
//		this.methods.put( method.getName(), method );
//	}
//
//}
