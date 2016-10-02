package http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

public class HTTPRequest {

    private final static String USER_AGENT = "Mozilla/5.0";

    private final String urlString;
    private final Map<String, String> params;

    public HTTPRequest(String urlString, Map<String, String> params) {
        this.urlString = urlString;
        this.params = params;
    }

    public HTTPResponse sendGetRequest() throws MalformedURLException, IOException {
        System.out.println("HTTP request using GET");
        StringBuilder modifiedUrlString
                = new StringBuilder(urlString).append("?");
        modifiedUrlString.append(concatenateParameters(params));

        URL url = new URL(modifiedUrlString.toString());
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = httpConnection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        String contentType = httpConnection.getContentType();
        System.out.println(contentType);

        return new HTTPResponse(contentType, httpConnection.getInputStream());
    }

    public HTTPResponse sendPostRequest() throws MalformedURLException, IOException {
        System.out.println("HTTP request using POST");
        URL url = new URL(urlString);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("User-Agent", USER_AGENT);

        String paramString = concatenateParameters(params);

        httpConnection.setDoOutput(true);
        try (OutputStream out = httpConnection.getOutputStream()) {
            out.write(paramString.getBytes());
            out.flush();
        }

        int responseCode = httpConnection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        String contentType = httpConnection.getContentType();
        System.out.println(contentType);

        return new HTTPResponse(contentType, httpConnection.getInputStream());
    }

    private String concatenateParameters(Map<String, String> params) {
        return params.keySet().stream()
                .map(key -> key + "=" + params.get(key))
                .collect(Collectors.joining("&"));
    }
}
