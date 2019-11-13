package com.matas.liteconstruct.db.models.collectiondynamicrole.abstractmodel;

import java.util.UUID;
import com.matas.liteconstruct.db.models.collectioncase.abstractmodel.CollectionCaseAbstract;
import com.matas.liteconstruct.db.models.dynamicrole.abstractmodel.DynamicRoleModelAbstract;

public interface CollectionDynamicRoleModelAbstract {

	UUID getId();
	
	UUID getCollectionId();

	int getActive();

	UUID getClassId();

	DynamicRoleModelAbstract getDynamicRole();
	
	CollectionCaseAbstract getCollectionCase();
}
