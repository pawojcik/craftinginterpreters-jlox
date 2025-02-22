package com.craftinginterpreters.lox;

import java.util.List;
import java.util.stream.Collectors;

import static com.craftinginterpreters.lox.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.Test;

class ScannerTest {

    @Test
    void should_recognize_single_character_tokens() {
        String source = "(){},.-+;/";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        assertEquals(11, tokens.size(), "Should have 10 tokens (including EOF)");

        assertEquals(TokenType.LEFT_PAREN, tokens.get(0).type);
        assertEquals(TokenType.RIGHT_PAREN, tokens.get(1).type);
        assertEquals(TokenType.LEFT_BRACE, tokens.get(2).type);
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(3).type);
        assertEquals(TokenType.COMMA, tokens.get(4).type);
        assertEquals(TokenType.DOT, tokens.get(5).type);
        assertEquals(TokenType.MINUS, tokens.get(6).type);
        assertEquals(TokenType.PLUS, tokens.get(7).type);
        assertEquals(TokenType.SEMICOLON, tokens.get(8).type);
        assertEquals(TokenType.SLASH, tokens.get(9).type);
        assertEquals(TokenType.EOF, tokens.get(10).type);
    }

    @Test
    void should_handle_identifiers() {
        String source = "these are123 identi_fiers";
        Scanner scanner = new Scanner(source);

        List<Token> tokens = scanner.scanTokens();

        assertEquals(4, tokens.size());
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
        assertEquals("these", tokens.get(0).lexeme);
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals("are123", tokens.get(1).lexeme);
        assertEquals(TokenType.IDENTIFIER, tokens.get(2).type);
        assertEquals("identi_fiers", tokens.get(2).lexeme);
    }

    @Test
    void should_handle_keywords() {
        String source = "and class else false for fun if nil or print return super this true var while";
        Scanner scanner = new Scanner(source);

        List<Token> tokens = scanner.scanTokens();

        assertEquals(17, tokens.size());
        assertIterableEquals(
                List.of(AND, CLASS, ELSE, FALSE, FOR, FUN, IF, NIL, OR, PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE, EOF),
                tokens.stream().map(t -> t.type).collect(Collectors.toList())
                );
    }

    @Test
    void should_skip_block_comment() {
        String source = "/* this is *** \n *** a comment */ var x = 123";
        Scanner scanner = new Scanner(source);

        List<Token> tokens = scanner.scanTokens();

        assertEquals(5, tokens.size());
        assertIterableEquals(List.of(VAR, IDENTIFIER, EQUAL, NUMBER, EOF), tokens.stream().map(t -> t.type).collect(Collectors.toList()));
    }

    @Test
    void should_skip_nested_block_comment() {
        String source = "/* this is /*** another one \n ***/ a comment */ var x = 123";
        Scanner scanner = new Scanner(source);

        List<Token> tokens = scanner.scanTokens();

        assertEquals(5, tokens.size());
        assertIterableEquals(List.of(VAR, IDENTIFIER, EQUAL, NUMBER, EOF), tokens.stream().map(t -> t.type).collect(Collectors.toList()));
    }

    @Test
    void should_detect_unfinished_nested_block_comment() {
        String source = "/* this is /*** another one \n *** a comment */ var x = 123";
        Scanner scanner = new Scanner(source);

        List<Token> tokens = scanner.scanTokens();

        assertEquals(1, tokens.size());
        assertEquals(EOF, tokens.get(0).type);
    }
} 