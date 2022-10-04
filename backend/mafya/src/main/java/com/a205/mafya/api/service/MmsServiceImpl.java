package com.a205.mafya.api.service;

import com.a205.mafya.api.request.MmsReq;
import com.a205.mafya.db.dto.MessageDTO;
import com.a205.mafya.db.dto.SmsRequestDTO;
import com.a205.mafya.db.dto.SmsResponseDTO;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import java.io.UnsupportedEncodingException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;




@Service
public class MmsServiceImpl implements MmsService{

    @Autowired
    private UserRepository userRepository;

    @Value("${mms.accessKey}")
    private String accessKey;
    @Value("${mms.secretKey}")
    private String secretKey;
    @Value("${mms.serviceId}")
    private String serviceId;
    @Value("${mms.senderPhoneNum}")
    private String senderPhoneNum;

    @Override
    public String sendMms(MmsReq mmsReq) {
        System.out.println(mmsReq.getMessages());
        System.out.println(Arrays.toString(mmsReq.getIds()));
        System.out.println(accessKey+" "+secretKey+" "+serviceId+" "+senderPhoneNum);
        List<Integer> ids= new ArrayList<Integer>();

        for (int i=0; i< mmsReq.getIds().length; i++){
            ids.add(Integer.parseInt(mmsReq.getIds()[i]));
        }
        List<User> users = userRepository.findAllById(ids);

        System.out.println("들어옴: "+users.get(0).getPhoneNum());

        for (int i=0; i< users.size();i++){
            MessageDTO temp = new MessageDTO();
            temp.setTo(users.get(i).getPhoneNum());
            temp.setContent(mmsReq.getMessages());
            try {
                sendSms(temp);
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
        return "제대로 출력됨";
    }

    public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+ this.serviceId+"/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
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

    public SmsResponseDTO sendSms(MessageDTO messageDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        List<MessageDTO> messages = new ArrayList<>();
        messages.add(messageDto);

        SmsRequestDTO request = SmsRequestDTO.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(senderPhoneNum)
                .content(messageDto.getContent())
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        SmsResponseDTO response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"), httpBody, SmsResponseDTO.class);

        return response;
    }

}
