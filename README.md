```markdown
# User Management and Security Application

## Description

This application provides a robust solution for managing user security, authentication, user management, and role management. It includes features for user registration, role assignment, password management, and security verification tasks, leveraging modern technologies like Java 21, Spring Boot, Spring Security, and Java-JWT.

## Features

- **User Authentication**: Secure user login with username and password using JWT.
- **User Management**: Create, update, delete, and retrieve user information.
- **Role Management**: Assign and remove roles from users.
- **Password Management**: Update user passwords.
- **Verification Management**: Handle verification codes with expiration and reset functionalities.
- **Scheduled Tasks**: Periodically clean up expired verifications.

## Technologies Used

- **Java 21**: The programming language.
- **Spring Boot**: For building the RESTful API and managing application configuration.
- **Spring Security**: For authentication and authorization.
- **Java-JWT**: For creating and validating JSON Web Tokens.
- **Spring Data JPA**: For database access.
- **H2 Database**: In-memory database for development and testing (configurable to other databases like MySQL or PostgreSQL).
- **Lombok**: For reducing boilerplate code.

## Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven** or **Gradle** (for building the project)
- **IDE**: Optional, but IntelliJ IDEA or Eclipse is recommended

### Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/user-management-security-app.git
   cd user-management-security-app
   ```

2. **Build the Project**

   Using Maven:
   ```bash
   mvn clean install
   ```

   Using Gradle:
   ```bash
   ./gradlew build
   ```

3. **Run the Application**

   Using Maven:
   ```bash
   mvn spring-boot:run
   ```

   Using Gradle:
   ```bash
   ./gradlew bootRun
   ```

   Alternatively, you can run the JAR file generated in the `target` (for Maven) or `build/libs` (for Gradle) directory:

   ```bash
   java -jar target/user-management-security-app-0.0.1-SNAPSHOT.jar
   ```

### Configuration

- **Application Properties**: Configuration settings are found in `src/main/resources/application.properties`. Modify database settings, server port, and other configurations as needed.

  ```properties
  # Example application.properties
  server.port=8080
  spring.datasource.url=jdbc:h2:mem:testdb
  spring.datasource.username=sa
  spring.datasource.password=password
  spring.jpa.hibernate.ddl-auto=update
  spring.security.jwt.secret=your-jwt-secret
  ```

  Make sure to replace `your-jwt-secret` with a strong secret key for signing JWT tokens.

### API Endpoints

- **User Management**
    - `POST /users` - Create a new user
    - `GET /users/{id}` - Get user by ID
    - `PUT /users/{id}` - Update user details
    - `DELETE /users/{id}` - Delete user by ID
    - `GET /users` - Get all users

- **Role Management**
    - `POST /roles` - Create a new role
    - `GET /roles/{id}` - Get role by ID
    - `PUT /roles/{id}` - Update role details
    - `DELETE /roles/{id}` - Delete role by ID
    - `GET /roles` - Get all roles

- **Authentication**
    - `POST /login` - Authenticate a user and return a JWT token

- **Password Management**
    - `PUT /users/{username}/password` - Update user password

- **Verification Management**
    - `POST /verifications/request` - Request a new verification code
    - `POST /verifications/reset-password` - Reset user password with a verification code

### Scheduled Tasks

- **Verification Cleanup**: Runs every 10 minutes to remove expired verifications. Configured in the `MyScheduledTask` class.

### Security Configuration

- **Spring Security**: Configured for user authentication and role-based access control.
- **JWT Authentication**: Tokens are generated using Java-JWT and validated on each request to secure endpoints.

### Testing

- **Unit Tests**: Located in `src/test/java`. Run tests with Maven or Gradle.

  Using Maven:
  ```bash
  mvn test
  ```

  Using Gradle:
  ```bash
  ./gradlew test
  ```

- **Integration Tests**: Ensure the API endpoints and services work correctly with the database.

## Contributing

1. Fork the repository
2. Create a new branch (`git checkout -b feature-branch`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature-branch`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

```
