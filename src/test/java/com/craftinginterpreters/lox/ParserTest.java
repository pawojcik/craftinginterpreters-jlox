package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class ParserTest {

    @Test
    void should_parse_number() {
        // given
        var token = new Token(TokenType.NUMBER, "not important", 1234, 0);
        var parser = new Parser(tokens(token));

        // when
        var exp = new ExprHelper(parser.expression());

        // then
        Assertions.assertTrue(exp.isLiteral());
        Assertions.assertEquals(1234, exp.value());
    }

    @Test
    void should_parse_string() {
        // given
        var token = new Token(TokenType.STRING, "not important", "hello world", 0);
        var parser = new Parser(tokens(token));

        // when
        var exp = new ExprHelper(parser.expression());

        // then
        Assertions.assertTrue(exp.isLiteral());
        Assertions.assertEquals("hello world", exp.value());
    }

//        @Test
        void should_parse_identifier() {
        // given
        var token = new Token(TokenType.IDENTIFIER, "not important", "foo", 0);
        var parser = new Parser(tokens(token));

        // when
        var exp = new ExprHelper(parser.expression());

        // then
        Assertions.assertTrue(exp.isLiteral());
        Assertions.assertEquals("foo", exp.value());
    }

    @Test
    void should_parse_equals() {
        // given
        var left = new Token(TokenType.STRING, "not important", "foo", 0);
        var operator = new Token(TokenType.EQUAL_EQUAL, "==", null, 0);
        var right = new Token(TokenType.STRING, "not important", "bar", 0);
        var parser = new Parser(tokens(left, operator, right));

        // when
        var exp = new ExprHelper(parser.expression());

        // then
        Assertions.assertTrue(exp.isBinary());
        Assertions.assertEquals(TokenType.EQUAL_EQUAL, exp.operatorType());
        Assertions.assertEquals("foo", exp.left().value());
        Assertions.assertEquals("bar", exp.right().value());
    }

    @Test
    void should_parse_not_equals() {
        // given
        var left = new Token(TokenType.STRING, "not important", "biz", 0);
        var operator = new Token(TokenType.BANG_EQUAL, "!=", null, 0);
        var right = new Token(TokenType.STRING, "not important", "baz", 0);
        var parser = new Parser(tokens(left, operator, right));

        // when
        var exp = new ExprHelper(parser.expression());

        // then
        Assertions.assertTrue(exp.isBinary());
        Assertions.assertEquals(TokenType.BANG_EQUAL, exp.operatorType());
        Assertions.assertEquals("biz", exp.left().value());
        Assertions.assertEquals("baz", exp.right().value());
    }

    @Test
    void should_prioritize_comparison_over_equals() {
        // given
        var eqLeft = new Token(TokenType.STRING, "", "a", 0);
        var eqOp = new Token(TokenType.EQUAL_EQUAL, "==", null, 0);
        var cmpLeft = new Token(TokenType.STRING, "", "b", 0);
        var cmpOp = new Token(TokenType.GREATER, ">", null, 0);
        var cmpRight = new Token(TokenType.STRING, "", "c", 0);
        var parser = new Parser(tokens(eqLeft, eqOp, cmpLeft, cmpOp, cmpRight));

        // when
        var exp = new ExprHelper(parser.expression());

        // then
        Assertions.assertTrue(exp.isBinary());
        Assertions.assertEquals(TokenType.EQUAL_EQUAL, exp.operatorType());
        Assertions.assertEquals("a", exp.left().value());
        Assertions.assertTrue(exp.right().isBinary());
        Assertions.assertEquals(TokenType.GREATER, exp.right().operatorType());
        Assertions.assertEquals("b", exp.right().left().value());
        Assertions.assertEquals("c", exp.right().right().value());
    }

    @Test
    void should_parse_series() {
        // given
        var tokens = tokens(
          new Token(TokenType.NUMBER, "1", 1, 0),
          new Token(TokenType.COMMA, ",", null, 0),
          new Token(TokenType.NUMBER, "2", 2, 0),
          new Token(TokenType.COMMA, ",", null, 0),
          new Token(TokenType.NUMBER, "3", 3, 0)
        );
        var parser = new Parser(tokens);

        // when
        var exp = new ExprHelper(parser.expression());

        // then
        Assertions.assertTrue(exp.isSeries());
        Assertions.assertEquals(3, exp.count());
    }

    private List<Token> tokens(Token ...tokens) {
        var result = new ArrayList<Token>();
        Collections.addAll(result, tokens);
        result.add(new Token(TokenType.EOF, "", null, 0));
        return result;
    }

    static class ExprHelper {
        private final Expr exp;

        ExprHelper(Expr exp) {
            this.exp = exp;
        }

        boolean isBinary() {
            return exp instanceof Expr.Binary;
        }

        boolean isLiteral() {
            return exp instanceof Expr.Literal;
        }

        TokenType operatorType() {
            if (!isBinary()) {
                return null;
            }
            return ((Expr.Binary) exp).operator.type;
        }

        Object value() {
            if (!isLiteral()) {
                return null;
            }
            return ((Expr.Literal) exp).value;
        }

        ExprHelper left() {
            if (!isBinary()) {
                return null;
            }
            return new ExprHelper(((Expr.Binary) exp).left);
        }

        ExprHelper right() {
            if (!isBinary()) {
                return null;
            }
            return new ExprHelper(((Expr.Binary) exp).right);
        }

        public boolean isSeries() {
            return exp instanceof Expr.Series;
        }

        public int count() {
            if (!isSeries()) {
                throw new IllegalStateException("Expression is not a Series");
            }

            return ((Expr.Series) exp).expressions.size();
        }
    }

}
