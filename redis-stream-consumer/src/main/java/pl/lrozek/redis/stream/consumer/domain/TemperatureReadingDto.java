package pl.lrozek.redis.stream.consumer.domain;

import java.math.BigDecimal;

import lombok.ToString;

@ToString
public class TemperatureReadingDto {

    private String sensorId;

    private BigDecimal temperature;

}
