package com.matas.liteconstruct.db.models.classes.repos;

import java.util.Map;
import java.util.UUID;
import com.matas.liteconstruct.db.models.classes.abstractmodel.CustomerClassModelAbstract;

public interface CustomerClassRepository {

  public void addCustomerClass(CustomerClassModelAbstract customClass);

  /**
   * Remove field's data from user data structure collections table
   * 
   * @param field field's data
   */
  public void removeCustomerClass(UUID classId);

  /**
   * Update field's data in user data structure collections table
   * 
   * @param field field's data
   */
  public void updateCustomerClass(CustomerClassModelAbstract customClass);

  public Map<UUID, CustomerClassModelAbstract> listByType(int type, int permission);

  public CustomerClassModelAbstract getById(UUID id);

  public CustomerClassModelAbstract getByName(String name);
}
