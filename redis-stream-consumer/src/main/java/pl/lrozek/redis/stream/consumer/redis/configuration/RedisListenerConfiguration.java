package pl.lrozek.redis.stream.consumer.redis.configuration;

import static org.springframework.data.redis.connection.stream.StreamOffset.fromStart;
import static org.springframework.data.redis.stream.StreamMessageListenerContainer.create;
import static org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamReadRequest;
import org.springframework.data.redis.stream.Subscription;

import pl.lrozek.redis.stream.consumer.domain.TemperatureReadingDto;

@Configuration
public class RedisListenerConfiguration {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public Subscription listenerContainer(StreamListener<String, ObjectRecord<String, TemperatureReadingDto>> streamListener) {
        StreamMessageListenerContainerOptions<String, ObjectRecord<String, TemperatureReadingDto>> options = builder()
                .targetType(TemperatureReadingDto.class)
                .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, TemperatureReadingDto>> container = create(redisConnectionFactory, options);

        Subscription subscription = container.register(
                StreamReadRequest.builder(
                        fromStart("mystream"))
                        .cancelOnError(e -> false)
                        .build(),
                streamListener);

        container.start();
        return subscription;
    }

}
