package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ErrorConditionTest {

    private Resolver resolver;

    @BeforeEach
    void initObjects() {
        this.resolver = new Resolver(new Interpreter());
    }

    @Test
    void initalizers_must_not_return() {
        // given
        String script = """
        class Foo {
          init() {
            return "something else";
          }
        }
        """;

        // when
        var statements = Lox.parse(script);
        // parsing was successful
        Assertions.assertFalse(Lox.hadError);
        resolver.resolve(statements);

        // then
        Assertions.assertTrue(Lox.hadError);
    }

}
