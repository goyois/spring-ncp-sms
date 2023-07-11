package spring.ncpsms.sms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.ncpsms.sms.dto.MessageDTO;
import spring.ncpsms.sms.dto.ResponseDTO;
import spring.ncpsms.sms.service.SmsService;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class SmsController {

    /**
     * API 사용 시 NCP 대표번호 인증이 필요함
     */


    
    private final SmsService smsService;

    @PostMapping("/sms/send")
    public ResponseDTO sendSms(@RequestBody MessageDTO messageDTO) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException,
            InvalidKeyException, JsonProcessingException {
        ResponseDTO response = smsService.sendSms(messageDTO);
        return response;
    }



}
