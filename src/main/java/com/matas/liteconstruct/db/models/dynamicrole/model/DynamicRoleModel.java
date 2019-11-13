package com.matas.liteconstruct.db.models.dynamicrole.model;

import java.util.UUID;

import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DynamicRoleModel implements DynamicRoleModelAbstract {

	private UUID id;

	private String name;

	private UUID companyId;

	private UUID serviceId;

	private UUID roleId;

  

//	public DynamicRoleModel(UUID id, String name, UUID company_id, UUID service_id, UUID role_id) {
//		this.company_id = id;
//		this.name = name;
//		this.company_id = company_id;
//		this.service_id = service_id;
//		this.role_id = role_id;
//	}
//
//	@Override
//	public UUID getId() {
//		return id;
//	}
//
//	@Override
//	public String getName() {
//		return name;
//	}
//
//	@Override
//	public UUID getCompanyId() {
//		return company_id;
//	}
//
//	@Override
//	public UUID getServiceId() {
//		return service_id;
//	}
//
//	@Override
//	public UUID getRoleId() {
//		return role_id;
//	}

}
