package com.wangboyi;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Lab 7: calculator backend as an HTTP API service.
 * The program no longer reads command-line arguments; instead it starts a
 * tiny HTTP server (JDK built-in, no extra dependencies) that listens on
 * port 8000 and answers calculation requests like:
 *
 *   GET /calculate?a=4&op=%2B&b=2   ->   {"a":"4","op":"+","b":"2","result":6.0}
 *
 * The calculation logic itself is still in Calculator.eval(), unchanged.
 */
public class App {

    public static void main(String[] args) throws IOException {
        int port = 8000; // the backend listens on this port inside the container
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/calculate", App::handleCalculate);
        server.setExecutor(null); // use the default executor
        server.start();
        System.out.println("Calculator API server listening on port " + port);
    }

    /** Handle one GET /calculate request. */
    private static void handleCalculate(HttpExchange exchange) throws IOException {
        // Only GET is supported, everything else gets a 405 response.
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        try {
            Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
            String a = params.getOrDefault("a", "");
            String op = params.getOrDefault("op", "");
            String b = params.getOrDefault("b", "");

            Calculator c = new Calculator();
            double result = c.eval(a, op, b);

            String json = "{\"a\":\"" + a + "\",\"op\":\"" + op
                    + "\",\"b\":\"" + b + "\",\"result\":" + result + "}";
            respond(exchange, 200, json);
        } catch (Exception e) {
            // eval() throws IllegalArgumentException for blank input, bad number or division by zero.
            String msg = e.getMessage() == null ? "Unknown error" : e.getMessage().replace("\"", "'");
            respond(exchange, 400, "{\"error\":\"" + msg + "\"}");
        }
    }

    /** Split "a=4&op=%2B&b=2" into a map, URL-decoding every value. */
    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> map = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return map;
        }
        for (String pair : rawQuery.split("&")) {
            int idx = pair.indexOf('=');
            if (idx < 0) {
                continue;
            }
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            map.put(key, value);
        }
        return map;
    }

    /** Write the status code, a JSON body and close the exchange. */
    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
