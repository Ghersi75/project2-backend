package com.team2.backend.Controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.DTO.User.UserLoginDTO;
import com.team2.backend.DTO.User.UserSignUpDTO;
import com.team2.backend.Exceptions.InvalidCredentialsException;
import com.team2.backend.Models.User;
import com.team2.backend.Service.UserService;
import com.team2.backend.util.JwtUtil;

@AutoConfigureMockMvc(addFilters = false) // Disable Spring Security
@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper(); // Initialize ObjectMapper here

    @Test
    void testRegisterUser_Success() throws Exception {
        // Mock input and output
        UserSignUpDTO request = new UserSignUpDTO();
        request.setDisplayName("Test User");
        request.setUsername("testuser");
        request.setPassword("password123");

        // Mock behavior of UserService
        String mockToken = "mock-token";
        when(userService.createUser(ArgumentMatchers.any(User.class))).thenReturn(mockToken);

        // Perform POST request and verify response
        this.mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", Matchers.containsString("token=" + mockToken))) // Check for //
                                                                                                         // the token
                                                                                                         // cookie
                .andExpect(jsonPath("$.message").value("Register successful."))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testRegisterUser_BadRequest() throws Exception {
        // Mock input with invalid data
        UserSignUpDTO request = new UserSignUpDTO();
        request.setDisplayName("Test User");
        request.setUsername(""); // Empty username
        request.setPassword("password123");

        // Perform POST request and verify response
        this.mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username cannot be blank"));
    }

    @Test
    void testLoginUser_Success() throws Exception {
        // Mock the request DTO
        UserLoginDTO loginRequestDTO = new UserLoginDTO();
        loginRequestDTO.setUsername("testUser");
        loginRequestDTO.setPassword("testPassword");

        // Mock the service call
        String mockToken = "mock-token";
        when(userService.authenticateUser("testUser", "testPassword")).thenReturn(mockToken);

        // Perform the request and verify the response
        this.mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", Matchers.containsString("token=" + mockToken))) // Check for
                                                                                                         // the token
                                                                                                         // cookie
                .andExpect(jsonPath("$.message").value("Login successful."))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testLoginUser_BadRequest() throws Exception {
        // Mock the request DTO
        UserLoginDTO loginRequestDTO = new UserLoginDTO();
        loginRequestDTO.setUsername("testUser");
        loginRequestDTO.setPassword("testPassword");

        // Mock the service call
        String mockToken = "mock-token";
        when(userService.authenticateUser("testUser", "testPassword"))
                .thenThrow(new InvalidCredentialsException("Invalid username"));

        // Perform the request and verify the response
        this.mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized()) // the token // cookie
                .andExpect(jsonPath("$.message").value("Invalid username"));
    }

}
