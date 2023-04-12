// Generated from crux\pt\Crux.g4 by ANTLR 4.7.2
package crux.pt;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CruxLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, SemiColon=4, Comma=5, Assign=6, LessThan=7, GreaterThan=8, 
		Equal=9, NotEqual=10, LessEqual=11, GreaterEqual=12, Div=13, Mul=14, Sub=15, 
		Add=16, CloseBracket=17, OpenBracket=18, CloseBrace=19, OpenBrace=20, 
		CloseParen=21, OpenParen=22, Integer=23, True=24, False=25, Identifier=26, 
		WhiteSpaces=27, Comment=28;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "SemiColon", "Comma", "Assign", "LessThan", "GreaterThan", 
			"Equal", "NotEqual", "LessEqual", "GreaterEqual", "Div", "Mul", "Sub", 
			"Add", "CloseBracket", "OpenBracket", "CloseBrace", "OpenBrace", "CloseParen", 
			"OpenParen", "Integer", "True", "False", "Identifier", "WhiteSpaces", 
			"Comment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'!'", "'&&'", "'||'", "';'", "','", "'='", "'<'", "'>'", "'=='", 
			"'!='", "'<='", "'>='", "'/'", "'*'", "'-'", "'+'", "']'", "'['", "'}'", 
			"'{'", "')'", "'('", null, "'true'", "'false'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, "SemiColon", "Comma", "Assign", "LessThan", "GreaterThan", 
			"Equal", "NotEqual", "LessEqual", "GreaterEqual", "Div", "Mul", "Sub", 
			"Add", "CloseBracket", "OpenBracket", "CloseBrace", "OpenBrace", "CloseParen", 
			"OpenParen", "Integer", "True", "False", "Identifier", "WhiteSpaces", 
			"Comment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public CruxLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Crux.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\36\u009b\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\3\3\3\3\3\3\4"+
		"\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13"+
		"\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21"+
		"\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30"+
		"\3\30\7\30q\n\30\f\30\16\30t\13\30\5\30v\n\30\3\31\3\31\3\31\3\31\3\31"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\7\33\u0085\n\33\f\33\16\33\u0088"+
		"\13\33\3\34\6\34\u008b\n\34\r\34\16\34\u008c\3\34\3\34\3\35\3\35\3\35"+
		"\3\35\7\35\u0095\n\35\f\35\16\35\u0098\13\35\3\35\3\35\2\2\36\3\3\5\4"+
		"\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36\3\2\b\3\2\63"+
		";\3\2\62;\4\2C\\c|\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\4\2\f\f\17\17\2\u009f"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\3;\3"+
		"\2\2\2\5=\3\2\2\2\7@\3\2\2\2\tC\3\2\2\2\13E\3\2\2\2\rG\3\2\2\2\17I\3\2"+
		"\2\2\21K\3\2\2\2\23M\3\2\2\2\25P\3\2\2\2\27S\3\2\2\2\31V\3\2\2\2\33Y\3"+
		"\2\2\2\35[\3\2\2\2\37]\3\2\2\2!_\3\2\2\2#a\3\2\2\2%c\3\2\2\2\'e\3\2\2"+
		"\2)g\3\2\2\2+i\3\2\2\2-k\3\2\2\2/u\3\2\2\2\61w\3\2\2\2\63|\3\2\2\2\65"+
		"\u0082\3\2\2\2\67\u008a\3\2\2\29\u0090\3\2\2\2;<\7#\2\2<\4\3\2\2\2=>\7"+
		"(\2\2>?\7(\2\2?\6\3\2\2\2@A\7~\2\2AB\7~\2\2B\b\3\2\2\2CD\7=\2\2D\n\3\2"+
		"\2\2EF\7.\2\2F\f\3\2\2\2GH\7?\2\2H\16\3\2\2\2IJ\7>\2\2J\20\3\2\2\2KL\7"+
		"@\2\2L\22\3\2\2\2MN\7?\2\2NO\7?\2\2O\24\3\2\2\2PQ\7#\2\2QR\7?\2\2R\26"+
		"\3\2\2\2ST\7>\2\2TU\7?\2\2U\30\3\2\2\2VW\7@\2\2WX\7?\2\2X\32\3\2\2\2Y"+
		"Z\7\61\2\2Z\34\3\2\2\2[\\\7,\2\2\\\36\3\2\2\2]^\7/\2\2^ \3\2\2\2_`\7-"+
		"\2\2`\"\3\2\2\2ab\7_\2\2b$\3\2\2\2cd\7]\2\2d&\3\2\2\2ef\7\177\2\2f(\3"+
		"\2\2\2gh\7}\2\2h*\3\2\2\2ij\7+\2\2j,\3\2\2\2kl\7*\2\2l.\3\2\2\2mv\7\62"+
		"\2\2nr\t\2\2\2oq\t\3\2\2po\3\2\2\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2\2sv\3\2"+
		"\2\2tr\3\2\2\2um\3\2\2\2un\3\2\2\2v\60\3\2\2\2wx\7v\2\2xy\7t\2\2yz\7w"+
		"\2\2z{\7g\2\2{\62\3\2\2\2|}\7h\2\2}~\7c\2\2~\177\7n\2\2\177\u0080\7u\2"+
		"\2\u0080\u0081\7g\2\2\u0081\64\3\2\2\2\u0082\u0086\t\4\2\2\u0083\u0085"+
		"\t\5\2\2\u0084\u0083\3\2\2\2\u0085\u0088\3\2\2\2\u0086\u0084\3\2\2\2\u0086"+
		"\u0087\3\2\2\2\u0087\66\3\2\2\2\u0088\u0086\3\2\2\2\u0089\u008b\t\6\2"+
		"\2\u008a\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d"+
		"\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008f\b\34\2\2\u008f8\3\2\2\2\u0090"+
		"\u0091\7\61\2\2\u0091\u0092\7\61\2\2\u0092\u0096\3\2\2\2\u0093\u0095\n"+
		"\7\2\2\u0094\u0093\3\2\2\2\u0095\u0098\3\2\2\2\u0096\u0094\3\2\2\2\u0096"+
		"\u0097\3\2\2\2\u0097\u0099\3\2\2\2\u0098\u0096\3\2\2\2\u0099\u009a\b\35"+
		"\2\2\u009a:\3\2\2\2\b\2ru\u0086\u008c\u0096\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}