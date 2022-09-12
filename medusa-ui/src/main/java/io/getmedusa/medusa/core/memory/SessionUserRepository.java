package io.getmedusa.medusa.core.memory;

import io.getmedusa.medusa.core.util.RandomUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SessionUserRepository implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private final SessionMemoryRepository sessionMemoryRepository;
    public SessionUserRepository(SessionMemoryRepository sessionMemoryRepository) {
        this.sessionMemoryRepository = sessionMemoryRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String sessionId) {
        //TODO ensure this is the right session (ie only used once)
        return Mono.just(User.withUsername(sessionId).password("{noop}" + RandomUtils.generatePassword(sessionId)).roles("USER").build());
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        throw new IllegalStateException("All sessions are one time use, not allowed to change passwords");
    }


}
