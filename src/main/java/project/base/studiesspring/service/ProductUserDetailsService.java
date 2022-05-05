package project.base.studiesspring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.base.studiesspring.repository.ProductUserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductUserDetailsService implements UserDetailsService {
    private final ProductUserRepository productUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username)  {
        return Optional.ofNullable(productUserRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("ProductUser not found"));
    }
}
