package tech.skylo.hackathon.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class HubRegistrationRequest {

    List<String> hubIds;

}
