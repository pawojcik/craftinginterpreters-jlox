package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    private static final String PROMPT = ">";

    private static final Interpreter interpreter = new Interpreter();

    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }

        if (args.length == 1) {
            Lox.runFile(args[0]);
        } else {
            Lox.runPrompt();
        }
    }

    static void runFile(String fileName) throws IOException {
        runScript(Files.readString(Paths.get(fileName), Charset.defaultCharset()));
        if (hadError) {
            System.exit(65);
        }
        if (hadRuntimeError) {
            System.exit(70);
        }
    }

    static void runPrompt() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(PROMPT);
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            var statements = parse(line);
            if (hadError || statements.isEmpty()) {
                hadError = false;
                continue;
            }

            var s = statements.getFirst();
            if (s instanceof Stmt.Expression) {
                System.out.println(((Stmt.Expression) s).expression.accept(interpreter));
            } else {
                s.accept(interpreter);
            }
        }
    }

    static List<Stmt> parse(String script) {
        var scanner = new Scanner(script);
        var tokens = scanner.scanTokens();
        var parser = new Parser(tokens);
        return parser.parse();
    }

    static void runScript(String script) {
        var statements = parse(script);

        if (hadError) {
            // stop if there was a syntax error
            return;
        }

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        if (hadError) {
            // stop if there's a resolution error
            return;
        }

        interpreter.interpret(statements);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}
