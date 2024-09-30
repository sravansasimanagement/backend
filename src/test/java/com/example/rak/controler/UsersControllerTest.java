package com.example.rak.controler;

import com.example.rak.modal.Users;
import com.example.rak.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@SpringBootTest
class UsersControllerTest {

    @Mock
    private UsersService userService;

    @InjectMocks
    private UsersController userController;

    private Users user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Users();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword"); // Assuming this is a mock password
    }

    @Test
    void testCreateUser() {
        when(userService.createUser(any(Users.class))).thenReturn(user);
        ResponseEntity<Users> response = userController.createUsers(user);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getId());
    }

    @Test
    void testGetUserById() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        ResponseEntity<Users> response = userController.getUserById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getId());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Users> response = userController.getUserById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteUser() {
        when(userService.deleteUserById(1L)).thenReturn(true);
        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    void testChangePassword_Success() {
        when(userService.updateUserPassword(1L, "newPassword123")).thenReturn(Optional.of(user));
        ResponseEntity<Users> response = userController.updateUserPassword(1L, "newPassword123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testChangePassword_Failure() {
        when(userService.updateUserPassword(1L, "newPassword123")).thenReturn(Optional.empty());
        ResponseEntity<Users> response = userController.updateUserPassword(1L, "newPassword123");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
    @Test
    public void testUpdateUserSuccess() {
        Users user = new Users();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setPassword("password");
        when(userService.updateUser(eq(1L), any(Users.class))).thenReturn(Optional.of(user));
        ResponseEntity<Users> response = userController.updateUser(1L, user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).updateUser(eq(1L), any(Users.class));
    }

    @Test
    public void testUpdateUserPasswordNotFound() {
        when(userService.updateUserPassword(eq(1L), anyString())).thenReturn(Optional.empty());
        ResponseEntity<Users> response = userController.updateUserPassword(1L, "newPassword");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).updateUserPassword(eq(1L), anyString());
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
        when(userService.getAllUsers()).thenReturn(usersList);
        ResponseEntity<List<Users>> response = userController.getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }


}