package core.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import core.token.TokenType;
import core.token.Token;

public class Lexer {
    private final String input;
    private int pos = 0;

    public static final Map<String, TokenType> KEYWORDS = new TreeMap<>();
    public static final Map<String, TokenType> STAT_KEYWORDS = new TreeMap<>();
    public static final Map<String, Double> CONSTANTS = new TreeMap<>();

    static {
        KEYWORDS.put("sin", TokenType.SIN);
        KEYWORDS.put("cos", TokenType.COS);
        KEYWORDS.put("tan", TokenType.TAN);
        KEYWORDS.put("sqrt", TokenType.SQRT);
        KEYWORDS.put("log", TokenType.LOG);
        
        STAT_KEYWORDS.put("mean", TokenType.MEAN);
        STAT_KEYWORDS.put("median", TokenType.MEDIAN);
        STAT_KEYWORDS.put("mode", TokenType.MODE);
        STAT_KEYWORDS.put("pvar", TokenType.PVARIANCE);
        STAT_KEYWORDS.put("pstddev", TokenType.PSTD_DEV);
        STAT_KEYWORDS.put("svar", TokenType.SVARIANCE);
        STAT_KEYWORDS.put("sstddev", TokenType.SSTD_DEV);

        CONSTANTS.put("pi", Math.PI);
        CONSTANTS.put("e", Math.E);
    }

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char ch = peek();

            if (Character.isWhitespace(ch)) {
                pos++;
            } else if (isNumberStart(ch)) {
                tokens.add(readNumber());
            } else if (isIdentifierStart(ch)) {
                tokens.add(readIdentifier());
            } else if (isOperator(ch)) {
                tokens.add(readOperator(tokens));
            } else if (ch == '(' || ch == ')') {
                tokens.add(readParenthesis());
            } else {
                throw new IllegalArgumentException("Unknown character: " + ch + " at position " + pos);
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private char peek() {
        if (pos >= input.length()) return '\0';
        return input.charAt(pos);
    }

    private Token readNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(peek())) {
            sb.append(input.charAt(pos++));
        }

        if (pos < input.length() && peek() == '.') {
            sb.append(input.charAt(pos++));
            while (pos < input.length() && Character.isDigit(peek())) {
                sb.append(input.charAt(pos++));
            }
        }
        return new Token(TokenType.NUMBER, sb.toString());
    }

    private Token readIdentifier() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isLetter(peek())) {
            sb.append(input.charAt(pos++));
        }
        String name = sb.toString();

        if (KEYWORDS.containsKey(name)) {
            return new Token(KEYWORDS.get(name), name);
        }
        if (STAT_KEYWORDS.containsKey(name)) {
            return new Token(STAT_KEYWORDS.get(name), name);
        }
        if (CONSTANTS.containsKey(name)) {
            return new Token(TokenType.NUMBER, String.valueOf(CONSTANTS.get(name)));
        }
        return new Token(TokenType.IDENTIFIER, name);
    }

    private Token readOperator(List<Token> currentTokens) {
        char ch = peek();
        pos++;

        if (ch == '-') {
            if (currentTokens.isEmpty() || isPreviousOperator(currentTokens)) {
                return new Token(TokenType.UNARY_MINUS, "-");
            }
            return new Token(TokenType.MINUS, "-");
        }

        return switch (ch) {
            case '+' -> new Token(TokenType.PLUS, "+");
            case '*' -> new Token(TokenType.MULTIPLY, "*");
            case '/' -> new Token(TokenType.DIVIDE, "/");
            case '^' -> new Token(TokenType.POWER, "^");
            case '!' -> new Token(TokenType.POSTFIX, "!");
            default -> throw new IllegalArgumentException("Unexpected operator: " + ch);
        };
    }

    private Token readParenthesis() {
        char ch = peek();
        pos++;
        return (ch == '(') ? new Token(TokenType.LPAREN, "(") : new Token(TokenType.RPAREN, ")");
    }

    private boolean isPreviousOperator(List<Token> tokens) {
        TokenType last = tokens.get(tokens.size() - 1).getType();
        return last == TokenType.PLUS || last == TokenType.MINUS || 
               last == TokenType.MULTIPLY || last == TokenType.DIVIDE || 
               last == TokenType.POWER || last == TokenType.LPAREN || 
               last == TokenType.UNARY_MINUS;
    }

    private boolean isNumberStart(char ch) {
        return Character.isDigit(ch) || ch == '.';
    }

    private boolean isOperator(char ch) {
        return (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '!');
    }

    private boolean isIdentifierStart(char ch) {
        return Character.isLetter(ch);
    }
}