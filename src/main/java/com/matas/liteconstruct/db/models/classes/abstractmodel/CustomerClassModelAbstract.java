package com.matas.liteconstruct.db.models.classes.abstractmodel;

import java.util.UUID;

public interface CustomerClassModelAbstract {

	public UUID getId();
	
	public String getName();
	
	public byte getType();
	
	public int getPermission();
}
