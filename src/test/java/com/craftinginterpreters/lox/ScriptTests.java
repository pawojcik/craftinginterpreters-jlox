package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class ScriptTests {

    @ParameterizedTest
    @MethodSource("scriptProvider")
    void run_script(String scriptFile) {
        // given
        var script = readFile(scriptFile);
        var expectedOutput = readFile(scriptFile + ".out");
        // when
        var actualOutput = runScript(script);
        // then
        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    static Stream<Arguments> scriptProvider() {
        return Stream.of(
                Arguments.of("/basic.lox"),
                Arguments.of("/logic.lox"),
                Arguments.of("/if.lox"),
                Arguments.of("/while.lox"),
                Arguments.of("/for.lox"),
                Arguments.of("/fib.lox"),
                Arguments.of("/fn_hi.lox"),
                Arguments.of("/fn_fib.lox"),
                Arguments.of("/fn_closure.lox"),
                Arguments.of("/fn_shadow.lox"),
                Arguments.of("/captured_declaration.lox"),
                Arguments.of("/class_print.lox")
                // add more test cases here
        );
    }

    String readFile(String filename) {
        try (var f = ScriptTests.class.getResourceAsStream(filename)) {
            Assertions.assertNotNull(f);
            return new String(f.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String runScript(String script) {
        var originalOut = System.out;
        try (var content = new ByteArrayOutputStream(10 * 1024); var out = new PrintStream(content)) {
            System.setOut(out);
            Lox.runScript(script);
            out.flush();
            return content.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
        }
    }

}
