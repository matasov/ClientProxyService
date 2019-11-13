package com.matas.liteconstruct.db.models.dynamicrole.abstractmodel;

import java.util.UUID;

public interface DynamicRoleModelAbstract {

	public UUID getId();
	
	public String getName();
	
	public UUID getCompanyId();
	
	public UUID getServiceId();
	
	public UUID getRoleId();
}
