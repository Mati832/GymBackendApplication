# Gym Backend Application

## Getting Started

### 1. Build the application
This command builds the application and runs all unit and integration tests.
```bash
./mvnw clean package
```
### 2. Start the container (Docker)
To start the backend in a containerized environment:
```bash
docker-compose up -d --build
```
The API will be available at http://localhost:8080


## Running Tests
```bash
./mvnw verify
```



