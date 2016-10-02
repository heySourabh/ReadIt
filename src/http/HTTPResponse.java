package http;

import java.io.InputStream;

public class HTTPResponse {

    private final String contentType;
    private final InputStream inputStream;

    public HTTPResponse(String contentType, InputStream inputStream) {
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
