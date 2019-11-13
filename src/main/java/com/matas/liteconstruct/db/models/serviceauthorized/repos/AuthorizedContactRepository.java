package com.matas.liteconstruct.db.models.serviceauthorized.repos;

import com.matas.liteconstruct.db.models.serviceauthorized.abstractmodel.AuthorizedContactAbstract;

public interface AuthorizedContactRepository {
  void addAuthorizedContact(AuthorizedContactAbstract authorizedContact);

  void removeAuthorizedContact(AuthorizedContactAbstract authorizedContact);

  void updateAuthorizedContact(AuthorizedContactAbstract authorizedContact);

  AuthorizedContactAbstract getAuthorizedContactByNamePassword(String authorizedContactName,
      String authorizedContactPassword);
  
  AuthorizedContactAbstract getAuthorizedContactByName(String authorizedContactName);
}
