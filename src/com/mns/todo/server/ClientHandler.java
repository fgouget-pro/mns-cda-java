package com.mns.todo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientHandler {

    private Request req;
    private final Socket client;
    private final Router router;

    public ClientHandler(Socket client, Router router) {
        this.client = client;
        this.router = router;
    }

    public void handleClient() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        List<String> lines = new ArrayList<>();
        String line;
        while (!(line = br.readLine()).isBlank()){
            lines.add(line);
        }
        req = parseFirstLine(lines.getFirst());
        req.setHeaders(parseHeaders(lines));
        System.out.println(req);
        if (req.getHeaders().containsKey("Content-Length")){
            req.setBody(handleBody(br, Integer.parseInt(req.getHeaders().get("Content-Length"))));
        }
        System.out.println("Body of request= " + req.getBody());
        router.handleRouting(req, client);
        client.close();
    }


    private String handleBody(BufferedReader br, int size) throws IOException {
        char[] chars = new char[size];
        var read = br.read(chars, 0, chars.length);
        if (read == -1 || read != size){
            return "";
        }
        return String.valueOf(chars);
    }

    private Request parseFirstLine(String line) {
        String[] firstLine = line.split(" ");
        return new Request(firstLine[0], firstLine[1], firstLine[2]);
    }

    private Map<String,String> parseHeaders(List<String> lines){
        return lines.stream()
                .skip(1) // Ignore the first line, it is not a header
                .takeWhile(s -> !s.isBlank())
                .map(s -> s.split(": "))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }





}
