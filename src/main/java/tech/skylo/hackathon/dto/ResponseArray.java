package tech.skylo.hackathon.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

@JsonSerialize
@Data
public class ResponseArray {

    List<ClientResponse> responseList;

}
