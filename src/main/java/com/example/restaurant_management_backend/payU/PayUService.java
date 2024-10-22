package com.example.restaurant_management_backend.payU;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayUService {

    public static final String PAYU_URI_PREFIX = "https://secure.snd.payu.com";
    public static final String PAYU_URL = PAYU_URI_PREFIX + "/pl/standard/user/oauth/authorize";
    public static final String ORDER_CREATE_URL = PAYU_URI_PREFIX + "/api/v2_1/orders";
    public static final String CLIENT_ID = "485688";
    public static final String CUSTOMER_IP = "127.0.0.1";
    public static final String ORDER_DESCRIPTION = "Restaurant order";
    public static final String ORDER_CURRENCY = "PLN";
    public static final String CLIENT_SECRET = "55f94323d6c3c0afa854fe8376108a33";
    public static final String GRANT_TYPE = "client_credentials";

    public PayUOrderResponse createOrder(String accessToken, Double amount, Long orderId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = new HashMap<>();
        body.put("customerIp", CUSTOMER_IP);
        body.put("merchantPosId", CLIENT_ID);
        body.put("description", ORDER_DESCRIPTION);
        body.put("currencyCode", ORDER_CURRENCY);
        long amountInGrosze = convertZlotyToGrosze(amount);
        body.put("totalAmount", amountInGrosze);
        body.put("extOrderId", orderId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<PayUOrderResponse> response = restTemplate.exchange(ORDER_CREATE_URL, HttpMethod.POST, request, PayUOrderResponse.class);
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        return response.getBody();

    }

    private long convertZlotyToGrosze(Double amount) {
        return (long) (amount * 100);
    }

    public PayUTokenResponseDTO getPayUToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> requestEntity = prepareRequestEntity(headers);

        ResponseEntity<PayUTokenResponseDTO> response = restTemplate.exchange(PAYU_URL, HttpMethod.POST, requestEntity, PayUTokenResponseDTO.class);

        return response.getBody();
    }

    private HttpEntity<String> prepareRequestEntity(HttpHeaders headers) {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", GRANT_TYPE);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);

        StringBuilder requestBody = new StringBuilder();
        params.forEach((key, value) -> requestBody.append(key).append("=").append(value).append("&"));
        requestBody.setLength(requestBody.length() - 1); // remove trailing "&"

        return new HttpEntity<>(requestBody.toString(), headers);
    }


}

