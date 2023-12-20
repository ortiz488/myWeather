package com.example.myweather;

import android.util.Log;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequest {

    public static Response HttpRequest(String url) {
        try {
            Log.e("HttpUtils1", "FROM HttpUtils - URL:  ----->  " + url);

            //A new client is created. It is then populated with the provided URL and
            //the method is set to GET
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            //This small class allows the HTTP request to run on a different thread. This
            //is required for OkHTTP to work, in the future it may be improved so that it actually
            //runs parallel to other workloads.
            class Foo implements Runnable {
                private volatile Response response;

                @Override
                public void run() {
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                public Response getResponse() {
                    return response;
                }
            }

            //Runs the Foo class through a thread, then joins again to return the response.
            Foo foo = new Foo();
            Thread thread = new Thread(foo);
            thread.start();
            thread.join();
            return foo.getResponse();
        } catch (InterruptedException err) {
            //Debug
            Log.e("HttpUtils", "FROM HttpUtils CATCH ERROR - URL:  ----->  " + url);

            //If any error occurs during runtime then null is returned.
            System.out.println(err.getMessage());
            return null;
        }
    }
}
