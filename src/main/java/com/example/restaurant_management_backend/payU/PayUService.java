package com.example.restaurant_management_backend.payU;

import com.example.restaurant_management_backend.jpa.model.Order;
import com.example.restaurant_management_backend.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayUService {

    private static final String PAYU_URI_PREFIX = "https://secure.snd.payu.com";
    private static final String PAYU_URL = PAYU_URI_PREFIX + "/pl/standard/user/oauth/authorize";
    private static final String ORDERS_URL = PAYU_URI_PREFIX + "/api/v2_1/orders";
    private static final String CLIENT_ID = "485688";
    private static final String CUSTOMER_IP = "127.0.0.1";
    private static final String ORDER_DESCRIPTION = "Restaurant order";
    private static final String ORDER_CURRENCY = "PLN";
    private static final String CLIENT_SECRET = "55f94323d6c3c0afa854fe8376108a33";
    private static final String GRANT_TYPE = "client_credentials";
    private final OrderService orderService;

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

        ResponseEntity<PayUOrderResponse> response = restTemplate.exchange(ORDERS_URL, HttpMethod.POST, request, PayUOrderResponse.class);
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        return response.getBody();
    }

    public String getPayUOrderId(Long orderId) {
        return orderService.getOrderById(orderId).map(Order::getPayUOrderId).orElse(null);
    }

    public String getPayUOrderDetails(String payUOrderId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getPayUToken().access_token()); // TODO: Access token get only once

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(ORDERS_URL + "/" + payUOrderId, HttpMethod.GET, request, String.class);
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

