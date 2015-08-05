package com.igs.swc.robot.command;

import java.util.Collection;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.igs.swc.eis.MsgKey;
import com.igs.swc.robot.RobotCommand;

public class CheckOfflineCmd implements RobotCommand {

	private RedisTemplate<String, String> redisTemplate;
	private Collection<String> swcKeys;

	private String offlineQueue = MsgKey.T_SWC_HB;

	private int interval = 180;

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setSwcKeys(Collection<String> swcKeys) {
		this.swcKeys = swcKeys;
	}

	public void setOfflineQueue(String offlineQueue) {
		this.offlineQueue = offlineQueue;
	}

	@Override
	public void execute() throws Exception {

		long now = System.currentTimeMillis() / 1000;

		HashOperations<String, String, String> hashOperations = redisTemplate
				.opsForHash();

		for (String swcKey : swcKeys) {

			long time = hashOperations.increment(swcKey, MsgKey.HW_OFFLINE_TIME,
					-now);

			if (time < -interval) { // 超时
				String s = hashOperations.get(swcKey, MsgKey.HW_OFFLINE_FLAG);
				if (s.equalsIgnoreCase(MsgKey.HW_OFFLINE_FLAG_F)) {
					hashOperations.put(swcKey, MsgKey.HW_OFFLINE_FLAG,
							MsgKey.HW_OFFLINE_FLAG_T);
					redisTemplate.convertAndSend(this.offlineQueue, swcKey);
				}
			}
			Thread.sleep(50);
		}
	}
}
