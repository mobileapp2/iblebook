package in.oriange.iblebook.utilities;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebServiceCalls {

    public static String APICall(String urlString, String jsonAnsString) {
        String serverResponse = "[]";
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, jsonAnsString);
            Request request = new Request.Builder()
                    .url(urlString)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            serverResponse = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverResponse;
    }
}
