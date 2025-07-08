package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.otus.hw.dto.AppUserDto;
import ru.otus.hw.mapper.AppUserMapper;
import ru.otus.hw.models.AppUser;
import ru.otus.hw.models.Role;
import ru.otus.hw.repositories.AppUserRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({AppUserServiceImpl.class, AppUserMapper.class})
class AppUserServiceImplTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserServiceImpl appUserService;

    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("testuser");
        appUser.setPassword("password");
        appUser.setRoles(Set.of(Role.USER, Role.ADMIN));
        appUserRepository.save(appUser);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        UserDetails userDetails = appUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> appUserService.loadUserByUsername("unknown"));

        assertEquals("User not found: unknown", exception.getMessage());
    }

    @Test
    void findByUsername_ShouldReturnAppUserDto_WhenUserExists() {
        AppUserDto result = appUserService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.username());
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserNotFound() {
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> appUserService.findByUsername("unknown"));

        assertEquals("User not found: unknown", exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnListOfAppUserDto() {
        List<AppUserDto> result = appUserService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).username());
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoUsers() {
        appUserRepository.deleteAll();

        List<AppUserDto> result = appUserService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}