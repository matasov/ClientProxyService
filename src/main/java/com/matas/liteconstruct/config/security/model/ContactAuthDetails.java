package com.matas.liteconstruct.config.security.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.matas.liteconstruct.db.models.security.model.ContactAuth;
import com.matas.liteconstruct.db.models.security.model.RoleAuth;


public class ContactAuthDetails implements UserDetails {
  private static final long serialVersionUID = 1L;
  private Collection<? extends GrantedAuthority> authorities;
  private String password;
  private String login;

  public ContactAuthDetails(ContactAuth user) {
    System.out.println("start work with user: "+user);
    this.login = user.getLogin();
    this.password = user.getPassword();
    this.authorities = translate(user.getRoles());
  }

  private Collection<? extends GrantedAuthority> translate(List<RoleAuth> roles) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (RoleAuth role : roles) {
      String name = role.getName().toUpperCase();
      if (!name.startsWith("ROLE_")) {
        if (name.equals("SUPERADMIN")) {
          name = "ADMIN";
        } else if (name.equals("ANONYMOUS")) {
          name = "GUEST";
        }
        name = "ROLE_" + name;
      }
      authorities.add(new SimpleGrantedAuthority(name));
    }
    return authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return login;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
