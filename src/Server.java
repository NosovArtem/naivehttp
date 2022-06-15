import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class Server {
    private final static int BUFFER_SIZE = 256;
    private AsynchronousServerSocketChannel server;
    private final HttpHandler handler;

    public Server(HttpHandler handler) {
        this.handler = handler;
    }

    public void bootstrap() {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress("127.0.0.1", 8099));

            while (true) {
                Future<AsynchronousSocketChannel> future = server.accept();
                handleClient(future);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Future<AsynchronousSocketChannel> future) throws InterruptedException, ExecutionException, TimeoutException, IOException {
        System.out.println("new client connection");
        AsynchronousSocketChannel clientChannel = future.get();
        while (clientChannel != null && clientChannel.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            StringBuilder builder = new StringBuilder();
            boolean keepReading = true;

            while (keepReading) {
                int readRezult = clientChannel.read(buffer).get();
                keepReading = readRezult == BUFFER_SIZE;
                buffer.flip();
                CharBuffer decode = StandardCharsets.UTF_8.decode(buffer);

                builder.append(decode);
                buffer.clear();
            }

            HttpRequest httpRequest = new HttpRequest(builder.toString());
            HttpResponse httpResponse = new HttpResponse();

            if (Objects.nonNull(handler)) {
                try {
                    String body = this.handler.handle(httpRequest, httpResponse);
                    if(Objects.nonNull(body) && !body.isEmpty()){
                        if(httpResponse.getHeaders().get(HttpRequest.HTTP_CONTENT_TYPE_KEY) == null) {
                            httpResponse.addHeaders(HttpRequest.HTTP_CONTENT_TYPE_KEY, "text/html; charset=utf-8");
                        }
                    }
                    httpResponse.setBody(body);
                } catch (Exception e) {
                    e.printStackTrace();
                    httpResponse.setStatusCode(500);
                    httpResponse.setStatus("Internal server error");
                    httpResponse.addHeaders(HttpRequest.HTTP_CONTENT_TYPE_KEY, "text/html; charset=utf-8");
                    httpResponse.setBody("<html><body><h1>Error happens</h1></body></html>");
                }


            } else {
                httpResponse.setStatusCode(404);
                httpResponse.setStatus("Not found");
                httpResponse.addHeaders(HttpRequest.HTTP_CONTENT_TYPE_KEY, "text/html; charset=utf-8");
                httpResponse.setBody("<html><body><h1>Resourse not found</h1></body></html>");
            }

            ByteBuffer resp = ByteBuffer.wrap(httpResponse.getBytes());
            clientChannel.write(resp);
            clientChannel.close();
        }
    }
}
