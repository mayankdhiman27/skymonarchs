package tech.skylo.hackathon.dto;

import java.util.List;

public class WebhookRequest {

    String type;
    String state;
    WebHookConfig config;
    List<String> events;

}
