package com.team2.backend.Controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.DTO.User.UserLoginDTO;
import com.team2.backend.DTO.User.UserSignUpDTO;
import com.team2.backend.Models.User;
import com.team2.backend.Service.UserService;

 
public class UserControllerTest {

      private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }
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
    mockMvc.perform(post("/register")
    .contentType(MediaType.APPLICATION_JSON)
    .content(new ObjectMapper().writeValueAsString(request)))
.andExpect(status().isOk())
.andExpect(header().string("Set-Cookie", Matchers.containsString("token=" + mockToken))) // Check for the token cookie
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
    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
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
    mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
        .andExpect(status().isOk())
        .andExpect(header().string("Set-Cookie", Matchers.containsString("token=" + mockToken))) // Check for the token cookie
        .andExpect(jsonPath("$.message").value("Login successful."))
        .andExpect(jsonPath("$.token").value(mockToken)); 
}


}
