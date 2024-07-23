package fr.projet.diginamic.backend.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.projet.diginamic.backend.dtos.UserDto;
import fr.projet.diginamic.backend.entities.UserEntity;
import fr.projet.diginamic.backend.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/** User controller */
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "User API endpoint")
public class UserController {
    @Autowired
    UserService userService;

    /** Get all users */
    @GetMapping
    public ResponseEntity<List<UserDto>> all() {
        List<UserEntity> users = userService.getAll();
        List<UserDto> usersDto = new ArrayList<>();

        for (UserEntity user : users) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setBirthDate(user.getBirthDate());
            userDto.setEmail(user.getEmail());
            userDto.setRole(user.getRole().getType());

            usersDto.add(userDto);
        }

        return ResponseEntity.ok(usersDto);
    }

    /**
     * Get one user
     * 
     * @param id - the id of the user
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> one(@PathVariable Long id) {
        UserEntity user = userService.getOne(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setBirthDate(user.getBirthDate());
        userDto.setEmail(user.getEmail());
        return ResponseEntity.ok(userDto);
    }

    /**
     * Get one user by email
     * 
     * @param email - the email of the user
     */
    @GetMapping("/one")
    public ResponseEntity<UserDto> oneByEmail(@PathVariable String email) {
        UserEntity user = userService.getOneByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setBirthDate(user.getBirthDate());
        userDto.setEmail(user.getEmail());
        return ResponseEntity.ok(userDto);
    }

    /**
     * Update one user
     * 
     * @param id      - the id of the user
     * @param userDto - the dto of the user
     */
    @PutMapping("/{id}")
    public ResponseEntity<List<UserDto>> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        List<UserEntity> users = userService.update(id, userDto);
        List<UserDto> usersDto = new ArrayList<>();

        for (UserEntity user : users) {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setBirthDate(user.getBirthDate());
            dto.setEmail(user.getEmail());
            usersDto.add(dto);
        }

        return ResponseEntity.ok(usersDto);
    }

    /**
     * Delete one user
     * 
     * @param id - the id of the user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<List<UserDto>> delete(@PathVariable Long id) {
        List<UserEntity> users = userService.delete(id);
        List<UserDto> usersDto = new ArrayList<>();

        if (users == null) {
            return ResponseEntity.notFound().build();
        }

        for (UserEntity user : users) {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setBirthDate(user.getBirthDate());
            dto.setEmail(user.getEmail());
            usersDto.add(dto);
        }
        return ResponseEntity.ok(usersDto);
    }
}
