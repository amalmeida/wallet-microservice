# Wallet Microservice

A REST microservice built with Spring Boot to manage user wallets, implemented with Domain-Driven Design (DDD), following SOLID principles and Clean Code practices.

## Features

### Functional Requirements
- **CreateWallet**: Creates a wallet for a user (`POST /wallets`).
- **RetrieveBalance**: Retrieves the current balance of a wallet (`GET /wallets/{walletId}/balance`).
- **RetrieveHistoricalBalance**: Retrieves the balance at a specific point in the past (`GET /wallets/{walletId}/historical-balance`).
- **DepositFunds**: Allows deposits into a wallet (`POST /wallets/{walletId}/deposit`).
- **WithdrawFunds**: Allows withdrawals from a wallet (`POST /wallets/{walletId}/withdraw`).
- **TransferFunds**: Facilitates transfers between wallets (`POST /wallets/transfer`).

### Non-Functional Requirements
- **Critical Mission**: High availability and consistency are prioritized using pessimistic locks in the database.
- **Traceability**: All operations are recorded as transactions for auditing purposes.

## Installation & Execution

### Prerequisites
- Java 21
- Maven
- Docker (optional, for running in a container)

### Local Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/amalmeida/wallet-microservice.git
   cd wallet-microservice
   ```
2. Compile and package the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the H2 console (optional):
    - URL: http://localhost:8080/h2-console
    - JDBC URL: jdbc:h2:mem:walletdb

### Running with Docker
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/wallet-microservice.git
   cd wallet-microservice
   ```
2. Build the Docker image:
   ```bash
   docker build -t wallet-microservice .
   ```
3. Run the container:
   ```bash
   docker run -p 8080:8080 wallet-microservice
   ```
4. The application will be available at http://localhost:8080.

## API Endpoints

### Create Wallet
```
POST /wallets?userId={userId}
```
Creates a wallet and returns its ID.

### Get Current Balance
```
GET /wallets/{walletId}/balance
```
Retrieves the current balance of the wallet.

### Get Historical Balance
```
GET /wallets/{walletId}/historical-balance?at={dateTime}
```
Returns the balance at a specific point in the past (ISO format, e.g., 2025-04-01T10:00:00).

### Deposit Funds
```
POST /wallets/{walletId}/deposit?amount={amount}
```
Deposits funds. Requires header **Idempotency-Key** (UUID).

### Withdraw Funds
```
POST /wallets/{walletId}/withdraw?amount={amount}
```
Withdraws funds. Requires header **Idempotency-Key** (UUID).

### Transfer Funds
```
POST /wallets/transfer?fromWalletId={from}&toWalletId={to}&amount={amount}
```
Transfers funds. Requires header **Idempotency-Key** (UUID).

## Design Choices

The design follows widely accepted patterns to ensure quality and maintainability.

### Patterns Used

#### DDD (Domain-Driven Design)
- **Wallet** is the aggregate root, encapsulating business rules (deposits, withdrawals).
- **Transaction** is a value object used to track operations.
- **WalletRepository** defines the persistence interface within the domain.

#### SOLID Principles
- **Single Responsibility**: Each class has a single responsibility (e.g., Wallet only manages balances).
- **Open/Closed**: Extensible via new transactions without modifying existing code.
- **Liskov Substitution**: Not directly applicable here.
- **Interface Segregation**: Small, specific interfaces (e.g., WalletRepository).
- **Dependency Inversion**: Dependencies are injected via interfaces (e.g., WalletService uses WalletRepository).

#### Clean Code
- Descriptive names (e.g., depositFunds, retrieveHistoricalBalance).
- Small, focused functions.
- Clear separation of layers (domain, application, infrastructure).

### Technical Choices
- **Java 21**: Uses `record` for Transaction, ensuring immutability.
- **Spring Boot**: A robust and widely used REST framework.
- **H2 Database**: An in-memory database chosen for quick validation (see trade-offs below).
- **JPA with Pessimistic Locks**: Ensures consistency in concurrent operations.
- **Idempotency**: Uses UUID in the Idempotency-Key header, verified in the transaction table.
- **Historical Balance Calculation**: Derived from transactions for full traceability.

## Trade-offs & Constraints

### H2 Database
- **Why**: Lightweight, embedded, ideal for prototypes. Allows validation of business logic without setting up an external database.
- **Trade-off**: Not persistent; for production, PostgreSQL or similar should be used for durability.

### Pessimistic Locks
- **Why**: Ensures consistency in concurrent scenarios.
- **Trade-off**: Reduces scalability under high load; optimistic locks or distributed systems (e.g., Kafka) should be considered for production.

### Historical Balance Calculation
- **Why**: Calculated in real-time based on transactions.
- **Trade-off**: O(n) performance; optimizations like snapshots or database indexing should be considered.

### Input Validation
- **Why**: Minimal (e.g., dateTime assumes ISO format).
- **Trade-off**: In production, robust validation should be added (e.g., `@Valid`, custom exceptions).

## Next Steps
- Replace H2 with a persistent database (e.g., PostgreSQL).
- Add indexes in the database for `TransactionEntity` (e.g., walletId, timestamp).
- Implement robust input validation.
- Create full unit and integration tests.
- Evaluate concurrency alternatives (e.g., optimistic locks or message queues).

