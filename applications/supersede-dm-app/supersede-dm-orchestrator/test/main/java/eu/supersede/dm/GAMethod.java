//package eu.supersede.dm;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class GAMethod implements DMMethod {
//	
//	static List<DMRoleSpec> roles = new ArrayList<>();
//	
//	static {
//		roles.add( new DMRoleSpec( new DMRole( "Supervisor" ), 1, 1 ) );
//		roles.add( new DMRoleSpec( new DMRole( "Player" ), 1, -1 ) );
//		roles.add( new DMRoleSpec( new DMRole( "Negotiator" ), 0, 1 ) );
//	}
//	
//	@Override
//	public String getName() {
//		return "Genetic Algorithm based prioritization";
//	}
//
//	@Override
//	public DMObjective getObjective() {
//		return DMObjective.PrioritizeRequirements;
//	}
//
//	@Override
//	public List<DMOption> getOptions() {
//		return new ArrayList<>();
//	}
//
//	@Override
//	public List<DMRoleSpec> getRoleList() {
//		return roles;
//	}
//
//	@Override
//	public List<DMCondition> preconditions() {
//		List<DMCondition> list = new ArrayList<DMCondition>();
//		list.add( new DMCondition() {
//			@Override
//			public boolean isTrue( DMStatus status ) {
//				if( status.getRequirementsCount() < 1 ) {
//					return false;
//				}
//				// TODO: check that requirements status if "confirmed"
//				return true;
//			}} );
//		return list;
//	}
//
//}
