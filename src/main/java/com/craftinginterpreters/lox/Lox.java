package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Lox {

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

    private static final String PROMPT = ">";

    static boolean hadError = false;
    

    static void runFile(String fileName) throws IOException {
        run(Files.readString(Paths.get(fileName), Charset.defaultCharset()));
        if (hadError) {
            System.exit(65);
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
            run(line);
            hadError = false;
        }
    }

    static void run(String script) {
        var scanner = new Scanner(script);
        var tokens = scanner.scanTokens();
        
        for (var token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}
