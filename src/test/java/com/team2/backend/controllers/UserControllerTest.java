package com.team2.backend.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import static org.hamcrest.Matchers.containsString;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.controllers.UserController;
import com.team2.backend.dto.user.ChangeDisplayNameDTO;
import com.team2.backend.dto.user.ChangePasswordDTO;
import com.team2.backend.dto.user.ChangeUsernameDTO;
import com.team2.backend.dto.user.UserLoginDTO;
import com.team2.backend.dto.user.UserSignUpDTO;
import com.team2.backend.exceptions.InvalidCredentialsException;
import com.team2.backend.models.User;
import com.team2.backend.service.UserService;
import com.team2.backend.util.JwtUtil;

@AutoConfigureMockMvc(addFilters = false)
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegisterUser_Success() throws Exception {
        UserSignUpDTO request = new UserSignUpDTO();
        request.setDisplayName("Test User");
        request.setUsername("testuser");
        request.setPassword("password123");

        String mockToken = "mock-token";
        when(userService.createUser(ArgumentMatchers.any(User.class))).thenReturn(mockToken);

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
        UserSignUpDTO request = new UserSignUpDTO();
        request.setDisplayName("Test User");
        request.setUsername("");
        request.setPassword("password123");

        this.mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username cannot be blank"));
    }

    @Test
    void testLoginUser_Success() throws Exception {
        UserLoginDTO loginRequestDTO = new UserLoginDTO();
        loginRequestDTO.setUsername("testUser");
        loginRequestDTO.setPassword("testPassword");

        String mockToken = "mock-token";
        when(userService.authenticateUser("testUser", "testPassword")).thenReturn(mockToken);

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
        UserLoginDTO loginRequestDTO = new UserLoginDTO("wrongUser", "wrongPassword");

        when(userService.authenticateUser("wrongUser", "wrongPassword"))
                .thenThrow(new InvalidCredentialsException("Invalid username or password"));

        this.mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    void testChangeUsername_Success() throws Exception {
        String username = "testUser";
        String newUsername = "newUser";
        ChangeUsernameDTO dto = new ChangeUsernameDTO(newUsername);
        String token = "newToken";

        when(userService.changeUsername(eq(username), any(ChangeUsernameDTO.class))).thenReturn(token);

        mockMvc.perform(put("/user/username")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("token=" + token)))
                .andExpect(content().string("Username changed successfully."));

        verify(userService, times(1)).changeUsername(eq(username), any(ChangeUsernameDTO.class));
    }

    @Test
void testChangePassword_Success() throws Exception {
    String username = "testUser";
    ChangePasswordDTO dto = new ChangePasswordDTO("oldPass", "newPass");
    String token = "newToken";

    when(userService.changePassword(eq(username), any(ChangePasswordDTO.class))).thenReturn(token);

    mockMvc.perform(put("/user/password")
            .param("username", username)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(header().string("Set-Cookie", containsString("token=" + token)))
            .andExpect(content().string("Password changed successfully."));

    verify(userService, times(1)).changePassword(eq(username), any(ChangePasswordDTO.class));
}

@Test
void testChangeDisplayName_Success() throws Exception {
    String username = "testUser";
    ChangeDisplayNameDTO dto = new ChangeDisplayNameDTO("New Display Name");
    String token = "newToken";

    when(userService.changeDisplayName(eq(username), any(ChangeDisplayNameDTO.class))).thenReturn(token);

    mockMvc.perform(put("/user/displayname")
            .param("username", username)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(header().string("Set-Cookie", containsString("token=" + token)))
            .andExpect(content().string("Display name changed successfully."));

    verify(userService, times(1)).changeDisplayName(eq(username), any(ChangeDisplayNameDTO.class));
}


}
