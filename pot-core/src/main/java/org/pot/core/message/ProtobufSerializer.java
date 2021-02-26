package org.pot.core.message;

import com.google.protobuf.GeneratedMessageV3;
import java.util.Map;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Description: User: zouhaiyang Date: 2021-02-23
 */
public class ProtobufSerializer implements Serializer<GeneratedMessageV3> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {

	}

	@Override
	public byte[] serialize(String s, com.google.protobuf.GeneratedMessageV3 protobufable) {
		return protobufable.toByteArray();
	}

	@Override
	public byte[] serialize(String topic, Headers headers, GeneratedMessageV3 data) {
		return data.toByteArray();
	}

	@Override
	public void close() {

	}
}
