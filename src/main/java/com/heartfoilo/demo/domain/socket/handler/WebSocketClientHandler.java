package com.heartfoilo.demo.domain.socket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class WebSocketClientHandler extends TextWebSocketHandler {

    Map<String, String> header = new HashMap<>();
    Map<String, String> input = new HashMap<>();
    Map<String, Map<String, String>> body = new HashMap<>();
    Map<String, Object> request = new HashMap<>();

    @Value("${websocket.approval_key")
    static String approvalKey;
    static final ObjectMapper objectMapper = new ObjectMapper();
    static String value_iv;
    static String value_key;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("connect success");

        header.put("approval_key", approvalKey);
        header.put("custtype", "P");
        header.put("tr_type", "1");
        header.put("content-type", "utf-8");

        input.put("tr_id", "H0STCNT0");
//        input.put("tr_id", "HDFSCNT0");
//        input.put("tr_key", "DNASAAPL");
        input.put("tr_key", "000020");
        body.put("input", input);
        request.put("header", header);
        request.put("body", body);

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(request)));
    }



    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
        throws Exception {
        String s = message.getPayload().toString();
        System.out.println("s :"+s);
//        handleData(s);
//        getData(s);

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
        throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
        throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private static void getData(String encryptedData){
        try {

            IvParameterSpec ivSpec = new IvParameterSpec(adjustByteLength(value_iv, 16));
            SecretKeySpec skeySpec = new SecretKeySpec(adjustByteLength(value_key, 32), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
//            byte[] encrypt = hexStringToByteArray(encryptedData);
//            System.out.println("len "+encrypt.length);
            byte[] original = cipher.doFinal(adjustByteLength(encryptedData, 16*1000));
            String originalString = new String(original);
            System.out.println("Original string: " + originalString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static byte[] adjustByteLength(String hex, int length) {
        byte[] data = new byte[length];
        byte[] temp = hexStringToByteArray(hex);

        int copyLength = Math.min(temp.length, data.length);
        System.arraycopy(temp, 0, data, 0, copyLength);
        return data;
    }
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static void handleData(String data) {
        if (data.trim().startsWith("{")) {
            parseJson(data);
        } else {
            parseCustomFormat(data);
        }
    }

    public static void parseJson(String jsonData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonData);
            JsonNode headerNode = rootNode.path("header");
            JsonNode bodyNode = rootNode.path("body");
            JsonNode outputNode = bodyNode.path("output");

            String trId = headerNode.path("tr_id").asText();
            String rtCd = bodyNode.path("rt_cd").asText();
            String msgCd = bodyNode.path("msg_cd").asText();
            String msg = bodyNode.path("msg1").asText();
            String iv = outputNode.path("iv").asText();
            String key = outputNode.path("key").asText();

            System.out.println("Transaction ID: " + trId);
            System.out.println("Return Code: " + rtCd);
            System.out.println("Message Code: " + msgCd);
            System.out.println("Message: " + msg);
            System.out.println("IV: " + iv);
            System.out.println("Key: " + key);
            value_iv = iv;
            value_key = key;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseCustomFormat(String data) {
//        String[] fields = data.split("\\|");
//        System.out.println("First field: " + fields[0]);
        getData(data);
    }
}
