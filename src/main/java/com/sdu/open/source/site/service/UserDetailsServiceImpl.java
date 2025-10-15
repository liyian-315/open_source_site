package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.User;
import com.sdu.open.source.site.repository.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    private static String normalizeRole(String raw) {
        if (raw == null) return null;
        String r = raw.trim().toUpperCase(Locale.ROOT);
        return r.startsWith("ROLE_") ? r : "ROLE_" + r;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userDao.findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        List<GrantedAuthority> authorities = Optional.ofNullable(u.getRole())
                .map(s -> s.contains(",") ? Arrays.asList(s.split(",")) : List.of(s))
                .orElseGet(List::of)
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(UserDetailsServiceImpl::normalizeRole)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 打印
        log.info("Load user '{}', enabled={}, authorities={}",
                u.getUsername(), u.getEnabled(), authorities);

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPassword(),
                Boolean.TRUE.equals(u.getEnabled()),
                true, true, true,
                authorities
        );
    }
}
