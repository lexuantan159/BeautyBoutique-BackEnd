package com.example.beautyboutique.Services.ZaloPay;

import com.example.beautyboutique.Configs.ZalopayConstant;
import com.example.beautyboutique.DTOs.Requests.Payment.RefundRequestDTO;
import com.example.beautyboutique.DTOs.Requests.Payment.RefundStatusRequestDTO;
import com.example.beautyboutique.Models.*;
import com.example.beautyboutique.Repositories.*;
import com.example.beautyboutique.Utils.ZaloAlgorithem.HMACUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ZaloPayServiceImpl implements ZaloPayService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    VoucherDetailRepository voucherDetailRepository;


    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public String getCurrentTimeString() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat("yyMMdd");
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public BigDecimal sumPriceItem(Integer[] cartItemIds) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Integer cartItemId : cartItemIds) {
            Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
            if (cartItemOptional.isPresent()) {
                CartItem cartItem = cartItemOptional.get();
                totalPrice = totalPrice.add(cartItem.getTotalPrice());
            }
        }
        return totalPrice;
    }

    private Boolean checkQuantityInStock(Integer[] cartItemIds) {
        Boolean[] isValid = {false};
        for (Integer cartItemId : cartItemIds) {
            cartItemRepository.findById(cartItemId).ifPresent(cartItem -> {
                Product product = cartItem.getProduct();
                Integer inStock = product.getQuantity();
                Integer quantityBuy = cartItem.getQuantity();
                // check quantity in stock
                if (inStock - quantityBuy >= 0) {
                    isValid[0] = true;
                }
            });
        }
        return isValid[0];
    }


    @Override
    public Map<String, Object> createOrder(Integer userId, Integer[] cartItemIds, Integer voucherId) throws IOException, JSONException {
        Map<String, Object> resultMap = new HashMap<>();


        if (!checkQuantityInStock(cartItemIds)) {
            resultMap.put("returnmessage", "Not enough quantity!");
            resultMap.put("returncode", -4);
            return resultMap;
        }
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                resultMap.put("returnmessage", "User not found!");
                resultMap.put("returncode", -1);
                return resultMap;
            }
            User user = userOptional.get();
            String appUser = user.getUsername();
            String appTransId = getCurrentTimeString() + "_" + new Date().getTime();


            // Giả sử totalPrice đã được tính toán trước đó và là tiền đô
            BigDecimal totalPriceUSD = sumPriceItem(cartItemIds);
            System.out.println("totalPriceUSD: " + totalPriceUSD);
            int exchangeRate = 24000;
            BigDecimal totalPriceAfterDiscount = BigDecimal.ZERO;
            if (voucherId != null) {
                Optional<VoucherDetail> voucherDetailOptional = voucherDetailRepository.findByUserIdAndVoucherId(userId, voucherId);
                if (voucherDetailOptional.isPresent()) {
                    Optional<Voucher> voucherOptional = voucherRepository.findById(voucherId);
                    if (voucherOptional.isPresent()) {
                        Voucher voucher = voucherOptional.get();
                        BigDecimal discount = BigDecimal.valueOf(voucher.getDiscount());
                        BigDecimal discountAmount = totalPriceUSD.multiply(discount);
                        // Kiểm tra giảm giá tối thiểu
                        if (voucher.getMinimumOrder() != null && totalPriceUSD.compareTo(voucher.getMinimumOrder()) >= 0) {
                            // Nếu tổng tiền của đơn hàng lớn hơn hoặc bằng giảm giá tối thiểu, áp dụng giảm giá
                            // Kiểm tra xem có giới hạn giảm giá tối đa không
                            BigDecimal maximDiscount = voucher.getMaximDiscount() != null ? voucher.getMaximDiscount() : BigDecimal.ZERO;
                            if (discountAmount.compareTo(maximDiscount) > 0) {
                                System.out.println("discountAmount: " + discountAmount);
                                // Nếu số tiền giảm giá vượt quá giới hạn giảm giá tối đa, chỉ áp dụng giảm giá tối đa
                                discountAmount = maximDiscount;
                            }
                        } else {
                            // Nếu tổng tiền của đơn hàng nhỏ hơn giảm giá tối thiểu, không áp dụng giảm giá
                            discountAmount = BigDecimal.ZERO;
                        }
                        // Tính toán giá trị giảm giá sau khi chuyển đổi về VND
                        BigDecimal discountValueInVND = discountAmount.multiply(BigDecimal.valueOf(exchangeRate));
                        // Trừ số tiền giảm giá từ tổng tiền của đơn hàng
                        totalPriceAfterDiscount = totalPriceUSD.multiply(BigDecimal.valueOf(exchangeRate)).subtract(discountValueInVND);
                        // Kiểm tra xem giá sau khi giảm giá có nhỏ hơn giá tối thiểu của đơn hàng không
                        if (totalPriceAfterDiscount.compareTo(voucher.getMinimumOrder()) < 0) {
                            totalPriceAfterDiscount = voucher.getMinimumOrder();
                        }
                    }
                }
            }

            System.out.println("totalPriceAfterDiscount: " + totalPriceAfterDiscount);

            BigDecimal totalPrice = voucherId == null ? totalPriceUSD.multiply(BigDecimal.valueOf(exchangeRate)) : totalPriceAfterDiscount;
            Map<String, Object> spa = new HashMap<String, Object>() {{
                put("id", user.getId());
                put("price", totalPrice);
            }};

            if (!appUser.isEmpty()) {
                Map<String, Object> order = new HashMap<String, Object>() {{
                    put("appid", ZalopayConstant.APP_ID);
                    put("apptransid", appTransId); // translation missing: vi.docs.shared.sample_code.comments.app_trans_id
                    put("apptime", System.currentTimeMillis()); // miliseconds
                    put("appuser", appUser);
                    put("amount", totalPrice.longValue());
                    put("description", "Beauty Boutique - Payment by zalopay#" + getCurrentTimeString() + "_" + new Date().getTime());
                    put("bankcode", "zalopayapp");
                    put("item", new JSONObject(spa).toString());
                    put("embeddata", "{\"redirecturl\": \"http://localhost:8080/api/v1/callback\"}");
                }};

                String data = order.get("appid") + "|" + order.get("apptransid") + "|" + order.get("appuser") + "|" + order.get("amount")
                        + "|" + order.get("apptime") + "|" + order.get("embeddata") + "|" + order.get("item");
                order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConstant.KEY1, data));

                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost post = new HttpPost(ZalopayConstant.ORDER_CREATE_ENDPOINT);

                List<NameValuePair> params = new ArrayList<>();
                for (Map.Entry<String, Object> e : order.entrySet()) {
                    params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
                }
                post.setEntity(new UrlEncodedFormEntity(params));

                CloseableHttpResponse res = client.execute(post);
                BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
                StringBuilder resultJsonStr = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    resultJsonStr.append(line);
                }

                JSONObject jsonResult = new JSONObject(resultJsonStr.toString());
                Map<String, Object> finalResult = new HashMap<>();
                for (Iterator<String> it = jsonResult.keys(); it.hasNext(); ) {
                    String key = it.next();
                    finalResult.put(key, jsonResult.get(key));
                }
                finalResult.put("apptransid", appTransId);
                return finalResult;
            }
            logger.info("--------------------------------");
            resultMap.put("returnmessage", "User not found!");
            resultMap.put("returncode", -1);
            return resultMap;
        } catch (JSONException e) {
            logger.info(e.getMessage());
            resultMap.put("returnmessage", "JSONException!");
            resultMap.put("returncode", -1);
            return resultMap;
        }
    }

    public Object doCallBack(JSONObject result, String jsonStr) throws JSONException, NoSuchAlgorithmException, InvalidKeyException {

        Mac HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(ZalopayConstant.KEY2.getBytes(), "HmacSHA256"));

        try {
            JSONObject cbdata = new JSONObject(jsonStr);
            String dataStr = cbdata.getString("data");
            String reqMac = cbdata.getString("mac");

            byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

            // check if the callback is valid (from ZaloPay server)
            if (!reqMac.equals(mac)) {
                // callback is invalid
                result.put("returncode", -1);
                result.put("returnmessage", "mac not equal");
            } else {
                // payment success
                // merchant update status for order's status
                JSONObject data = new JSONObject(dataStr);
                logger.info("update order's status = success where app_trans_id = " + data.getString("app_trans_id"));

                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0); // callback again (up to 3 times)
            result.put("return_message", ex.getMessage());
        }

        return result;
    }

    public Map<String, Object> statusOrder(String appTransId) throws URISyntaxException, IOException, JSONException {

        String data = ZalopayConstant.APP_ID + "|" + appTransId + "|" + ZalopayConstant.KEY1;
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConstant.KEY1, data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appid", ZalopayConstant.APP_ID));
        params.add(new BasicNameValuePair("apptransid", appTransId));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(ZalopayConstant.ORDER_STATUS_ENDPOINT);
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {

            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("returncode", result.get("returncode"));
        finalResult.put("returnmessage", result.get("returnmessage"));
        finalResult.put("isprocessing", result.get("isprocessing"));
        finalResult.put("amount", result.get("amount"));
        finalResult.put("discountamount", result.get("discountamount"));
        finalResult.put("zptransid", result.get("zptransid"));
        return finalResult;
    }

    public Map<String, Object> sendRefund(RefundRequestDTO refundRequestDTO) throws JSONException, IOException {
        Map<String, Object> order = new HashMap<String, Object>() {{
            put("app_id", ZalopayConstant.APP_ID);
            put("zp_trans_id", refundRequestDTO.getZpTransId());
            put("m_refund_id", getCurrentTimeString() + " " + ZalopayConstant.APP_ID + " " +
                    System.currentTimeMillis() + " " + (111 + new Random().nextInt(888)));
            put("timestamp", System.currentTimeMillis());
            put("amount", refundRequestDTO.getAmount());
            put("description", refundRequestDTO.getDescription());
        }};

        String data = order.get("app_id") + "|" + order.get("zp_trans_id") + "|" + order.get("amount")
                + "|" + order.get("description") + "|" + order.get("timestamp");
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConstant.KEY1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ZalopayConstant.REFUND_PAYMENT_ENDPOINT);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject jsonResult = new JSONObject(resultJsonStr.toString());
        Map<String, Object> finalResult = new HashMap<>();
        for (Iterator<String> it = jsonResult.keys(); it.hasNext(); ) {
            String key = (String) it.next();
            finalResult.put(key, jsonResult.get(key));
        }

        return finalResult;
    }


    public Map<String, Object> getStatusRefund(RefundStatusRequestDTO refundStatusDTO) throws IOException, URISyntaxException, JSONException {

//        String mRefundId = "190308_2553_123456";
        String timestamp = Long.toString(System.currentTimeMillis()); // miliseconds
        String data = ZalopayConstant.APP_ID + "|" + refundStatusDTO.getRefundId() + "|" + timestamp; // app_id|m_refund_id|timestamp
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZalopayConstant.KEY1, data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("app_id", ZalopayConstant.APP_ID));
        params.add(new BasicNameValuePair("m_refund_id", refundStatusDTO.getRefundId()));
        params.add(new BasicNameValuePair("timestamp", timestamp));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(ZalopayConstant.REFUND_STATUS_PAYMENT_ENDPOINT);
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject jsonResult = new JSONObject(resultJsonStr.toString());
        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("return_code", jsonResult.get("return_code"));
        finalResult.put("return_message", jsonResult.get("return_message"));
        finalResult.put("sub_return_code", jsonResult.get("sub_return_code"));
        finalResult.put("sub_return_message", jsonResult.get("sub_return_message"));
        return finalResult;
    }
}