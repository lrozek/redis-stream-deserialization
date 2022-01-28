# redis-stream-deserialization

```shell
docker-compose exec redis redis-cli XADD mystream \* sensorId 1234 temperature 19.8
```

```shell
docker-compose exec redis redis-cli XADD mystream \* sensorId 1234 temperature 19.8 _class pl.lrozek.redis.stream.consumer.domain.TemperatureReadingDto
```
