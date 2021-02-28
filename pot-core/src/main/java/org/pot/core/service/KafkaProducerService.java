package org.pot.core.service;

import com.google.protobuf.GeneratedMessageV3;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.pot.core.message.ProtobufSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Description: User: zouhaiyang Date: 2021-02-18
 */
@Service
public class KafkaProducerService {

	public static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);
	private KafkaProducer<String, GeneratedMessageV3> producer;
	@PostConstruct
	private void init(){
//		connect("10.24.6.149:9092");
	}
	/**
	 * 连接kafka
	 *
	 * @param url ip:port,ip:port
	 */
	public void connect(String url) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
		//An id string to pass to the server when making requests.
		// The purpose of this is to be able to track the source of requests beyond just ip/port by allowing a logical application name to be included in server-side request logging.
//		props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ProtobufSerializer.class.getName());
		producer = new KafkaProducer<>(props);
	}

	public void sendPb(GeneratedMessageV3 msg) {
		producer.send(new ProducerRecord<>("Login", "10001", msg));
	}
}
