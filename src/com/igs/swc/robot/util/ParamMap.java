package com.igs.swc.robot.util;

import java.util.HashMap;

public class ParamMap extends HashMap<String, String> {

	private static final long serialVersionUID = -9066227389562143683L;

	public ParamMap addVal(String key, String val) {
		put(key, val);
		return this;
	}
}
