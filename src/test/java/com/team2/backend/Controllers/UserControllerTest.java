package com.team2.backend.Controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.doNothing;
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
import com.team2.backend.DTO.User.ChangeDisplayNameDTO;
import com.team2.backend.DTO.User.ChangePasswordDTO;
import com.team2.backend.DTO.User.ChangeUsernameDTO;
import com.team2.backend.DTO.User.UserLoginDTO;
import com.team2.backend.DTO.User.UserSignUpDTO;
import com.team2.backend.Exceptions.InvalidCredentialsException;
import com.team2.backend.Models.User;
import com.team2.backend.Service.UserService;
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
        UserLoginDTO loginRequestDTO = new UserLoginDTO();
        loginRequestDTO.setUsername("testUser");
        loginRequestDTO.setPassword("testPassword");

        String mockToken = "mock-token";
        when(userService.authenticateUser("testUser", "testPassword"))
                .thenThrow(new InvalidCredentialsException("Invalid username"));

        this.mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized()) // the token // cookie
                .andExpect(jsonPath("$.message").value("Invalid username"));
    }

      @Test
      void changeUsername_ShouldReturnOkStatus() throws Exception {
          Long userId = 1L;
          ChangeUsernameDTO changeUsernameDTO = new ChangeUsernameDTO("newUsername", "password");
  
          when(userService.changeUsername(userId, changeUsernameDTO)).thenReturn(null);  

          this.mockMvc.perform(put("/user/username")
                  .param("userId", String.valueOf(userId))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(changeUsernameDTO)))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.message").value("Username changed successfully."));
      }
  
      @Test
      void changeUsername_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
          Long userId = 1L;
          ChangeUsernameDTO changeUsernameDTO = new ChangeUsernameDTO("", "password");  // Invalid username
  
          this.mockMvc.perform(put("/user/username")
                  .param("userId", String.valueOf(userId))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(changeUsernameDTO)))
                  .andExpect(status().isBadRequest())
                  .andExpect(jsonPath("$.message").value("Username cannot be blank"));
      }

      @Test
      void changePassword_ShouldReturnOkStatus() throws Exception {
          Long userId = 1L;
          ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("oldPassword", "newPassword", "newPassword");
  
          when(userService.changePassword(userId, changePasswordDTO)).thenReturn(null);  

          this.mockMvc.perform(put("/user/password")
                  .param("userId", String.valueOf(userId))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(changePasswordDTO)))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.message").value("Password changed successfully."));
      }
  
      @Test
      void changePassword_ShouldReturnBadRequest_WhenPasswordsDoNotMatch() throws Exception {
          Long userId = 1L;
          ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("oldPassword", "newPassword", "differentPassword");  // Passwords do not match
  
          this.mockMvc.perform(put("/user/password")
                  .param("userId", String.valueOf(userId))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(changePasswordDTO)))
                  .andExpect(status().isBadRequest())
                  .andExpect(jsonPath("$.message").value("Passwords do not match"));
      }
  
      @Test
      void changeDisplayName_ShouldReturnOkStatus() throws Exception {
          Long userId = 1L;
          ChangeDisplayNameDTO changeDisplayNameDTO = new ChangeDisplayNameDTO("newDisplayName", "password");
  
          when(userService.changeDisplayName(userId, changeDisplayNameDTO)).thenReturn(null);  // Assuming void method
          
          this.mockMvc.perform(put("/user/displayname")
                  .param("userId", String.valueOf(userId))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(changeDisplayNameDTO)))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.message").value("Display name changed successfully to: newDisplayName"));
      }
  
      @Test
      void changeDisplayName_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
          Long userId = 1L;
          ChangeDisplayNameDTO changeDisplayNameDTO = new ChangeDisplayNameDTO("", "password");  // Invalid display name

          this.mockMvc.perform(put("/user/displayname")
                  .param("userId", String.valueOf(userId))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(changeDisplayNameDTO)))
                  .andExpect(status().isBadRequest())
                  .andExpect(jsonPath("$.message").value("Display name cannot be blank"));
      }
}
