# Teach Sphere

TeachSphere is a web application that allows users to register as students or mentors, create personalized profiles, search for mentors by category and subject, and manage mentorship sessions.
The platform includes key features such as secure authentication using JWT, role-based access control, filtered mentor search, session requests with status tracking, a review system, and optional internal messaging.
The backend is built with Spring Boot, following best development practices, a modular architecture, automated testing, and modern tools like Docker for containerization, GitHub Actions for CI/CD, and Cloudinary for image management.

## Main Feature

### Authentication & Authorization with Asymmetric Keys and Redis Blacklist

This project implements secure JWT-based authentication and authorization using RSA asymmetric keys for signing and verifying tokens.
It supports both access and refresh tokens, with an additional mechanism to invalidate tokens before their natural expiration by maintaining a blacklist in Redis.

#### Key Features:
- Asymmetric cryptography (RSA) — Access and refresh tokens are signed with a private key and verified with a public key, ensuring high security without exposing the signing key to the server that only validates tokens.
- Access & Refresh Tokens — Short-lived access tokens for API requests, long-lived refresh tokens for reissuing access tokens without re-login.
- Token Blacklisting — When a user logs out or a refresh token is rotated, the old token is stored in Redis with a TTL equal to its remaining lifetime.

##### Redis as a Blacklist Store:
- Fast lookups: Redis is in-memory, enabling O(1) checks for token invalidation.
- Automatic expiration: Tokens are stored with a TTL, so expired entries are removed automatically, avoiding manual cleanup.
- Distributed environment friendly: Works seamlessly in multi-instance deployments where in-memory maps wouldn’t sync across nodes.

##### Flow:
- Login — User provides credentials → Server issues access & refresh tokens.
- Requests — Access token is sent via Authorization: Bearer <token> header and validated against RSA public key + Redis blacklist check.
- Refresh — Refresh token rotates a new pair of tokens, invalidating the old refresh token in Redis.
- Logout — Both access and refresh tokens are blacklisted in Redis until they expire.


## Technologies Used
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![draw.io](https://img.shields.io/badge/draw.io-F08705?style=for-the-badge&logo=diagramsdotnet&logoColor=white)
![Cloudinary](https://img.shields.io/badge/cloudinary-3448C5?style=for-the-badge&logo=cloudinary&logoColor=white)
![Swagger](https://img.shields.io/badge/swagger-%2385EA2D.svg?style=for-the-badge&logo=swagger&logoColor=black)

## Clone the Repository

```bash
git clone https://github.com/NepyAnna/teach-sphere.git
cd teach-sphere
```
### Run

```bash
./mvnw spring-boot:run
```
or
```bash
mvn spring-boot:run
```
Alternative Way to Run the Application
If you are using an IDE such as IntelliJ IDEA,VS Code etc, you can simply click the “Run” button or run the main application class directly (the one annotated with @SpringBootApplication).
For example, in IntelliJ IDEA, right-click the main class and choose "Run 'TeachSphereApplication...main()'".

## API Endpoints


### Registration / Login


### User

### Profile

### Category

### Subject

### Session Request

### Review


## Running Tests

## EER Diagram

## Chat Flow Diagram

## Class Diagram

## Contributors

Anna Nepyivoda
    <a href="https://github.com/NepyAnna">
        <picture>
            <source srcset="https://img.icons8.com/ios-glyphs/30/ffffff/github.png" media="(prefers-color-scheme: dark)">
            <source srcset="https://img.icons8.com/ios-glyphs/30/000000/github.png" media="(prefers-color-scheme: light)">
            <img src="https://img.icons8.com/ios-glyphs/30/000000/github.png" alt="GitHub icon"/>
        </picture>
    </a>