package com.uniquindio.api.controller;


import com.uniquindio.api.model.User;
import com.uniquindio.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Listar usuarios
     * @return Lista de usuarios
     */
    @GetMapping
    public List<User> getAllUsers() {
        logger.info("Obteniendo la lista de todos los usuarios.");
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            logger.info("No se encontraron usuarios en la base de datos.");
        } else {
            users.forEach(user -> logger.info("Usuario encontrado: ID = {}, Nombre = {}, Email = {}",
                    user.getId(), user.getName(), user.getEmail()));
        }

        return users;
    }

    /**
     * Buscar usuario por ID
     * @param id del usuario
     * @return Usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Buscando usuario con ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> {
            logger.warn("Usuario con ID: {} no encontrado.", id);
            return ResponseEntity.notFound().build();
        });
    }

    /**
     * Crear un nuevo usuario
     * @param user Usuario a crear
     * @return Usuario creado
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.info("Iniciando creación de un nuevo usuario...");
        User savedUser = userRepository.save(user);
        logger.info("Usuario creado con éxito: ID {}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    /**
     * Actualizar un usuario específico
     * @param id Id del usuario a actualizar
     * @param userDetails Nuevos datos para el cambio
     * @return Información del usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        logger.info("Intentando actualizar usuario con ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            userRepository.save(user);
            logger.info("Usuario con ID: {} actualizado correctamente.", id);
            return ResponseEntity.ok(user);
        } else {
            logger.warn("Usuario con ID: {} no encontrado para actualización.", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar un usuario por ID
     * @param id Id del usuario a eliminar
     * @return Respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Intentando eliminar usuario con ID: {}", id);
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.delete(user.get());
            logger.info("Usuario con ID: {} eliminado correctamente.", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Usuario con ID: {} no encontrado para eliminación.", id);
            return ResponseEntity.notFound().build();
        }
    }
}