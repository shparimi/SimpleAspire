
# Mini Aspire App

A simple app where we can onboard users and specific users will have provision to apply and repay loan in parts.







## API Reference

#### Signup User
API to onboard users. User can be of two types "Customer" and "Manager". 
Users of type customers can apply for loan and repay loan once approved. Users of type Managers can approve and view loan


```http
  POST /signup
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Required** username for login 
| `password` | `string` | **Required** password for login
| `email`    | `string` | **Required** email of user
| `role`     | `string` | **Required** role can be of two types Customer/Manager

#### Signin
API to generate JWT token which is required for further authentication. All the API's required for loan process requires the JWT

```http
  POST /signin
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`| `string` | **Required** username of the login user |
| `password`| `string` | **Required** password of the login user

#### Loan Application
API used to apply for loan. Can be used by user of role Customer only. On successful call, loan will be created in Pending state.
Path userId should match username used to create JWT token

```http
  POST /user/${userId}/applyLoan
  --header Authorization: Bearer userJWT
```
| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `amount`| `double` | **Required** loan amount |
| `term`| `int` | **Required** loan term max 10, min 1

#### Get All Loans
API to fetch all the loans created by the user. If the user is of Manager type, then can fetch loans of all users

```http
  GET /user/${userId}/loans
  --header Authorization: Bearer userJWT
```
#### Get Loan Details
API to get loan details of specific loan. User JWT should be of, either user of loan or of manager

```http
  GET /user/${userId}/loan/{loanId}
  --header Authorization: Bearer userJWT
```

#### Update Loan Status
Once the loan is created by user of type CUSTOMER, user of type MANAGER needs to approve/reject it

```http
  PATCH /user/${userId}/loan/{loanId}/status
    --header Authorization: Bearer managerJWT
```
| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `status`| `String` | **Required** APPROVED/REJECTED |
```

#### Loan Repayment
Once the loan is approved, user has provisioning to pay the loan selected terms. User can pay more than the term amount, except for the last term. Remaining amount will be adjusted between the remaining terms

```http
  POST /user/${userId}/loan/${loanId}/installment
    --header Authorization: Bearer userJWT
```
| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `installmentAmout`| `double` | **Required** amount to be paid |
```





## Run Locally

Accessing database

````bash
    http://localhost:8080/aspireapp/h2-console
````

Install dependencies

```bash
  JRE is required
```

Start the server

```bash
  .\mvnw spring-boot:run
```


## Running Tests

To run tests, run the following command

```bash
  .\mvnw clean test
```


## Tech Stack

**Server:** Java
**Database:** H2 persistence database

