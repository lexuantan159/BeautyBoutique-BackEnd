package com.example.beautyboutique.Services.ZaloPay;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

public interface ZaloPayService {
     Map<String, Object> createOrder(Integer userId, Integer[] cartItemIds, Integer voucherId) throws IOException, JSONException;
}
