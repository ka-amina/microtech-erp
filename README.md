# SmartShop - Backend API

SmartShop is a B2B commercial management web application backend for MicroTech Maroc, a Casablanca-based IT equipment distributor. The application provides a REST API to manage clients, products, orders, and payments, featuring a progressive loyalty system and split payment capabilities.

This is a backend-only application. All interactions and tests are intended to be performed via an API testing tool like Postman or Swagger.

## Features

- **Client Management**: CRUD operations for clients, tracking of order history, and cumulative spending.
- **Automated Loyalty System**: A tier-based loyalty system (BASIC, SILVER, GOLD, PLATINUM) that updates automatically based on client's order history and total spending.
- **Product Management**: CRUD operations for products with support for soft-deletes.
- **Order Management**: Create and manage multi-product orders with automatic calculations for discounts, VAT, and totals.
- **Multi-Method Payments**: Supports split payments for a single order via Cash, Check, or Bank Transfer.
- **Role-Based Access**: Simple role management for ADMIN and CLIENT users using HTTP Sessions.
- **Centralized Exception Handling**: Clear and consistent error responses for better API client integration.

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - For database interaction
- [PostgreSQL](https://www.postgresql.org/) - Relational database
- [Maven](https://maven.apache.org/) - Dependency Management
- [Lombok](https://projectlombok.org/) - To reduce boilerplate code
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

- JDK 17 or later
- Maven 3.2+
- A running instance of PostgreSQL

### Installation & Configuration

1.  **Clone the repository**
    ```sh
    git clone <your-repository-url>
    cd demo
    ```

2.  **Configure the database**
    Open `src/main/resources/application.properties` and update the database connection details:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```

3.  **Build the project**
    Use Maven to build the project and install dependencies:
    ```sh
    mvn clean install
    ```

### Running the Application

You can run the application using the Spring Boot Maven plugin:

```sh
mvn spring-boot:run
```
The application will start on `http://localhost:8080`.

## API Endpoints

Here is a summary of the available API endpoints.

### Authentication (`/api/auth`)
- `POST /register`: Register a new user (client or admin).
- `POST /login`: Log in a user and establish a session.
- `POST /logout`: Log out the current user.
- `GET /me`: Get details of the currently authenticated user.

### Clients (`/api/clients`)
- `POST /`: Create a new client.
- `GET /`: Get a list of all clients.
- `GET /{id}`: Get a specific client by their ID.
- `PUT /{id}`: Update a client's information.
- `DELETE /{id}`: Delete a client.

### Products (`/api/products`)
- `POST /`: Add a new product.
- `GET /`: Get a paginated list of all products. Supports sorting.
- `GET /{id}`: Get a specific product by its ID.
- `PUT /{id}`: Update a product's details.
- `DELETE /{id}`: Soft-delete a product.

### Orders (`/api/orders`)
- `POST /`: Create a new order.
- `GET /client/{clientId}`: Get the order history for a specific client.
- `PATCH /{orderId}/confirm`: Confirm a fully paid order (Admin only).
- `PATCH /{orderId}/cancel`: Cancel a pending order (Admin only).
- `PATCH /{orderId}/reject`: Reject a pending order (Admin only).

### Payments (`/api/orders/{orderId}/payments`)
- `POST /`: Add a payment to a specific order.
- `PATCH /payments/{paymentId}/status`: Update the status of a payment (e.g., from `PENDING` to `CASHED`).

## Data Model

The core data models of the application include:
- **User**: Represents an authenticated user (ADMIN or CLIENT).
- **Client**: Stores information about B2B customers and their loyalty tier.
- **Product**: Represents items available for purchase.
- **Order**: Contains details of a transaction, including items, discounts, and status.
- **OrderItem**: A line item within an order, linking a product with a quantity.
- **Payment**: Records a payment made towards an order.

## Project Structure

The project follows a standard layered architecture:

- `src/main/java/org/example/demo/`
  - `controller/`: REST API controllers for handling HTTP requests.
  - `service/`: Contains the business logic.
  - `repository/`: Spring Data JPA repositories for database interaction.
  - `model/`: JPA entities representing the database tables.
  - `dto/`: Data Transfer Objects for API requests and responses.
  - `mappers/`: MapStruct mappers for converting between entities and DTOs.
  - `exception/`: Custom exception classes and a global exception handler.
  - `enums/`: Enumerations for roles, statuses, etc.

## Testing

To run the test suite, use the following Maven command:

```sh
mvn test
```