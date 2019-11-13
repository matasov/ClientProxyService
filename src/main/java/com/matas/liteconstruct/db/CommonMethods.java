package com.matas.liteconstruct.db;

import java.util.UUID;

public interface CommonMethods {
	public static UUID getUUID(String value) {
		return ((value == null || value.equals("null")) ? null : UUID.fromString(value));
	}
}
