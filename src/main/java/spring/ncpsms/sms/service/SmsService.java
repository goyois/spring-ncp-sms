package spring.ncpsms.sms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import spring.ncpsms.sms.dto.MessageDTO;
import spring.ncpsms.sms.dto.RequestDTO;
import spring.ncpsms.sms.dto.ResponseDTO;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-local.yaml")
public class SmsService {

    private final String smsConfirmNum = createSmsKey();



    @Value("${spring.naver-cloud-sms.accessKey}")  //yaml 파일은 계층 구조이므로 Spring 부터 경로를 잡아야함 23/06/07 Issue
    private String accessKey;
    @Value("${spring.naver-cloud-sms.secretKey}")
    private String secretKey;
    @Value("${spring.naver-cloud-sms.serviceId}")
    private String serviceId;
    @Value("${spring.naver-cloud-sms.senderPhone}")
    private String phone;


    public String getSignature(String time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + this.serviceId + "/messages";
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(time)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);
        return encodeBase64String;
    }

    public ResponseDTO sendSms(MessageDTO messageDTO) throws JsonProcessingException, RestClientException, URISyntaxException,
                                                             InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {

        String time = Long.toString(System.currentTimeMillis());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time);
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", getSignature(time)); // signature 서명

        List<MessageDTO> messages = new ArrayList<>();
        messages.add(messageDTO);

        RequestDTO request = RequestDTO.builder()
                .type("SMS")
                .content("COMM")
                .contentType("82")
                .from(phone)
                .content("[서비스먕 xxxx] 인증번호 [" + smsConfirmNum + "]를 입력해주세요.")
                .messages(messages)
                .build();


        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body,headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        ResponseDTO ResponseDto = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"),
                httpBody,
                ResponseDTO.class);
        ResponseDTO responseDto = new ResponseDTO(smsConfirmNum);
        return responseDto;
    }






    public static String createSmsKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 5; i++) {
            key.append(rnd.nextInt(10));
        }
        return key.toString();
    }
}
