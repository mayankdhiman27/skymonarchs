package tech.skylo.hackathon.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
public class ClientResponse {

    String hubId;
    String assetId;
    String assetName;
    Float latitude;
    Float longitude;
    Integer thrashLevel;
    String colorCode;
    String percentage;
    String totalCapacity = String.valueOf(110);

}
