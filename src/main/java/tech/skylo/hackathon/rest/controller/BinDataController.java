package tech.skylo.hackathon.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.skylo.hackathon.dto.*;
import tech.skylo.hackathon.enums.TrashLevelColorCoding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@Slf4j
@RequestMapping(value = "/api/sky-monarchs")
public class BinDataController {

        ControllerHelper controllerHelper;

        private Long lastLoginRequestTimeMillis;

        private Long expirationMillis;

        private String token;

        Connection connection=null;
        Statement statement=null;
        ResultSet rs=null;

        public BinDataController(ControllerHelper controllerHelper){
            this.controllerHelper = controllerHelper;
        }

        @GetMapping(value = "/sensor-data")
        @ResponseBody
        @CrossOrigin(origins = "http://localhost:3000")
        ResponseEntity<String> getSensorData() throws JsonProcessingException {

            ResponseArray responseArray = new ResponseArray();

            ClientResponse clientResponse = new ClientResponse();

            try {
                if(connection == null) {
                    connection = DriverManager
                            .getConnection("jdbc:postgresql://" + "34.134.54.139:5432" + "/sensor_data?sslmode=" + "disable", "postgres", "skybase123");
                }
                // jdbc:postgresql://34.134.54.139:5432/sensor_data
                statement = connection.createStatement();
                rs = statement.executeQuery("select * from hub_data order by epoch_time_millis desc limit 1");

                if(rs.next())
                    log.info("result from db {}", rs.getString(6));

                Object obj = rs.getString(6);
                JSONParser parser = new JSONParser(rs.getString(6));
                LinkedHashMap object = (LinkedHashMap) parser.parse();

                clientResponse.setLatitude(Float.valueOf(object.get("latitude").toString()));
                clientResponse.setLongitude(Float.valueOf(object.get("longitude").toString()));

                if(object.containsKey("1506_1")) {
                    String thrashLevel = object.get("1506_1").toString();

                    log.info("thrashLevel : {}", thrashLevel);

                    if (thrashLevel != null && Integer.parseInt(thrashLevel) > 1) {
                        clientResponse.setThrashLevel(Integer.valueOf(thrashLevel));

                        int thrashLevelFinal = Integer.parseInt(thrashLevel);

                        Float filledPercentage = (float) (1 - thrashLevelFinal/110);

                        clientResponse.setPercentage(String.valueOf(1 - thrashLevelFinal/110));

                        if (filledPercentage >= 0.75) {
                            clientResponse.setColorCode(TrashLevelColorCoding.FULL.getValue());
                        }

                        else if(filledPercentage > 0.25 && filledPercentage < 0.75){
                            clientResponse.setColorCode(TrashLevelColorCoding.HAL_FULL.getValue());
                        }

                        else if(filledPercentage < 0.25){
                            clientResponse.setColorCode(TrashLevelColorCoding.ALMOST_EMPTY.getValue());
                        }
                    }
                    else{
                        log.info("Trash level less than 1...");
                    }
                }

            }
            catch (Exception ex){
                log.error("Error ", ex);
            }

            clientResponse.setAssetId("123454233");
            clientResponse.setAssetName("Bin1");

            List<ClientResponse> clientResponseList = new ArrayList<>();
            clientResponseList.add(clientResponse);
            responseArray.setResponseList(clientResponseList);
            ObjectMapper mapper = new ObjectMapper();
            String responseString = mapper.writeValueAsString(responseArray);

            return ResponseEntity.ok(responseString);
        }

    @RequestMapping(value = "/register-hub", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<String> registerHub(@RequestBody HubRegistrationRequest hubs) throws JsonProcessingException {

        log.info("Getting data for sensors..");

        if (lastLoginRequestTimeMillis != null && expirationMillis != null && System.currentTimeMillis() <= lastLoginRequestTimeMillis + expirationMillis) {

            log.info("Already logged in...");

        } else {

            String loginResult = controllerHelper.login();

            if (loginResult == null)
                return ResponseEntity.badRequest().body("Login not successful");

            Gson gson = new Gson();
            LoginResponse loginResponseObject = gson.fromJson(loginResult, LoginResponse.class);
            lastLoginRequestTimeMillis = System.currentTimeMillis();
            expirationMillis = loginResponseObject.getExpires_in();
            token = loginResponseObject.getAccess_token();
        }

        // register hub
        if(controllerHelper.registerHub(token, hubs.getHubIds()))
            return ResponseEntity.ok(HttpStatus.OK.toString());
        else return ResponseEntity.ok("Failed to register hubs.");
    }

    @RequestMapping(value = "/check-hub-status", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> checkHubStatus(@RequestBody HubRegistrationRequest hubs) throws JsonProcessingException {

         log.info("Checking hub status for hubs {}", hubs.getHubIds().toArray());

        if (lastLoginRequestTimeMillis != null && expirationMillis != null && System.currentTimeMillis() <= lastLoginRequestTimeMillis + expirationMillis) {

            log.info("Already logged in...");

        } else {

            String loginResult = controllerHelper.login();

            if (loginResult == null)
                return ResponseEntity.badRequest().body("Login not successful");

            Gson gson = new Gson();
            LoginResponse loginResponseObject = gson.fromJson(loginResult, LoginResponse.class);
            lastLoginRequestTimeMillis = System.currentTimeMillis();
            expirationMillis = loginResponseObject.getExpires_in();
            token = loginResponseObject.getAccess_token();
        }

        HubStatusResponse statusResult = controllerHelper.hubStatus(token, hubs);

        if(statusResult != null){
            Gson gson = new Gson();
            String responseJson = gson.toJson(statusResult);
            return ResponseEntity.ok(responseJson);
        }
        return ResponseEntity.ok("Failed");

    }

    @RequestMapping(value = "/create-webhook", method = RequestMethod.GET)
    ResponseEntity<String> createWebhook() throws JsonProcessingException {

        if (lastLoginRequestTimeMillis != null && expirationMillis != null && System.currentTimeMillis() <= lastLoginRequestTimeMillis + expirationMillis) {

            log.info("Already logged in...");

        } else {

            String loginResult = controllerHelper.login();

            if (loginResult == null)
                return ResponseEntity.badRequest().body("Login not successful");

            Gson gson = new Gson();
            LoginResponse loginResponseObject = gson.fromJson(loginResult, LoginResponse.class);
            lastLoginRequestTimeMillis = System.currentTimeMillis();
            expirationMillis = loginResponseObject.getExpires_in();
            token = loginResponseObject.getAccess_token();
        }

        return ResponseEntity.ok("OK");

    }


}
