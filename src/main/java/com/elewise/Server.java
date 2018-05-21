package com.elewise;

import com.elewise.api.*;
import com.elewise.crypto.ProviderManager;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int PORT = 13578;
    private HttpServer server;
    private int BUFFER_SIZE = 8192;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ObjectMapper objectMapper;

    public boolean start() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        try {
            this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/getcert", this::getCert);
            server.createContext("/getcerts", this::getCerts);
            server.createContext("/sign", this::sign);
            server.setExecutor(executorService);
            server.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private String extractBody(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        boolean isPost = requestMethod.equals("POST");
        setHeaders(exchange);
        if (isPost) {
            String body = null;
            try (InputStream inputStream = exchange.getRequestBody();
                 ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                int count;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((count = inputStream.read(buffer)) > 0)
                    result.write(buffer, 0, count);
                result.flush();
                body = result.toString("UTF-8");
            }
            logger.info("request body = " + body);
            return body;
        }
        return null;
    }

    private void getCert(HttpExchange exchange) throws IOException {
        String body = extractBody(exchange);
            CertificatRequest req = null;
            try {
                req = objectMapper.readValue(body, CertificatRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CertificateResponse resp = getCertificatByRequest(req);
            String data = objectMapper.writeValueAsString(resp);
            exchange.sendResponseHeaders(200, data.getBytes().length);
            exchange.getResponseBody().write(data.getBytes());
            exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }

    private void getCerts(HttpExchange exchange) throws IOException {
        String body = extractBody(exchange);

            CertificatListRequest req = null;
            try {
                req = objectMapper.readValue(body, CertificatListRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CertificateListResponse resp = getCertificateListByRequest(req);
            String data = objectMapper.writeValueAsString(resp);
            exchange.sendResponseHeaders(200, data.getBytes().length);
            exchange.getResponseBody().write(data.getBytes());
            exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }

    private CertificateListResponse getCertificateListByRequest(CertificatListRequest req) {
        CertificateListResponse resp = new CertificateListResponse();
        if (req == null) {
            resp.setError("Неверные входные параметры");
            return resp;
        }
        List<CertificateRec> list = ProviderManager.getProvider(req.getProvider()).getAllCertsRec(req);
        resp.setCerts(list);
        return resp;
    }

    private CertificateResponse getCertificatByRequest(CertificatRequest req) {
        CertificateResponse resp = new CertificateResponse();
        if (req == null) {
            resp.setError("Неверные входные параметры");
            return resp;
        }
        CertificateRec rec = ProviderManager.getProvider(req.getProvider()).getCertRecByThumbprint(req.getStoreLocation(), req.getThumbprint());
        resp.setCert(rec);
        return resp;
    }

    private void sign(HttpExchange exchange) throws IOException {
        String body = extractBody(exchange);

        SignRequest req = null;
        try {
            req = objectMapper.readValue(body, SignRequest.class);

            SignResponse resp = ProviderManager.getProvider(req.getProvider()).sign(req);
            String data = objectMapper.writeValueAsString(resp);
            exchange.sendResponseHeaders(200, data.getBytes().length);
            exchange.getResponseBody().write(data.getBytes());
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
        }
    }








    private void setHeaders(HttpExchange exchange) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "http://127.0.0.1:"+PORT);
        responseHeaders.put("Access-Control-Allow-Headers", asList("Origin", "X-Requested-With", "Content-Type", "Accept"));
        responseHeaders.put("Access-Control-Allow-Methods", asList("GET", "POST"));
        responseHeaders.put("Access-Control-Max-Age", singletonList("86400"));
        responseHeaders.put("Content-Type", singletonList("application/json"));
    }

    public void stop() {
        server.stop(0);
    }
}