package com.example.SoftbinatorProject.services;

import com.example.SoftbinatorProject.dtos.UserInfoDto;
import com.example.SoftbinatorProject.models.User;
import com.example.SoftbinatorProject.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@DataJpaTest
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private KeycloakAdminService keycloakAdminService;
    @InjectMocks
    private UserService userService;

    @Test
    void getUsers() {
        //given
        UserInfoDto firstUser = UserInfoDto.builder().username("firstUser").email("firstEmail").build();
        UserInfoDto secondUser = UserInfoDto.builder().username("secondUser").email("secondUser").build();
        List<UserInfoDto> userList = List.of(firstUser, secondUser);
        when(userRepository.getUserDtos()).thenReturn(userList);
        //when
        List<UserInfoDto> result = userService.getUsers();
        //then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(userList, result);
        verify(userRepository).getUserDtos();
    }
}
