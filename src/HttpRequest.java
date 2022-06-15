import java.util.*;

public class HttpRequest {
    public final static String HTTP_DELIMITER = "\r\n\r\n";
    public final static String HTTP_NEW_LINE_DELIMITER = "\r\n";
    public final static String HTTP_FIRST_LINE_DELIMITER = " ";
    public final static String HTTP_HEADER_DELIMITER = ":";
    public final static String HTTP_CONTENT_LENGTH_KEY = "Content-length";
    public final static String HTTP_CONTENT_TYPE_KEY = "Content-type";

    private final String message;
    private final String header;
    private final String body;
    private final HttpMethod method;
    private final String url;
    private final String protocol;

    private Map <String, String> headers = new HashMap<>();
    private final int contentLength;

    public HttpRequest(String message) {
        this.message = message;
        String[] parts = message.split(HTTP_DELIMITER);
        this.header = parts[0];

        List<String> listHeaders = Arrays.asList(header.split(HTTP_NEW_LINE_DELIMITER));
        String[] firstLine = listHeaders.get(0).split(HTTP_FIRST_LINE_DELIMITER);
        this.method = HttpMethod.valueOf(firstLine[0]);
        this.url = firstLine[1];
        this.protocol = firstLine[2];
        this.headers = Collections.unmodifiableMap(parseHeaders(listHeaders));
        this.contentLength = Integer.parseInt(this.headers.getOrDefault(HTTP_CONTENT_LENGTH_KEY, "0"));
        this.body =  contentLength > 0 ? parts[1].trim().substring(0, contentLength) : "";
    }

    private Map<String, String> parseHeaders(List<String> listHeaders){
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i < listHeaders.size(); i++) {
            String[] headerParts = listHeaders.get(i).split(HTTP_HEADER_DELIMITER, 2);
            map.put(headerParts[0].trim(), headerParts[1].trim());
        }
        return map;
    }

    public String getMessage() {
        return message;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
