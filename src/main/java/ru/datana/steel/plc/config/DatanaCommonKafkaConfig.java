package ru.datana.steel.plc.config;

import lombok.Data;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class DatanaCommonKafkaConfig {

    @Value(value = "${datana.kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${datana.kafka.topic-for-sensor}")
    private String sensorTopic;

    @Value(value = "${datana.kafka.topic-for-meta-info}")
    private String metaInfoTopic;

    @Value(value = "${datana.kafka.ssl.keystore.type}")
    private String keyStoreType;

    @Value(value = "${datana.kafka.ssl.keystore.location}")
    private String keystoreLocation;


    @Value(value = "${datana.kafka.ssl.keystore.password}")
    private String keystorePassword;

    @Value(value = "${datana.kafka.ssl.key.password}")
    private String keyPassword;

    @Value(value = "${datana.kafka.ssl.truststore.type}")
    private String truststoreType;

    @Value(value = "${datana.kafka.ssl.truststore.location}")
    private String truststoreLocation;


    @Value(value = "${datana.kafka.ssl.truststore.password}")
    private String truststorePassword;

    @Value(value = "${datana.kafka.ssl.secure.random.implementation}")
    private String secureRandomImplementation;


    @Value(value = "${datana.kafka.consumer-meta.id}")
    private String kafkaConsumerMetaId;

    @Value(value = "${datana.kafka.producer-meta.id}")
    private String kafkaProducerMetaId;

    @Value(value = "${datana.kafka.consumer-meta.group-id}")
    private String kafkaConsumerMetaGroupId;

    public Map<String, Object> getConfigForKafka(String kafkaClientId) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        configs.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
        configs.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, keyStoreType);
        configs.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
        configs.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);

        configs.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, keyPassword);

        configs.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, truststoreType);
        configs.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
        configs.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);

        configs.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        configs.put(SslConfigs.SSL_SECURE_RANDOM_IMPLEMENTATION_CONFIG, secureRandomImplementation);

        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configs.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        configs.put(CommonClientConfigs.CLIENT_ID_CONFIG, kafkaClientId);

        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return configs;
    }

}
