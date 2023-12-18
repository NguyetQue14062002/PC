package org.example.Controller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class SecondAPIHandler {
    public static void callSecondAPI(String barGuid) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:8080/api/verify");

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("barGuid", barGuid);

            StringEntity requestBodyEntity = new StringEntity(requestBody.toString());
            httpPost.setEntity(requestBodyEntity);
            httpPost.setHeader("Content-Type", "application/json");

            // Gửi yêu cầu POST đến second API
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            // Xử lý phản hồi từ second API
            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
