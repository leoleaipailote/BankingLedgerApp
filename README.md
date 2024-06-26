# Simple Bank Ledger System

I built this simple bank ledger system to enhance my skills with Spring Boot while exploring the [event sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) pattern. The system allows users to perform basic banking operations such as depositing funds, withdrawing funds, and checking balances. It maintains a complete and immutable record of all transactions, ensuring auditability and the ability to reconstruct account balances at any time.

## Features
- **Event Sourcing**: I implemented event sourcing to record every transaction as an immutable event.
- **Operations**:
  - **Deposit (Load)**: Add money to a user's account.
  - **Withdraw (Authorize)**: Conditionally remove money from a user's account.
- **Complete Transaction History**: The system keeps a full, auditable record of all transactions.
- **Balance Calculation**: The ledger returns updated balances with every transaction, including declined authorizations.

## API Details
The service, based on the [service.yml](service.yml) OpenAPI 3.0 schema, accepts two types of transactions:
1. **Load (Deposit)**: Adds funds to a user's account.
2. **Authorization (Withdraw)**: Conditionally deducts funds from a user's account.

Both endpoints return the updated balance after the transaction. Declined authorizations are recorded even if they don't affect the balance.

## Goals
In building this project, I focused on the following:
1. Supporting all requests as defined in the schema.
2. Ensuring responses conform to the prescribed schema.
3. Implementing the authorizations endpoint as documented.
4. Writing comprehensive unit and integration tests.

## Bootstrap Instructions

To run this server locally, follow these steps:

1. **Prerequisites**:
   - Ensure you have Java JDK 11 or higher installed.
   - Maven is required for building and running the application.

2. **Build the project**:
   - Run `mvn clean install` to build the project and install the dependencies.

3. **Run the application**:
   - Execute `mvn spring-boot:run` to start the server.
   - The server will be available at `http://localhost:8080`.

4. **Access the API**:
   - Use a tool like Postman or navigate to `http://localhost:8080/api/ping` to test if the server is running.
   - To use the 'load' endpoint, send a PUT request to `http://localhost:8080/api/load/{messageId}` with the following body format:
     ```json
     {
       "messageId": "{messageId}",
       "userId": "{userId}",
       "transactionAmount": {
         "amount": "{amount}",
         "currency": "{currency}",
         "debitOrCredit": "CREDIT"
       }
     }
     ```
   - To use the 'authorize' endpoint, send a PUT request to `http://localhost:8080/api/authorization/{messageId}` with the same body format used for the 'load' endpoint.

## Design Considerations

For this project, I chose Spring Boot for its robust support for REST APIs and quick bootstrapping capabilities. Here are some key design details:

- **TransactionEvent Object**: This class encapsulates all relevant data for a transaction event, including transaction type, status, user ID, transaction amount, and current balance.
- **Event Sourcing**: I stored each transaction as a `TransactionEvent` in an `EventStore` to ensure traceability. The current balance is dynamically calculated by iterating through all events for a user ID.
- **Service Layer**: The `loadFunds` and `authorizeFunds` endpoints trigger specific behaviors handled by `processLoad` and `processAuthorization` methods. These methods create a `TransactionEvent`, add it to the `EventStore`, calculate the updated balance, and format the response.
- **Error Handling and Validation**: I implemented error handling to ensure only valid transactions are processed, with appropriate responses for invalid transactions.
- **Unit Testing**: I used JUnit for unit testing, covering critical functionalities.
- **Integration Testing**: I used MockMvc for integration testing, simulating HTTP requests and validating responses.

## Assumptions
- **Load**: The application is designed to handle a moderate load. For high-load scenarios, further optimizations would be needed.
- **Data Persistence**: The application uses in-memory storage for simplicity, meaning data is not persistent across restarts.

## Bonus: Deployment Considerations
If I were to deploy this application, I would use the following strategy:
- **Containerization**: Docker for consistent environments.
- **Orchestration**: Kubernetes for managing and scaling the application.
- **CI/CD**: Jenkins or GitHub Actions for automating the build, test, and deployment pipeline.
- **Monitoring**: Prometheus and Grafana for monitoring performance and health.
- **Cloud Provider**: AWS, leveraging ECS or EKS and AWS RDS for a managed database solution if persistent storage is needed.

