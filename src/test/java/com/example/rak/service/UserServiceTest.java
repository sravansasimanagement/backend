package com.example.rak.service;

import com.example.rak.modal.Users;
import com.example.rak.repository.UsersRepository;
import com.example.rak.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UsersRepository userRepository;

    @InjectMocks
    private UsersService userService;

    private Users user;

    @Mock
    private BCryptPasswordEncoder passwordEncoder; // Mock the password encoder


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Users();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any(Users.class))).thenReturn(user);
        Users createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<Users> foundUser = userService.getUserById(1L);
        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
    }

    @Test
    void testDeleteUser() {
        Users mockUser = new Users();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        userService.deleteUserById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testChangePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(Users.class))).thenReturn(user);
        Optional<Users> isChanged = userService.updateUserPassword(1L, "newPassword123");
        assertTrue(isChanged.isPresent());
        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    void testChangePassword_Failure_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserPassword(1L, "newPassword123");
        });
    }

    @Test
    public void testGetAllUsers() {
        Users user1 = new Users();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");
        user1.setPassword("password");
        Users user2 = new Users();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@example.com");
        user2.setPassword("password123");
        List<Users> usersList = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(usersList);
        List<Users> result = userService.getAllUsers();
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateUser_Success() {
        Users user = new Users();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setPassword("password");
        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setName("Updated John");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword("newPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(Users.class))).thenReturn(updatedUser);
        Optional<Users> result = userService.updateUser(1L, updatedUser);
        assertTrue(result.isPresent());
        assertEquals("Updated John", result.get().getName());
        assertEquals("updated@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(Users.class));
    }

}
