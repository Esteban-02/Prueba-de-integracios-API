package com.uniquindio.api;

import com.uniquindio.api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/users";
    }

    @Test
    public void testAddUser() {
        User user = new User("John Doe", "john@example.com");
        ResponseEntity<User> response = restTemplate.postForEntity(getBaseUrl(), user, User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("John Doe");
    }

    @Test
    public void testGetUserById() {
        // Crear un usuario primero
        User user = new User("Jane Doe", "jane@example.com");
        ResponseEntity<User> createResponse = restTemplate.postForEntity(getBaseUrl(), user, User.class);

        Long userId = createResponse.getBody().getId();

        // Obtener el usuario por ID
        ResponseEntity<User> response = restTemplate.getForEntity(getBaseUrl() + "/" + userId, User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(userId);
        assertThat(response.getBody().getName()).isEqualTo("Jane Doe");
    }

    @Test
    public void testUpdateUser() {
        // Crear un usuario primero
        User user = new User("Jane Doe", "janee@example.com");
        ResponseEntity<User> createResponse = restTemplate.postForEntity(getBaseUrl(), user, User.class);

        Long userId = createResponse.getBody().getId();

        // Actualizar el usuario
        User updatedUser = new User("Jane Smith", "jane.smith@example.com");
        HttpEntity<User> requestUpdate = new HttpEntity<>(updatedUser);
        ResponseEntity<User> response = restTemplate.exchange(getBaseUrl() + "/" + userId, HttpMethod.PUT, requestUpdate, User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Jane Smith");
        assertThat(response.getBody().getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    public void testDeleteUser() {
        // Crear un usuario primero
        User user = new User("John Smith", "john.smith@example.com");
        ResponseEntity<User> createResponse = restTemplate.postForEntity(getBaseUrl(), user, User.class);
        System.out.println(user.toString());
        Long userId = createResponse.getBody().getId();

        // Eliminar el usuario
        restTemplate.delete(getBaseUrl() + "/" + userId);

        // Verificar que el usuario fue eliminado
        ResponseEntity<User> response = restTemplate.getForEntity(getBaseUrl() + "/" + userId, User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetAllUsers() {
        // Limpiar la base de datos y agregar dos usuarios
        restTemplate.delete(getBaseUrl() + "/clear");

        User user1 = new User("Alice", "alice@example.com");
        User user2 = new User("Bob", "bob@example.com");
        restTemplate.postForEntity(getBaseUrl(), user1, User.class);
        restTemplate.postForEntity(getBaseUrl(), user2, User.class);

        // Obtener todos los usuarios
        ResponseEntity<User[]> response = restTemplate.getForEntity(getBaseUrl(), User[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);
    }
}
