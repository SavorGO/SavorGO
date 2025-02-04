package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util;

import okhttp3.*;

import java.io.IOException;

public class HttpUtil {
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * Performs a GET request to the specified URL.
     *
     * @param url The URL to send the GET request to.
     * @return The response from the server.
     * @throws IOException If the request fails.
     */
    public static Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request).execute();
    }

    /**
     * Performs a POST request to the specified URL with the given JSON body.
     *
     * @param url  The URL to send the POST request to.
     * @param json The JSON body to send with the request.
     * @return The response from the server.
     * @throws IOException If the request fails.
     */
    public static Response post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return client.newCall(request).execute();
    }

    /**
     * Performs a PUT request to the specified URL with the given JSON body.
     *
     * @param url  The URL to send the PUT request to.
     * @param json The JSON body to send with the request.
     * @return The response from the server.
     * @throws IOException If the request fails.
     */
    public static Response put(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        return client.newCall(request).execute();
    }

    /**
     * Performs a DELETE request to the specified URL.
     *
     * @param url The URL to send the DELETE request to.
     * @return The response from the server.
     * @throws IOException If the request fails.
     */
    public static Response delete(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        return client.newCall(request).execute();
    }

    /**
     * Performs a DELETE request to the specified URL with the given JSON body.
     *
     * @param url  The URL to send the DELETE request to.
     * @param json The JSON body to send with the request.
     * @return The response from the server.
     * @throws IOException If the request fails.
     */
    public static Response deleteWithBody(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();
        return client.newCall(request).execute();
    }
}