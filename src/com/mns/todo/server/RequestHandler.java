package com.mns.todo.server;

import java.io.IOException;
import java.net.Socket;


public abstract class RequestHandler {


    public void sendHttpStatus(Socket client, HttpStatusCode statusCode) throws IOException {
        var firstLine = String.format("HTTP/1.1 %d %s\r\n", statusCode.getCode(), statusCode.getMessage());
        client.getOutputStream().write(firstLine.getBytes());
    }

    public void sendHeader(Socket client, String headerName, String headerValue) throws IOException {
        var line = String.format("%s: %s\r\n", headerName, headerValue);
        client.getOutputStream().write(line.getBytes());
    }

    public void sendBody(Socket client, String body) throws IOException {
        var os = client.getOutputStream();
        os.write("\r\n".getBytes());
        os.write(body.getBytes());
    }

    public void sendLastLineAndFlush(Socket client) throws IOException {
        client.getOutputStream().write("\r\n\r\n".getBytes());
        client.getOutputStream().flush();
    }

    public void sendContentType(Socket client, String contentType) throws IOException {
        sendHeader(client, "Content-Type", contentType);
    }

    protected void sent501(Socket client) throws IOException {
        sendHttpStatus(client, HttpStatusCode.NOT_IMPLEMENTED);
        sendContentType(client, "text/html");
        sendBody(client, "<b>Not Implemented</b>");
        sendLastLineAndFlush(client);
    }

    protected void sent401(Socket client) throws IOException {
        sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
        sendContentType(client, "text/html");
        sendBody(client, "<b>Bad Request</b>");
        sendLastLineAndFlush(client);
    }

    public abstract void handleGet(Request request, Socket client) throws IOException;


    public void handlePost(Request request, Socket client) throws IOException {
        sent501(client);
    }

    public void handlePut(Request request, Socket client) throws IOException {
        sent501(client);
    }

    public void handleDelete(Request request, Socket client) throws IOException {
        sent501(client);
    }


    public void handle(Request request, Socket client) throws IOException {
        System.out.println(">>" + request.getMethod() + "<<");
        System.out.println(this.getClass());
        switch (request.getMethod()) {
            case "POST":
                handlePost(request, client);
                break;
            case "PUT":
                handlePut(request, client);
                break;
            case "DELETE":
                handleDelete(request, client);
                break;
            default:
                handleGet(request, client);
        }
    }


    protected StringBuilder createHtmlHeader(String title) {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append(String.format("<title>%s</title>\n", title));
        html.append("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH\" crossorigin=\"anonymous\">");
        html.append("</head></body><div class=\"container-fluid\">\n");
        return html;
    }

    protected StringBuilder createHtmlFooter(StringBuilder sb) {
        return sb.append("</div></body></html>\n");
    }


    protected String createInputLine(String label, String name, String value) {
        return createInputLine(label, name, value, "text");
    }

    protected String createInputLine(String label, String name, String value, String type) {
        String klass = "form-control";
        String labelClass = "form-label";
        String valueString = String.format("value=\"%s\"", value);
        if ("checkbox".equals(type)) {
            klass = "form-check-input";
            labelClass = "form-check-label";
            valueString = "true".equals(value) ? "checked" : "";
        }

        return String.format(
                "<label class=\"%s\">%s </label><input class=\"%s\" type=\"%s\" name=\"%s\" %s><br>\n",
                labelClass, label, klass, type, name, valueString);
    }

    protected String makeLink(String destination, String... label) {
        var l = String.join(" ", label);
        return String.format("<a href=\"%s\">%s</a>", destination, l);
    }

}
