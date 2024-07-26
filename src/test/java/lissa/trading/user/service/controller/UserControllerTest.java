package lissa.trading.user.service.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import lissa.trading.user.service.dto.UserPostDto;
import lissa.trading.user.service.dto.UserResponseDto;
import lissa.trading.user.service.exception.UserNotFoundException;
import lissa.trading.user.service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserPostDto getUserPostDto() {
        return new UserPostDto(
                "John",
                "Doe",
                123456789L,
                "john_doe",
                "tinkoff-token-123",
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(2.5),
                BigDecimal.valueOf(25.00),
                2,
                true,
                "metrics",
                "tariff"
        );
    }

    private UserResponseDto getUserResponseDto(UUID externalId) {
        return new UserResponseDto(
                externalId,
                "John",
                "Doe",
                123456789L,
                "john_doe",
                "tinkoff-token-123",
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(2.5),
                BigDecimal.valueOf(25.00),
                2,
                true,
                "metrics",
                "tariff"
        );
    }

    @BeforeEach
    public void setup() {
        objectMapper.findAndRegisterModules();
    }

    @Test
    public void testCreateUser() throws Exception {
        UserPostDto userPostDto = getUserPostDto();
        UUID externalId = UUID.randomUUID();
        UserResponseDto userResponseDto = getUserResponseDto(externalId);

        when(userService.createUser(any(UserPostDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId", equalTo(externalId.toString())))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserPostDto userUpdates = getUserPostDto();
        userUpdates.setFirstName("Jane");
        userUpdates.setLastName("Smith");

        UUID externalId = UUID.randomUUID();
        UserResponseDto updatedUser = getUserResponseDto(externalId);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");

        when(userService.updateUser(any(UUID.class), any(UserPostDto.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/" + externalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value(externalId.toString()))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    public void testBlockUserByTelegramNickname() throws Exception {
        mockMvc.perform(post("/api/users/block/john_doe"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUserByExternalId() throws Exception {
        UUID externalId = UUID.randomUUID();
        mockMvc.perform(delete("/api/users/" + externalId))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserByExternalId() throws Exception {
        UUID externalId = UUID.randomUUID();
        UserResponseDto userResponseDto = getUserResponseDto(externalId);
        when(userService.getUserByExternalId(any(UUID.class))).thenReturn(userResponseDto);

        mockMvc.perform(get("/api/users/" + externalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value(externalId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void testGetUserByExternalIdNotFound() throws Exception {
        UUID externalId = UUID.randomUUID();
        when(userService.getUserByExternalId(any(UUID.class))).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/" + externalId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    public void testGetUsersWithPaginationAndFilters() throws Exception {
        UUID externalId = UUID.randomUUID();
        UserResponseDto userResponseDto = getUserResponseDto(externalId);
        Page<UserResponseDto> users = new PageImpl<>(Collections.singletonList(userResponseDto), PageRequest.of(0, 10), 1);
        when(userService.getUsersWithPaginationAndFilters(any(PageRequest.class), anyString(), anyString())).thenReturn(users);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].externalId", equalTo(externalId.toString())))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"));
    }

    @Test
    public void testGetUsersWithPaginationAndFiltersEmpty() throws Exception {
        Page<UserResponseDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(userService.getUsersWithPaginationAndFilters(any(PageRequest.class), anyString(), anyString())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("firstName", "NonExistingFirstName")
                        .param("lastName", "NonExistingLastName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }
}