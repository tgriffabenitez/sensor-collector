package com.tmg.sensorcollector.models.dto;

import com.tmg.sensorcollector.models.SensorInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorResponse {
    private String message;
    private SensorInformation data;
}
