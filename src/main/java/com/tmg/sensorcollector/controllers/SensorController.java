package com.tmg.sensorcollector.controllers;

import com.tmg.sensorcollector.services.SensorService;
import com.tmg.sensorcollector.models.SensorInformation;
import com.tmg.sensorcollector.models.dto.SensorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/sensor")
public class SensorController {

    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping()
    public ResponseEntity<SensorResponse> sensorCollector(@RequestBody SensorInformation sensorInformation) {
        if(Objects.isNull(sensorInformation))
            return new ResponseEntity<>(new SensorResponse("Sensor Information is null", null), HttpStatus.BAD_REQUEST);

        log.info("Sensor Information: {}", sensorInformation);
        sensorService.sendToKafka(sensorInformation);
        return new ResponseEntity<>(new SensorResponse("Sensor Information received", sensorInformation), HttpStatus.OK);
    }
}
