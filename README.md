# redis-stream-deserialization

## Example based on 
https://redis.io/topics/streams-intro


## how to run it

```shell
docker-compose down -v && docker-compose up --build
```


## OK scenario

Adding entry with `_class` field-value pair. 
```shell
docker-compose exec redis redis-cli XADD mystream \* sensorId 1234 temperature 19.8 _class pl.lrozek.redis.stream.consumer.domain.TemperatureReadingDto
```
When `_class` field-value pair is present, entry is deserialized properly and following text is logged:
```
2022-01-28 13:14:55.146  INFO 1 --- [cTaskExecutor-1] .l.r.s.c.r.l.TemperatureReadingsListener : received following message: ObjectBackedRecord{recordId=1643375695080-0, value=TemperatureReadingDto(sensorId=1234, temperature=19.8)}

```


## not OK scenario
Adding entry without `_class` field-value pair

```shell
docker-compose exec redis redis-cli XADD mystream \* sensorId 1234 temperature 19.8
```
When `_class` field-value pair is missing it caues an exception and entry is not consumed.

```
2022-01-28 13:17:51.472 ERROR 1 --- [cTaskExecutor-1] ageListenerContainer$LoggingErrorHandler : Unexpected error occurred in scheduled task.

org.springframework.core.convert.ConversionFailedException: Failed to convert from type [org.springframework.data.redis.connection.stream.StreamRecords$ByteMapBackedRecord] to type [pl.lrozek.redis.stream.consumer.domain.TemperatureReadingDto] for value 'MapBackedRecord{recordId=1643375871467-0, kvMap={[B@1f6764b8=[B@2da89d71, [B@53d5009=[B@666e28c7}}'; nested exception is java.lang.IllegalArgumentException: Value must not be null!
	at org.springframework.data.redis.stream.StreamPollTask.convertRecord(StreamPollTask.java:198) ~[spring-data-redis-2.6.1.jar:2.6.1]
	at org.springframework.data.redis.stream.StreamPollTask.deserializeAndEmitRecords(StreamPollTask.java:176) ~[spring-data-redis-2.6.1.jar:2.6.1]
	at org.springframework.data.redis.stream.StreamPollTask.doLoop(StreamPollTask.java:148) ~[spring-data-redis-2.6.1.jar:2.6.1]
	at org.springframework.data.redis.stream.StreamPollTask.run(StreamPollTask.java:132) ~[spring-data-redis-2.6.1.jar:2.6.1]
	at java.base/java.lang.Thread.run(Unknown Source) ~[na:na]
Caused by: java.lang.IllegalArgumentException: Value must not be null!
	at org.springframework.util.Assert.notNull(Assert.java:201) ~[spring-core-5.3.15.jar:5.3.15]
	at org.springframework.data.redis.connection.stream.Record.of(Record.java:81) ~[spring-data-redis-2.6.1.jar:2.6.1]
	at org.springframework.data.redis.connection.stream.MapRecord.toObjectRecord(MapRecord.java:147) ~[spring-data-redis-2.6.1.jar:2.6.1]
	at org.springframework.data.redis.core.StreamObjectMapper.toObjectRecord(StreamObjectMapper.java:138) ~[spring-data-redis-2.6.1.jar:2.6.1]
	at org.springframework.data.redis.core.StreamOperations.map(StreamOperations.java:577) ~[spring-data-redis-2.6.1.jar:2.6.1]
	at org.springframework.data.redis.stream.DefaultStreamMessageListenerContainer.lambda$getDeserializer$2(DefaultStreamMessageListenerContainer.java:240) ~[spring-data-redis-2.6.1.jar:2.6.1]
	at org.springframework.data.redis.stream.StreamPollTask.convertRecord(StreamPollTask.java:196) ~[spring-data-redis-2.6.1.jar:2.6.1]
	... 4 common frames omitted
```

## Excpected behaviour
`_class` field-value pair should not be mandatory to propely deserialize entry to `ObjectRecord`. `StreamMessageListenerContainerOptions` configured with `targetType` has already information regarding class used for deserialization. Look at
https://github.com/lrozek/redis-stream-deserialization/blob/main/redis-stream-consumer/src/main/java/pl/lrozek/redis/stream/consumer/redis/configuration/RedisListenerConfiguration.java#L29

Enforcing producer system, which can be written using any language / framowork should not be enforced to add `_class`. In my opinion this is an implementation leak.


## redis UI

To inspect stream content
- open in a browser following url: `http://localhost:8001` and proceed to `I already have a database` -> `Connect to a Redis Database`

![redisInsight-newDb](https://user-images.githubusercontent.com/741781/151555931-cf999249-e4c6-4217-b354-13e7ce45cd58.png)

![redisInsight-streamContent](https://user-images.githubusercontent.com/741781/151555959-1388b5b4-60de-4915-ab27-801485e95c30.png)

- via CLI `docker-compose exec redis redis-cli XREAD COUNT 10 STREAMS mystream 0-0`
![cli-streamContent](https://user-images.githubusercontent.com/741781/151556112-54271556-0f92-4948-9943-b66235439c08.png)
