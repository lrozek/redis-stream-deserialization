package pl.lrozek.redis.stream.consumer.redis.listener;

import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pl.lrozek.redis.stream.consumer.domain.TemperatureReadingDto;

@Component
@Slf4j
public class TemperatureReadingsListener implements StreamListener<String, ObjectRecord<String, TemperatureReadingDto>> {

    @Override
    public void onMessage(ObjectRecord<String, TemperatureReadingDto> message) {
        log.info("received following message: {}", message);
    }
}
