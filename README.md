**Important: Don't forget to update the [Candidate README](#candidate-readme) section**

Real-time Transaction Challenge
===============================
## Overview
Welcome to Current's take-home technical assessment for backend engineers! We appreciate you taking the time to complete this, and we're excited to see what you come up with.

You are tasked with building a simple bank ledger system that utilizes the [event sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) pattern to maintain a transaction history. The system should allow users to perform basic banking operations such as depositing funds, withdrawing funds, and checking balances. The ledger should maintain a complete and immutable record of all transactions, enabling auditability and reconstruction of account balances at any point in time.

## Details
The [included service.yml](service.yml) is the OpenAPI 3.0 schema to a service we would like you to create and host.

The service accepts two types of transactions:
1) Loads: Add money to a user (credit)

2) Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT should return the updated balance following the transaction. Authorization declines should be saved, even if they do not impact balance calculation.


Implement the event sourcing pattern to record all banking transactions as immutable events. Each event should capture relevant information such as transaction type, amount, timestamp, and account identifier.
Define the structure of events and ensure they can be easily serialized and persisted to a data store of your choice. We do not expect you to use a persistent store (you can you in-memory object), but you can if you want. We should be able to bootstrap your project locally to test.

## Expectations
We are looking for attention in the following areas:
1) Do you accept all requests supported by the schema, in the format described?

2) Do your responses conform to the prescribed schema?

3) Does the authorizations endpoint work as documented in the schema?

4) Do you have unit and integrations test on the functionality?

Here’s a breakdown of the key criteria we’ll be considering when grading your submission:

**Adherence to Design Patterns:** We’ll evaluate whether your implementation follows established design patterns such as following the event sourcing model.

**Correctness**: We’ll assess whether your implementation effectively implements the desired pattern and meets the specified requirements.

**Testing:** We’ll assess the comprehensiveness and effectiveness of your test suite, including unit tests, integration tests, and possibly end-to-end tests. Your tests should cover critical functionalities, edge cases, and potential failure scenarios to ensure the stability of the system.

**Documentation and Clarity:** We’ll assess the clarity of your documentation, including comments within the code, README files, architectural diagrams, and explanations of design decisions. Your documentation should provide sufficient context for reviewers to understand the problem, solution, and implementation details.

# Candidate README
## Bootstrap Instructions

To run this server locally, follow these steps:

1. **Prerequisites**:
   - Ensure you have Java JDK 11 or higher installed.
   - Maven is required for building and running the application.

2. **Build the project**:
   - Run `mvn clean install` to build the project and install the dependencies.

3. **Run the application**:
   - Execute `mvn spring-boot:run` to start the server.
   - The server will be available at `http://localhost:8080`

4. **Access the API**:
   - Use a tool like Postman or navigate to `http://localhost:8080/api/ping` to test if the server is running.
    Upon successful connection, a response status of 200 should be received along with a JSON object that contains the serverTime
    in a date format.
   - To use the 'load' endpoint, send a put request to `http://localhost:8080/api/load/{messageId}` with the following body format:
   {
        "messageId": "{messageId}",
        "userId": "{userId}",
        "transactionAmount": {
            "amount": "{amount}",
            "currency": "{currency}",
            "debitOrCredit": "CREDIT"
        }
    }
    - To use the 'authorize' endpoint, send a put request to `http://localhost:8000/api/authorization/{messageId}` with the same body format used for the 'load' endpoint



## Design Considerations

For this project, I chose to implement a Spring Boot application due to its extensive support for REST APIs and its ability to quickly bootstrap robust web applications. Below are the design details and considerations made during the development:

- **TransactionEvent Object**: This is a central class in the application that encapsulates all relevant data for a transaction event. It includes details such as:
  - **Type of Transaction**: Whether it's a credit (add funds) or a debit (reduce funds).
  - **Status**: Indicates if an authorization request was approved or denied.
  - **User ID**: The identifier for the user associated with the transaction.
  - **Transaction Amount**: The amount of money to be added or withdrawn.
  - **Balance Object**: Holds the current balance, including the amount and currency.

- **Event Sourcing**: To ensure all changes to the application state are stored and traceable, I implemented an event sourcing pattern. This involves:
  - **Storing Events**: Each transaction creates a `TransactionEvent` that is stored in an `EventStore`, a collection that logs every event.
  - **State Reconstruction**: The current balance is dynamically calculated by iterating through all events associated with a user ID in the `EventStore`. This approach ensures that the system state can be reconstructed at any point from the event log.

- **Service Layer**:
  - **Load and Authorization Services**: The `loadFunds` and `authorizeFunds` endpoints each trigger specific behaviors handled by `processLoad` and `processAuthorization` methods, respectively. These methods utilize the transaction data contained in the request bodies to:
    - Create a `TransactionEvent`.
    - Add this event to the `EventStore`.
    - Calculate the updated balance using `getBalance`, which aggregates the amounts from relevant transaction events to compute the current balance and stores it in the `TransactionEvent`.
  
  - **Response Formation**: After updating the event and calculating the balance, the newly created `TransactionEvent` is used to format and send a structured response back to the client. Responses for the Authorization request have an additional field that
  capture whether the debit request was authorized or declined (calculated based on whether account had sufficient funds before the request was made).

- **Error Handling and Validation**: Error handling ensures that only valid transactions are processed. Any attempt to process an invalid transaction (e.g., negative amounts, non-numerical amounts) is immediately caught and handled appropriately, returning a 500 error code.

- **Unit Testing**

   - **JUnit**: The primary framework used for unit testing. Each component, especially business logic handlers and utility classes, has corresponding unit tests that validate individual functions under controlled conditions.

- **Integration Testing**

   - **MockMvc**: Utilized for integration testing, allowing simulation of HTTP requests and assertion of responses for the REST API. This framework is instrumental in testing controller endpoints under conditions that closely mimic actual runtime operation.
   - **Endpoint Testing**: Specific tests for `loadFunds` and `authorizeFunds` endpoints check how the system processes transactions, including both normal and edge cases.
   - **Exception Handling**: Tests also cover scenarios where exceptions are expected to be thrown, ensuring that the system responds with appropriate error messages and status codes.
   - **Response Validation**: Integration tests verify that responses are correctly formatted and contain all expected elements, ensuring the API's contract is adhered to.



## Assumptions

- **Load**: It is assumed that the application will handle a moderate load. For high-load scenarios, further optimizations and configurations would be required.
- **Data Persistence**: For simplicity and demonstration purposes, the application uses in-memory storage which means data is not persistent across restarts.


## Bonus: Deployment Considerations

If I were to deploy this application, I would use the following strategy and technologies:

- **Containerization**: Docker would be used to containerize the application, ensuring consistency across different environments.
- **Orchestration**: Kubernetes for managing and scaling the application based on the load, improving availability and resource utilization.
- **CI/CD**: Implement continuous integration and continuous deployment using Jenkins or GitHub Actions to automate the build, test, and deployment pipeline.
- **Monitoring**: Use Prometheus and Grafana for monitoring application performance and health.
- **Cloud Provider**: Deploy on AWS using services like ECS or EKS, leveraging AWS RDS for a managed database solution if persistent storage is needed.


## License

At CodeScreen, we strongly value the integrity and privacy of our assessments. As a result, this repository is under exclusive copyright, which means you **do not** have permission to share your solution to this test publicly (i.e., inside a public GitHub/GitLab repo, on Reddit, etc.). <br>

## Submitting your solution

Please push your changes to the `main branch` of this repository. You can push one or more commits. <br>

Once you are finished with the task, please click the `Submit Solution` link on <a href="https://app.codescreen.com/candidate/8831f1ca-6b24-4a96-9948-b68a99e84665" target="_blank">this screen</a>.
