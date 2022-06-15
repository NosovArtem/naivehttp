public class Main {
    public static void main(String[] args) {
        new Server(((request, response) -> {
            return "<html><body><h1>Hello, client</h1>It handler</body></html>";
        })).bootstrap();
    }
}


