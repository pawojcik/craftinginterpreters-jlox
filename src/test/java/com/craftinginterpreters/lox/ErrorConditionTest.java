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

    @Test
    void super_is_not_available_outside_of_class() {
        // given
        String script = """
        super.does_not_exist();
        """;

        // when
        var statements = Lox.parse(script);
        Assertions.assertFalse(Lox.hadError);
        resolver.resolve(statements);

        // then
        Assertions.assertTrue(Lox.hadError);
    }

    @Test
    void super_is_not_available_without_superclass() {
        // given
        String script = """
        class Foo {
            wrong() {
                super.does_not_exist();
            }
        }
        """;

        // when
        var statements = Lox.parse(script);
        Assertions.assertFalse(Lox.hadError);
        resolver.resolve(statements);

        // then
        Assertions.assertTrue(Lox.hadError);
    }

}
