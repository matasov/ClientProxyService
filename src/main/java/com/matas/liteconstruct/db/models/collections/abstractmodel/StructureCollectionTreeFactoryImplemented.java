package com.matas.liteconstruct.db.models.collections.abstractmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matas.liteconstruct.db.models.classes.repos.CustomerClassRepository;
import com.matas.liteconstruct.db.models.collectiondynamicrole.repos.CollectionDynamicRoleRepository;
import com.matas.liteconstruct.db.models.collections.repos.StructureCollectionsFieldsRepository;

@Service
public class StructureCollectionTreeFactoryImplemented extends StructureCollectionTreeFactoryAbstract {

	@Autowired
	public void setStructureCollectionsFieldsRepositoryImplemented(
			StructureCollectionsFieldsRepository structureCollectionSqlRep) {
		this.structureCollectionSqlRep = structureCollectionSqlRep;
	}

	@Autowired
	public void setCustomerClassRepositoryImplemented(CustomerClassRepository customerClassRepository) {
		this.customerClassRepository = customerClassRepository;
	}

	@Autowired
	public void setCollectionDynamicRoleRepositoryImplemented(
			CollectionDynamicRoleRepository collectionDynamicRoleRepository) {
		this.collectionDynamicRoleRepository = collectionDynamicRoleRepository;
	}

}
