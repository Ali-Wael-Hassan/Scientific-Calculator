package core.parser;

import java.util.ArrayList;
import java.util.List;
import core.token.TokenType;
import core.token.Token;
import core.lexer.Lexer;

public class Parser {
    private int pos = 0;
    private final List<Token> input;

    public Parser(List<Token> input) {
        this.input = input;
    }

    public List<Token> infixToPostfix() {
        List<Token> postfix = new ArrayList<>();
        List<Token> ops = new ArrayList<>();
        Token lastToken = null;

        while (pos < input.size()) {
            Token token = input.get(pos++);
            TokenType type = token.getType();

            if (type == TokenType.EOF) break;

            if (lastToken != null && isImplicitMultNeeded(lastToken, token)) {
                handleOperator(new Token(TokenType.MULTIPLY, "*"), postfix, ops);
            }

            if (isOperand(type)) {
                postfix.add(token);
            }
            else if (isFunction(type) || type == TokenType.LPAREN) {
                ops.add(token);
            }
            else if (type == TokenType.RPAREN) {
                handleParentheses(postfix, ops);
            }
            else if (isOperator(type)) {
                handleOperator(token, postfix, ops);
            }

            lastToken = token;
        }

        while (!ops.isEmpty()) {
            postfix.add(popStack(ops));
        }

        return postfix;
    }

    private void handleOperator(Token current, List<Token> postfix, List<Token> ops) {
        while (!ops.isEmpty() && shouldPop(current, peekStack(ops))) {
            postfix.add(popStack(ops));
        }
        ops.add(current);
    }

    private void handleParentheses(List<Token> postfix, List<Token> ops) {
        while (!ops.isEmpty() && peekStack(ops).getType() != TokenType.LPAREN) {
            postfix.add(popStack(ops));
        }
        
        if (!ops.isEmpty()) {
            popStack(ops);
        }

        if (!ops.isEmpty() && isFunction(peekStack(ops).getType())) {
            postfix.add(popStack(ops));
        }
    }

    private boolean isImplicitMultNeeded(Token last, Token current) {
        TokenType l = last.getType();
        TokenType c = current.getType();

        if ((l == TokenType.NUMBER || l == TokenType.IDENTIFIER || l == TokenType.POSTFIX) && 
            (c == TokenType.LPAREN || isFunction(c) || c == TokenType.IDENTIFIER || c == TokenType.NUMBER)) {
            return true;
        }

        if (l == TokenType.RPAREN && 
            (c == TokenType.LPAREN || c == TokenType.NUMBER || c == TokenType.IDENTIFIER || isFunction(c))) {
            return true;
        }

        return false;
    }

    private boolean shouldPop(Token current, Token top) {
        if (top.getType() == TokenType.LPAREN) return false;

        int p1 = getPrecedence(current.getType());
        int p2 = getPrecedence(top.getType());

        if (isRightAssociative(current.getType())) {
            return p1 < p2;
        }
        return p1 <= p2;
    }


    private boolean isOperand(TokenType type) {
        return type == TokenType.NUMBER || type == TokenType.IDENTIFIER || type == TokenType.POSTFIX || Lexer.STAT_KEYWORDS.containsValue(type);
    }

    private boolean isOperator(TokenType type) {
        return type == TokenType.PLUS || type == TokenType.MINUS || 
               type == TokenType.MULTIPLY || type == TokenType.DIVIDE || 
               type == TokenType.POWER || type == TokenType.UNARY_MINUS;
    }

    private boolean isFunction(TokenType type) {
        return Lexer.KEYWORDS.containsValue(type);
    }

    private int getPrecedence(TokenType type) {
        return switch (type) {
            case POSTFIX -> 5;
            case POWER -> 4;
            case UNARY_MINUS -> 3;
            case MULTIPLY, DIVIDE -> 2;
            case PLUS, MINUS -> 1;
            default -> 0;
        };
    }

    private boolean isRightAssociative(TokenType type) {
        return type == TokenType.POWER || type == TokenType.UNARY_MINUS;
    }

    private Token peekStack(List<Token> stack) {
        if (stack.isEmpty()) return null;
        return stack.get(stack.size() - 1);
    }

    private Token popStack(List<Token> stack) {
        return stack.remove(stack.size() - 1);
    }
}