package com.spring.delivery.domain.fixture;

import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.TestConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@TestComponent
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class UserFixtureGenerator {
    private final UserRepository userRepository;
    private static AtomicInteger emailCounter = new AtomicInteger(0);

    public UserFixtureGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static User createUserFixture() {
        return User.createUser("testUser" + emailCounter.incrementAndGet(), "test@example.com", "password", Role.CUSTOMER);
    }

    public User createSavedUserFixture() {
        return userRepository.save(createUserFixture());
    }

    public UserDetailsImpl createdPrincipalFixture() {
        return new UserDetailsImpl(createSavedUserFixture());
    }
}
