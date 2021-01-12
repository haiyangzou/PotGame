package org.pot.core.io.message;

import org.pot.core.util.JsonUtils;

/**
 * 跨服消息
 * <br>
 * 内部使用grpc通信
 *
 * @author JiangZhiYong
 * @note 暂时不实用，无跨服消息，跨服待实现
 * @mail 359135103@qq.com
 */
@Deprecated
public final class CrossMessage {

	/**
	 * 消息来源服务器
	 */
	private int fromServerId;
	/**
	 * 发送目的服务器，如果大于0，发送到指定服务器
	 */
	private int toServerId;
	/**
	 * 发往服务器类型
	 */
	private int toServerType;
	/**
	 * 消息id
	 */
	private int mid;
	/**
	 * 消息体
	 */
	private byte[] bytes;


	/**
	 * 发送到类型服务器
	 *
	 * @param fromServerId
	 * @param toServerType
	 * @param mid
	 */
	public CrossMessage(int fromServerId, int toServerType, int mid, byte[] bytes) {
		this(fromServerId, 0, toServerType, mid, bytes);
	}

	/**
	 * 发送到指定某一个服务器
	 *
	 * @param fromServerId
	 * @param toServerId
	 * @param toServerType
	 * @param mid
	 */
	public CrossMessage(int fromServerId, int toServerId, int toServerType, int mid, byte[] bytes) {
		super();
		this.fromServerId = fromServerId;
		this.toServerId = toServerId;
		this.toServerType = toServerType;
		this.mid = mid;
		this.bytes = bytes;
	}

	public int getFromServerId() {
		return fromServerId;
	}

	public void setFromServerId(int fromServerId) {
		this.fromServerId = fromServerId;
	}

	public int getToServerId() {
		return toServerId;
	}

	public void setToServerId(int toServerId) {
		this.toServerId = toServerId;
	}

	public int getToServerType() {
		return toServerType;
	}

	public void setToServerType(int toServerType) {
		this.toServerType = toServerType;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public String toString() {
		return JsonUtils.toJSON(this);
	}

	/**
	 * 消息长度
	 *
	 * @return
	 */
	public int getMsgLength() {
		return 16 + bytes.length;
	}
}
