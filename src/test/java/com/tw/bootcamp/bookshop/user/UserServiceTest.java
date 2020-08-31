package com.tw.bootcamp.bookshop.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUserWithValidInputs() throws InvalidEmailException {
        CreateUserCommand userCredentials = new CreateUserCommandTestBuilder().build();

        User user = userService.create(userCredentials);
        Optional<User> fetchedUser = userRepository.findByEmail(userCredentials.getEmail());

        assertTrue(fetchedUser.isPresent());
        assertEquals(user.getId(), fetchedUser.get().getId());
    }

    @Test
    void shouldNotCreateUserWhenUserWithSameEmailAlreadyExists() {
        CreateUserCommand userCommand = new CreateUserCommandTestBuilder().build();
        userRepository.save(new User(userCommand));

        InvalidEmailException createUserException = assertThrows(InvalidEmailException.class,
                () -> userService.create(userCommand));
        assertEquals("User with same email already created", createUserException.getMessage());
    }

    @Test
    void shouldNotCreateUserWhenInputIsInvalid() {
        CreateUserCommand invalidCommand = new CreateUserCommandTestBuilder().withEmptyEmail().build();

        assertThrows(ConstraintViolationException.class, () -> userService.create(invalidCommand));
    }
}