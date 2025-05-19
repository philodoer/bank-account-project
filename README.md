# Project Overview.  
This is a bank account management project. It has three micro-services
1. **Customer Service** - managing customer CRUD operations. run on port 1500
2. **Account Service** - Manage account CRUD operations - linked to customer through customer Id. Run on port 1501
3. **Card Service** - manage card CRUD operations - linked to account via accountId. Run on port 1502.

## Architecture.
Framework: Springboot.  
Built Tool: maven, Java-21.  
Unit test: JUnit 5.  
Database: postgreSQL.  
Pagination and filtering: JpaSpecialization.  
Other key tools - Hibernate, Jpa and FeignClient.  

## Future Improvement.  
1. Authorization and Authentication (RBAC access).  
2. Database migration service - for migration as services grow.  
3. Gateway and Eureka service - for interservice communication, centralized authentication and service registration.  
4. Full internalization, documentation, unit testing.  
5. Integration testing.  
# Bank Account Management Project

This is a bank account management project composed of three microservices:

1. **Customer Service** – Manages customer CRUD operations.
2. **Account Service** – Manages account CRUD operations, linked to customers via `customerId`.
3. **Card Service** – Manages card CRUD operations, linked to accounts via `accountId`.

## Architecture

- **Framework**: Spring Boot  
- **Build Tool**: Maven  
- **Java Version**: Java 21  
- **Unit Testing**: JUnit 5  
- **Database**: PostgreSQL  
- **Pagination & Filtering**: JpaSpecification  
- **Other Tools**: Hibernate, JPA, FeignClient  

## Future Improvements

1. Add Role-Based Access Control (RBAC) for authorization and authentication.
2. Introduce a database migration service for scalable growth.
3. Integrate Spring Cloud Gateway and Eureka for:
   - Centralized authentication
   - Service registration
   - Improved interservice communication
4. Full internationalization, documentation, and unit testing.
5. Add integration testing.
