# Lex_Events_Backend
A secure backend API for an event sharing application built with Spring Boot.

## 🚀 Features

- **JWT-based Authentication**: Secure user login and logout.
- **Stateless Session Management**: Uses HTTP-only cookies for secure JWT storage.
- **User Registration & Management**: Endpoints for creating and managing user accounts.
- **CRUD operations for Events**: GET, POST, UPDATE, DELETE operations
- **Password encryption with BCrypt**
- **PostgreSQL Integration**: Robust and reliable relational database.
- **OpenAPI DOCS**: Open api docs 

## 🛠️ Tech Stack

- **Backend Framework**: Spring Boot
- **Language**: Java
- **Security**: Spring Security, JWT (JJWT library)
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Containerization**: Docker 

## 📋 Prerequisites

Before you begin, ensure you have the following installed on your machine:

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- (Optional) Docker

## ⚙️ Installation & Setup

Follow these steps to get your development environment running.

1.  **Clone the repository**
    ```bash
    git clone https://github.com/rameshpokharel21/lex-events-backend.git
    cd lex-events-backend
    ```

2.  **Configure Database**
    - Create a new PostgreSQL database "your_db_name"
    - Update the `src/main/resources/application.yml` file with your database credentials.


3.  **Run the Application**

    You can run the app using your IDE or the command line.  
    **Using Maven:**: `./mvnw spring-boot:run`  
    The API server will start at `http://localhost:9000`.

## 🔑 API Usage & Authentication

This API uses JWT for authentication. The token is automatically set as an HTTP-only cookie upon successful login.

1. Register a New User

    **POST**: `/api/auth/register`  
    **Request Body:**
    ```json
    {
        "username": "john_doe",
        "email": "john@example.com",
        "password": "myPassword123"
    }
    ```
2. Login

    `POST /api/auth/login`  
        Request Body:  
    ```json
        {
            "username": "john_doe",
            "password": "myPassword123"
        }
    ```
    Response: Upon success, a JWT token will be set in an HttpOnly cookie named jwt. Subsequent requests to protected endpoints will automatically include this cookie.  

3. Access a Protected Endpoint
Simply make a request to any protected endpoint (e.g., GET /api/users/me). The server will validate the JWT from the cookie.

4. Logout
`POST /api/auth/logout`  
Response: Clears the JWT cookie, effectively logging the user out.

📁 Project Structure text

        src/main/java/com/ramesh/lex_events/
        
        ├── config/          # Security & Web Configuration
        ├── controllers/     # REST API Controllers
        ├── dto/             # Data Transfer Objects (Requests/Responses)
        ├── models/          # JPA Entities (User, Post, etc.)
        ├── repositories/    # Spring Data JPA Repositories
        ├── security/        # JWT & UserDetailsService logic
        ├── service/         # Business Logic Layer
        ├── util/            # Utilities (e.g., JWT Utility class)
        ├── mapper/          # Converting JPA entities to DTOs
        └── validator/       # Validations
        
🔒 Environment Variables

For production, it's best to use environment variables instead of the application.properties file.

| Variable Name     | Description                             | Example                                      |
|-------------------|-----------------------------------------|----------------------------------------------|
| DB_URL            | PostgreSQL JDBC URL                     | `jdbc:postgresql://host:port/db`             |
| DB_USERNAME       | Database Username                       | `user`                                       |
| DB_PASSWORD       | Database Password                       | `pass`                                       |
| JWT_SECRET        | Secret key for signing JWTs             | `yourSuperSecretKey`                         |
| JWT_EXPIRATION    | JWT Expiration in Milliseconds          | `86400000` (24×60×60×1000)                   |
| JWT_COOKIE        | Cookie name                             | `some_cookie_name`                           |
| ADMIN_USERNAME    | Sample Admin username                   | `admin`                                      |
| ADMIN_PASSWORD    | Sample Admin password                   | `admin123`                                   |
| ADMIN_EMAIL       | Admin email address                     | `admin@example.com`                          |
| USER_USERNAME     | Sample User username                    | `user1`                                      |
| USER_PASSWORD     | Sample User password                    | `user123`                                    |
| USER_EMAIL        | User email address                      | `user@example.com`                           |
| BREVO_API_URL     | Brevo API URL                           | `https://api.brevo.com/v3`                   |
| BREVO_API_KEY     | Brevo API key for this app              | `your brevo_app_key`                         |
| SENDER_EMAIL      | Email registered in Brevo               | `your email or domain email`                 |
| ALLOWED_ORIGINS   | Frontend URL or localhost list          | `http://localhost:5173,http://localhost:3000`|