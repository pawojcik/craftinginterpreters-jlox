package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ScriptTests {

    @ParameterizedTest
    @MethodSource("scriptProvider")
    void run_script(Path scriptFile, Path outputFile) throws IOException {
        // given
        var script = readFile(scriptFile);
        var expectedOutput = readFile(outputFile);
        // when
        var actualOutput = runScript(script);
        // then
        Assertions.assertEquals(expectedOutput, actualOutput);
    }

    static Stream<Arguments> scriptProvider() throws IOException {
        return Files.list(Paths.get("expectation_tests"))
                .filter(file -> file.toString().endsWith(".lox"))
                .map(file -> Arguments.of(file, file.resolveSibling(file.getFileName() + ".out")));
    }

    String readFile(Path filename) throws IOException {
        Assertions.assertTrue(Files.exists(filename));
        return Files.readString(filename, StandardCharsets.UTF_8);
    }

    String runScript(String script) {
        var originalOut = System.out;
        try (var content = new ByteArrayOutputStream(10 * 1024); var out = new PrintStream(content)) {
            System.setOut(out);
            Lox.runScript(script);
            out.flush();
            return content.toString();
        } catch (Exception e) {
            Assertions.fail(e);
            return null;
        } finally {
            System.setOut(originalOut);
        }
    }

}
