/* Generated By:JavaCC: Do not edit this line. XPathGrammar.java */
package eu.supersede.orch.qpath;

public class XPathGrammar implements XPathGrammarConstants {

	final public XPath load() throws ParseException {
		XPath path = new XPath();
		path(path);
		return path;
	}

	final public void path(XPath path) throws ParseException {
		label_1:
			while (true) {
				XPart part = new XPart();
				part(part);
				path.add( part );
				switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
				case 21:
					;
					break;
				default:
					jj_la1[0] = jj_gen;
					break label_1;
				}
			}
	switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
	case 0:
		jj_consume_token(0);
		break;
	default:
		jj_la1[1] = jj_gen;
		;
	}
	}

	final public void root(XPath path) throws ParseException {
		XPart root = new XPart();
		node(root);
		label_2:
			while (true) {
				switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
				case 25:
					;
					break;
				default:
					jj_la1[2] = jj_gen;
					break label_2;
				}
				predicate(root);
			}
		path.setRoot( root );
	}

	final public void part(XPart part) throws ParseException {
		jj_consume_token(21);
		node(part);
		label_3:
			while (true) {
				switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
				case 25:
					;
					break;
				default:
					jj_la1[3] = jj_gen;
					break label_3;
				}
				predicate(part);
			}
	}

	final public void axes(XPart part) throws ParseException {
		jj_consume_token(NAME_TOKEN);
		jj_consume_token(22);
	}

	final public void node(XPart part) throws ParseException {
		switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		case 23:
			jj_consume_token(23);
			part.setNodeName( token.toString() );
			break;
		case 24:
			jj_consume_token(24);
			part.setNodeName( token.toString() );
			break;
		case NAME_TOKEN:
			jj_consume_token(NAME_TOKEN);
			part.setNodeName( token.toString() );
			break;
		default:
			jj_la1[4] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
	}

	final public void predicate(XPart part) throws ParseException {
		jj_consume_token(25);
		switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		case NUMBER:
			range(part);
			break;
		case 29:
			condition(part);
			label_4:
				while (true) {
					switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
					case 26:
						;
						break;
					default:
						jj_la1[5] = jj_gen;
						break label_4;
					}
					jj_consume_token(26);
					condition(part);
				}
			break;
		default:
			jj_la1[6] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		jj_consume_token(27);
	}

	final public void range(XPart part) throws ParseException {
		number(part);
		part.setFrom( Long.parseLong( token.toString() ) );
		switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		case 28:
			jj_consume_token(28);
			number(part);
			part.setTo( Long.parseLong( token.toString() ) );
			break;
		default:
			jj_la1[7] = jj_gen;
			;
		}
	}

	final public void number(XPart part) throws ParseException {
		jj_consume_token(NUMBER);
	}

	final public void condition(XPart part) throws ParseException {
		XCondition c = new XCondition();
		jj_consume_token(29);
		jj_consume_token(NAME_TOKEN);
		c.setTarget( token.toString() );
		jj_consume_token(COMPARATOR);
		c.setComparator( token.toString() );
		jj_consume_token(QUOTED_TEXT);
		c.setValue( token.toString() );
		part.addCondition( c );
	}

	/** Generated Token Manager. */
	public XPathGrammarTokenManager token_source;
	SimpleCharStream jj_input_stream;
	/** Current token. */
	public Token token;
	/** Next token. */
	public Token jj_nt;
	private int jj_ntk;
	private int jj_gen;
	final private int[] jj_la1 = new int[8];
	static private int[] jj_la1_0;
	static {
		jj_la1_init_0();
	}
	private static void jj_la1_init_0() {
		jj_la1_0 = new int[] {0x200000,0x1,0x2000000,0x2000000,0x1800100,0x4000000,0x20000040,0x10000000,};
	}

	/** Constructor with InputStream. */
	public XPathGrammar(java.io.InputStream stream) {
		this(stream, null);
	}
	/** Constructor with InputStream and supplied encoding */
	public XPathGrammar(java.io.InputStream stream, String encoding) {
		try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
		token_source = new XPathGrammarTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++) jj_la1[i] = -1;
	}

	/** Reinitialise. */
	public void ReInit(java.io.InputStream stream) {
		ReInit(stream, null);
	}
	/** Reinitialise. */
	public void ReInit(java.io.InputStream stream, String encoding) {
		try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++) jj_la1[i] = -1;
	}

	/** Constructor. */
	public XPathGrammar(java.io.Reader stream) {
		jj_input_stream = new SimpleCharStream(stream, 1, 1);
		token_source = new XPathGrammarTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++) jj_la1[i] = -1;
	}

	/** Reinitialise. */
	public void ReInit(java.io.Reader stream) {
		jj_input_stream.ReInit(stream, 1, 1);
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++) jj_la1[i] = -1;
	}

	/** Constructor with generated Token Manager. */
	public XPathGrammar(XPathGrammarTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++) jj_la1[i] = -1;
	}

	/** Reinitialise. */
	public void ReInit(XPathGrammarTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++) jj_la1[i] = -1;
	}

	private Token jj_consume_token(int kind) throws ParseException {
		Token oldToken;
		if ((oldToken = token).next != null) token = token.next;
		else token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		if (token.kind == kind) {
			jj_gen++;
			return token;
		}
		token = oldToken;
		jj_kind = kind;
		throw generateParseException();
	}


	/** Get the next Token. */
	final public Token getNextToken() {
		if (token.next != null) token = token.next;
		else token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		jj_gen++;
		return token;
	}

	/** Get the specific Token. */
	final public Token getToken(int index) {
		Token t = token;
		for (int i = 0; i < index; i++) {
			if (t.next != null) t = t.next;
			else t = t.next = token_source.getNextToken();
		}
		return t;
	}

	private int jj_ntk() {
		if ((jj_nt=token.next) == null)
			return (jj_ntk = (token.next=token_source.getNextToken()).kind);
		else
			return (jj_ntk = jj_nt.kind);
	}

	private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
	private int[] jj_expentry;
	private int jj_kind = -1;

	/** Generate ParseException. */
	public ParseException generateParseException() {
		jj_expentries.clear();
		boolean[] la1tokens = new boolean[30];
		if (jj_kind >= 0) {
			la1tokens[jj_kind] = true;
			jj_kind = -1;
		}
		for (int i = 0; i < 8; i++) {
			if (jj_la1[i] == jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((jj_la1_0[i] & (1<<j)) != 0) {
						la1tokens[j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 30; i++) {
			if (la1tokens[i]) {
				jj_expentry = new int[1];
				jj_expentry[0] = i;
				jj_expentries.add(jj_expentry);
			}
		}
		int[][] exptokseq = new int[jj_expentries.size()][];
		for (int i = 0; i < jj_expentries.size(); i++) {
			exptokseq[i] = jj_expentries.get(i);
		}
		return new ParseException(token, exptokseq, tokenImage);
	}

	/** Enable tracing. */
	final public void enable_tracing() {
	}

	/** Disable tracing. */
	final public void disable_tracing() {
	}

}
