package org.pot.test;

import javax.annotation.Resource;
import org.junit.Test;
import org.pot.core.service.KafkaProducerService;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Description: User: zouhaiyang Date: 2021-02-18
 */
@SpringBootTest
public class ConfigTest {

	@Resource
	KafkaProducerService kafkaProducerService;

	@Test
	void testKafka() {

	}
}
