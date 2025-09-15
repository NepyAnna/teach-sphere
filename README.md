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

### Clone from DockerHub
```bash
docker pull sheoanna/teach-sphere-app:v1
```

### Run project in container 
- In order to run the project you need to fill in all the variables in the .env file as specified in .env.example. and also docker must be installed and running

```bash
docker-compose up -d --build 
```
### Stop container

```bash
docker-compose down --volumes --remove-orphans
```
### Run tests in container

```bash
docker-compose -f docker-compose-test.yml run --rm teach-sphere-test ./mvnw test 
```
- You should see :
- 
  [![temp-Image-HTqz-G1.avif](https://i.postimg.cc/hPgz2mmn/temp-Image-HTqz-G1.avif)](https://postimg.cc/jCgS5Lv3)

## CI/CD with GitHub Actions
This project uses GitHub Actions to automate the software lifecycle: testing, building, and releasing Docker images.

#### Workflows
We created three separate workflows inside .github/workflows/:
* test.yml – Runs automated tests on pull requests before merging into main.
* build.yml – Builds and pushes Docker images to Docker Hub whenever new code is pushed to the main branch.
* release.yml – Creates production-ready Docker images and tags them when a version tag (e.g., v1.0.0) is pushed.

#### Execution
* Test workflow: triggered automatically on every Pull Request → main.
* Build workflow: triggered automatically on every push to main.
* Release workflow: triggered automatically when a tag starting with v is pushed (e.g., git tag v1.0.0 && git push origin v1.0.0).
Screenshots of each workflow execution can be found in the Actions tab of this repository.

## API Endpoints

### Registration / Login
- POST http://localhost:8080/api/registar registration of users
- POST http://localhost:8080/api/login for login
- POST http://localhost:8080/api/refresh for refresh token
- POST http://localhost:8080/api/logout for logout

### User
- GET http://localhost:8080/api/users to get all users (only for ADMIN)
- GET http://localhost:8080/api/users/{id} to get user by ID
- PUT http://localhost:8080/api/users/{id} to update user by ID
- DELETE http://localhost:8080/api/users/{id} to delete user by ID

### Profile
- GET http://localhost:8080/api/profiles to get all profiles (only for ADMIN)
- GET http://localhost:8080/api/profiles/{id} to get profile by ID
- POST http://localhost:8080/api/profiles to create profile
- PUT http://localhost:8080/api/profiles/{id} to update profile by ID
- DELETE http://localhost:8080/api/profiles/{id} to delete profile by ID

### Category
- GET http://localhost:8080/api/categories to get all categories
- GET http://localhost:8080/api/categories/{id} to get category by ID
- POST http://localhost:8080/api/categories to create category(only for ADMIN)
- PUT http://localhost:8080/api/categories/{id} to update category by ID(only for ADMIN)
- DELETE http://localhost:8080/api/categories/{id} to delete category by ID(only for ADMIN)

### Subject
- GET http://localhost:8080/api/subjects to get all subjects
- GET http://localhost:8080/api/subjects/{id} to get subject by ID
- POST http://localhost:8080/api/subjects to create subject(only for ADMIN)
- PUT http://localhost:8080/api/subjects/{id} to update subject by ID(only for ADMIN)
- DELETE http://localhost:8080/api/subjects/{id} to delete subject by ID(only for ADMIN)

### Mentor Subject
- GET http://localhost:8080/api/mentor_subjects to get all mentor subjects
- GET http://localhost:8080/api/mentor_subjects/{id} to get mentor subject by ID
- POST http://localhost:8080/api/mentor_subjects to create mentor subject(only for MENTOR)
- PUT http://localhost:8080/api/mentor_subjects/{id} to update mentor subject by ID(only for MENTOR)
- DELETE http://localhost:8080/api/mentor_subjects/{id} to delete mentor subject by ID(only for MENTOR)

### Review
- GET http://localhost:8080/api/mentor_subject_reviews to get all mentor subject reviews
- GET http://localhost:8080/api/mentor_subject_reviews/{id} to get mentor subject review by ID
- POST http://localhost:8080/api/mentor_subject_reviews to create mentor subject review(only for STUDENT)
- PUT http://localhost:8080/api/mentor_subject_reviews/{id} to update mentor subject review by ID(only for STUDENT)
- DELETE http://localhost:8080/api/mentor_subject_reviews/{id} to delete mentor subject review by ID(only for STUDENT)

### Session Request
- GET http://localhost:8080/api/session_requests/student to get all session request for STUDENT
- - GET http://localhost:8080/api/session_requests/mentor to get all session request for MENTOR
- POST http://localhost:8080/api/session_requests to create session request (only for STUDENT)
- PUT http://localhost:8080/api/session_requests/1/status to update status of session request (only for MENTOR)

## Running Tests

[![temp-Image-Euo-Fi3.avif](https://i.postimg.cc/TYGyRt2L/temp-Image-Euo-Fi3.avif)](https://postimg.cc/jCgqcQ7t)

## EER Diagram

[![temp-Imagebj7e-I8.avif](https://i.postimg.cc/TYGNwn19/temp-Imagebj7e-I8.avif)](https://postimg.cc/YjnzDG1G)

## Chat Flow Diagram

[![temp-Image-CZjqb-H.avif](https://i.postimg.cc/d0dGrSST/temp-Image-CZjqb-H.avif)](https://postimg.cc/QB8BrSDX)

## Contributors

Anna Nepyivoda
    <a href="https://github.com/NepyAnna">
        <picture>
            <source srcset="https://img.icons8.com/ios-glyphs/30/ffffff/github.png" media="(prefers-color-scheme: dark)">
            <source srcset="https://img.icons8.com/ios-glyphs/30/000000/github.png" media="(prefers-color-scheme: light)">
            <img src="https://img.icons8.com/ios-glyphs/30/000000/github.png" alt="GitHub icon"/>
        </picture>
    </a>