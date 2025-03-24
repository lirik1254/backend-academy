package backend.academy.bot;

// isolated from the "scrapper" module's containers!
// @TestConfiguration(proxyBeanMethods = false)
// class TestcontainersConfiguration {
//
//    @Bean
//    @RestartScope
//    @ServiceConnection(name = "redis")
//    GenericContainer<?> redisContainer() {
//        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);
//    }
//
//    @Bean
//    @RestartScope
//    @ServiceConnection
//    KafkaContainer kafkaContainer() {
//        return new KafkaContainer("apache/kafka-native:3.8.1").withExposedPorts(9092);
//    }
// }
