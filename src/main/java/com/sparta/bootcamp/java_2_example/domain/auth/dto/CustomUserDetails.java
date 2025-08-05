package com.sparta.bootcamp.java_2_example.domain.auth.dto;

import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails, Serializable {

  private static final long serialVersionUID = 1L;

  // User 엔티티 대신 필요한 정보만 저장
  private final Long userId;
  private final String email;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(User user) {
    this.userId = user.getId();
    this.email = user.getEmail();
    this.password = user.getPassword();

    // roles가 없으므로 기본 권한만 설정
    this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
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
    return email;
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

  public Long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }
}
