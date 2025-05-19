# Project Overview.  
This is a bank account management project. It has three micro-services
1. **Customer Service** - managing customer CRUD operations. run on port 1500. Doc. accessible via **ip:1500/swagger-ui/index.html**
2. **Account Service** - Manage account CRUD operations - linked to customer through customer Id. Run on port 1501 Doc. accessible via **ip:1501/swagger-ui/index.html**
3. **Card Service** - manage card CRUD operations - linked to account via accountId. Run on port 1502. Doc. accessible via **ip:1502/swagger-ui/index.html**

## Architecture.
Framework: Springboot.  
Built Tool: maven, Java-21.  
Documentation: swagger.
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
