package tech.skylo.hackathon.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tech.skylo.hackathon.dto.HubRegistrationRequest;
import tech.skylo.hackathon.dto.HubStatusResponse;

import java.util.*;

@Slf4j
@Component
public class ControllerHelper {

    private final String parentURI = "https://daas-api-poc.skylo.io";


    public String login() throws JsonProcessingException {
        final String uri = "/api/v1/login";

        Map<String, String> creds = new HashMap<>();
        creds.put("username", "birender@skylo.tech");
        creds.put("password", "SkyMonarchs123");

        String json = new ObjectMapper().writeValueAsString(creds);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("tenantId", "skymonarchs");

        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.postForObject(parentURI.concat(uri), requestEntity, String.class);
            return  result;
        }
        catch (Exception e){
            log.error("Error in logging in ", e);
        }

        return null;
    }

    public HubStatusResponse hubStatus(String token, HubRegistrationRequest hubRegistrationRequest) throws JsonProcessingException {

        final String uri = "/api/v1/daas/hub/status";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("tenantId", "skymonarchs");
        headers.add("Authorization", "Bearer " + token);

        String requestJson = new ObjectMapper().writeValueAsString(hubRegistrationRequest);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            HubStatusResponse hubStatusResponse = restTemplate.postForObject(parentURI.concat(uri), requestEntity, HubStatusResponse.class);
            return hubStatusResponse;
        }
        catch (Exception ex){
            log.error("Error in checking hub status", ex);
        }

        return null;

    }


    public boolean registerHub(String token, List<String> hubIds) throws JsonProcessingException{

        final String uri = "/api/v1/daas/hub/registration";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("tenantId", "skymonarchs");
        headers.add("Authorization", "Bearer " + token);

        HubRegistrationRequest hubRegistrationRequest = new HubRegistrationRequest();
        hubRegistrationRequest.setHubIds(hubIds);

        String requestJson = new ObjectMapper().writeValueAsString(hubRegistrationRequest);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> result = restTemplate.exchange(parentURI.concat(uri), HttpMethod.PUT, requestEntity, String.class);
            log.info("result :  {}", result);
            return true;
        }
        catch (Exception ex){
            log.error("Error registering hubs {}", hubIds, ex);
        }

        return false;

    }






}
