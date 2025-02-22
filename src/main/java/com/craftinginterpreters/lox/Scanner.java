package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

public class Scanner {
    private static final Map<String, TokenType> KEYWORDS;

    static {
        KEYWORDS = new HashMap<>() {{
            put("and", AND);
            put("class", CLASS);
            put("else", ELSE);
            put("false", FALSE);
            put("for", FOR);
            put("fun", FUN);
            put("if", IF);
            put("nil", NIL);
            put("or", OR);
            put("print", PRINT);
            put("return", RETURN);
            put("super", SUPER);
            put("this", THIS);
            put("true", TRUE);
            put("var", VAR);
            put("while", WHILE);
        }};

    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // we are at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));

        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    skipSingleLineComment();
                } else if (match('*')) {
                    skipBlockComment();
                }
                else {
                    addToken(SLASH);
                }
            }
            case ' ', '\t', '\r' -> {}
            case '\n' -> line++;
            // string literals
            case '"' -> string();
            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
            }
        }
    }

    private void skipBlockComment() {
        int level = 1;

        while (!isAtEnd() && level > 0) {
            if (blockCommentStart()) {
                level++;
                advance();
                advance();
            } else if (blockCommentEnd()) {
                level--;
                advance();
                advance();
            } else {
                advance();
            }
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated block comment");
        }
    }

    private boolean blockCommentEnd() {
        return peek() == '*' && peekNext() == '/';
    }

    private boolean blockCommentStart() {
        return peek() == '/' && peekNext() == '*';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        addToken(KEYWORDS.getOrDefault(text, IDENTIFIER));
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
        }

        // Consume the rest of the number
        while (isDigit(peek())) {
            advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing "
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void skipSingleLineComment() {
        while (peek() != '\n' && !isAtEnd()) {
            advance();
        }
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }

        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }

        current++;
        return true;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(tokenType, text, literal, line));
    }
}
