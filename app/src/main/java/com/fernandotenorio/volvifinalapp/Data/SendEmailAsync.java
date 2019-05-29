package com.fernandotenorio.volvifinalapp.Data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.Client;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;

import java.io.IOException;

public class SendEmailAsync  {

    private static String apiKeyId = "";

    private static void getCollection(Client client, Request request) throws IOException {
        request.setMethod(Method.GET);
        request.setEndpoint("/v3/api_keys");
        request.addQueryParam("limit", "100");
        request.addQueryParam("offset", "0");
        try {
            processResponse();
        } catch (IOException ex) {
            throw ex;
        }
        request.clearQueryParams();
    }

    public static void post(Client client, Request request) throws IOException {
        request.setMethod(Method.POST);
        request.setEndpoint("/v3/api_keys");
        request.setBody("{\"name\": \"My api Key\",\"scopes\": [\"mail.send\",\"alerts.create\",\"alerts.read\"]}");

        processResponse();
        String apiKeyId = "";
        try {
            Response response = client.api(request);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());
            apiKeyId = json.path("SENDGRID_API_KEY").asText();
        } catch (IOException ex) {
            throw ex;
        }
        request.clearBody();
    }

    private static void getSingle(Client client, Request request) throws IOException {
        request.setMethod(Method.GET);
        request.setEndpoint("/v3/api_keys/" + apiKeyId);
        processResponse();
    }

    private static void patch(Client client, Request request) throws IOException {
        request.setMethod(Method.PATCH);
        request.setBody("{\"name\": \"A New Ho}");
        processResponse();
        request.clearBody();
    }

    private static void put(Client client, Request request) throws IOException {
        request.setMethod(Method.PUT);
        request.setBody("{\"name\": \"A New Hope\",\"scopes\": [\"user.profile.read\",\"user.profile.update\"]}");
        processResponse();
        request.clearBody();
    }

    private static void delete(Client client, Request request) throws IOException {
        request.setMethod(Method.DELETE);
        try {
            Response response = client.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }
    private static void processResponse() throws IOException {
        Client client = new Client();
        Request request = new Request();
        Response response = client.api(request);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders());
    }
}
