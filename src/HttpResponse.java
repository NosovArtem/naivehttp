import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    int statusCode = 200;
    String status = "Ok";
    Map<String, String> headers = new HashMap<>();
    String body = "";

    public HttpResponse() {
        headers.put("Server", "naive");
        headers.put("Connection", "close");
    }

    public void addHeaders(String key, String value) {
        this.headers.put(key, value);
    }

    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public byte[] getBytes(){
        return messages().getBytes(StandardCharsets.UTF_8);
    }

    public String messages() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1").append(HttpRequest.HTTP_FIRST_LINE_DELIMITER)
                .append(statusCode).append(HttpRequest.HTTP_FIRST_LINE_DELIMITER)
                .append(status).append(HttpRequest.HTTP_FIRST_LINE_DELIMITER)
                .append(HttpRequest.HTTP_NEW_LINE_DELIMITER);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(HttpRequest.HTTP_NEW_LINE_DELIMITER);
        }

        return builder.append(HttpRequest.HTTP_NEW_LINE_DELIMITER)
                .append(body)
                .toString();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.headers.put(HttpRequest.HTTP_CONTENT_LENGTH_KEY, String.valueOf(body.length()));
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
