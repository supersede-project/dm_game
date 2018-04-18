package eu.supersede.orch.kb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Command {
	
	public static final int TYPE_CREATE = 1;
	public static final int TYPE_READ = 0;
	public static final int TYPE_UPDATE = 2;
	public static final int TYPE_DELETE = 3;
	
	static final String TYPENAMES[] = { "READ", "CREATE", "UPDATE", "DELETE" };
	static final int REVERSE_CMDS[] = { TYPE_READ, TYPE_DELETE, TYPE_UPDATE, TYPE_CREATE };
	
	static final Map<String,Integer> TYPEMAP = mktyemap();
	
	
	int					type;
	DomPath				domPath;
	Map<String,String>	values = new HashMap<String,String>();
	
	Command				reverse;
	
	
	@SuppressWarnings("unused")
	private Command()
	{
		// To avoid restoreCmd to be recursively instantiated
	}
	
	private static Map<String, Integer> mktyemap() {
		Map<String,Integer> m = new HashMap<String,Integer>();
		m.put( TYPENAMES[TYPE_READ], TYPE_READ );
		m.put( TYPENAMES[TYPE_CREATE], TYPE_CREATE );
		m.put( TYPENAMES[TYPE_UPDATE], TYPE_UPDATE );
		m.put( TYPENAMES[TYPE_DELETE], TYPE_DELETE );
		return m;
	}
	
	public Command( String string ) throws Exception
	{
		List<String> parts = split( string, " ", "\"" );
		
		type = TYPEMAP.get( parts.get(0).toUpperCase() );
		
		for( int i = 1; i < parts.size(); i++ )
		{
			if( "INTO".equalsIgnoreCase( parts.get(i) ) ) {
				domPath = new DomPath( parts.get(i+1) );
				i++;
			}
			if( "NODE".equalsIgnoreCase( parts.get(i) ) ) {
				domPath = new DomPath( domPath, parts.get(i+1) );
				i++;
			}
			if( "VALUES".equalsIgnoreCase( parts.get(i) ) ) {
				List<String> pairs = split( parts.get(i+1), ";", "\"" );
				for( String pair : pairs ) {
					String[] sides = pair.split( "[=]" );
					sides[1].replaceAll( "\"", " " );
					values.put( sides[0], sides[1].trim() );
				}
				i++;
			}
		}
		
		
//		String str = "CREATE INTO /diagrams/diagram1/indicators/ NODE indicator VALUES class=Node;id=i103a;x=10;y=20;width=50;height=20;caption=\"i103a Number of component licenses (Fossology)\"";
//		
//		List<String> list = new ArrayList<String>();
//		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(str);
//		while (m.find())
//			list.add(m.group(1)); // Add .replace("\"", "") to remove surrounding quotes.
//		
//		
//		
//		List<String> matchList = new ArrayList<String>();
//		Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
//		Matcher regexMatcher = regex.matcher(string);
//		while (regexMatcher.find()) {
//		    if (regexMatcher.group(1) != null) {
//		        // Add double-quoted string without the quotes
//		        matchList.add(regexMatcher.group(1));
//		    } else if (regexMatcher.group(2) != null) {
//		        // Add single-quoted string without the quotes
//		        matchList.add(regexMatcher.group(2));
//		    } else {
//		        // Add unquoted word
//		        matchList.add(regexMatcher.group());
//		    }
//		}
//		
//		for( String s : matchList )
//			System.out.println( "-> " + s );
//		
////		System.exit( 0 );
//		
//		String[] parts = string.split( "([a-z])(?!.*\1)(?<!\1.{0,10})([a-z])(?!.*\2)(?<!\2.{0,10})(.)(\3)(.)(\5)" );
//		
//		System.out.println( "==============" );
//		for( String s : parts )
//			System.out.println( s );
//		
//		System.exit( 0 );
		
//		type = TYPEMAP.get( parts[0] );
//		
//		for( int i = 1; i < parts.length; i++ )
//		{
//			if( parts[i].compareTo( "INTO" ) == 0 ) {
//				domPath = new DomPath( parts[i+1] );
//				i++;
//			}
//			else if( parts[i].compareTo( "NODE" ) == 0 ) {
//				domPath = new DomPath( domPath, parts[i+1] );
//				i++;
//			}
//			else if( parts[i].compareTo( "VALUES" ) == 0 ) {
//				String[] pairs = parts[i+1].split( "[;]" );
//				for( String pair : pairs ) {
//					String[] sides = pair.split( "[=]" );
//					values.put( sides[0], sides[1] );
//				}
//				i++;
//			}
//		}
	}
	
	public Command( int type, DomPath dp, Map<String,String> attrs )
	{
		this.type = type;
		this.domPath = dp;
		if( attrs != null )
			this.values = attrs;
	}
	
	List<String> split( String content, String sep, String quote ) {
		List<String> list = new ArrayList<String>();
		String quotedTerm = quote + "[^" + quote + "]*" + quote;
		String unquotedTerm = "[^" + sep + quote + "]";
		String term = "(?:(?:" + quotedTerm + ")|(?:" + unquotedTerm + "))+";
		String regex = "(" + term + ")";
		Matcher matcher = Pattern.compile(regex).matcher(content);
		while (matcher.find()) {
			list.add( matcher.group(1) );
//		    System.out.println("- " + matcher.group(1));
		}
		return list;
	}
	
	public int getType() {
		return type;
	}

	public String getPath() {
		return domPath.getParentPath();
	}

	public String getTag()
	{
		return domPath.getName();
	}
	
	public String getQualifier()
	{
		return domPath.getQuantifiedName();
	}
	
	public void addValue( String key, String value )
	{
		this.values.put( key, value );
	}
	
	public String getValue( String key )
	{
		return values.get( key );
	}
	
	public String getValue( String key, String def )
	{
		String ret = values.get( key );
		
		if( ret == null ) ret = def;
		
		return ret;
	}
	
	public String toString()
	{
		String ret = "";
		
		ret += TYPENAMES[type];
		ret += " INTO ";
		ret += getPath();
		
//		if( getValue() != null )
		{
			ret += " NODE ";
			ret += getTag();
			if( domPath.getLeaf().getIndex() > 0 ) ret += "[" + domPath.getLeaf().getIndex() + "]";
		}
		
		if( values.size() > 0 )
		{
			ret += " VALUES ";
			for( String k : values.keySet() )
				ret += k + "=" + values.get(k) + ";";
		}
		
		return ret;
	}

	public void setReverseCmd( Command  cmd )
	{
		this.reverse = cmd;
	}

	public DomPath getDomPath()
	{
		return domPath;
	}
}
