package org.pot.core.message;

import org.pot.core.util.JsonUtils;

/**
 * Description: User: zouhaiyang Date: 2021-02-23
 */
public interface IMessage {

	long getId();

	String topic();

	default String key() {
		return String.valueOf(getId());
	}

	default String value() {
		return JsonUtils.toJSON(this);
	}
}
