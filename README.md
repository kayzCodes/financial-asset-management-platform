# Financial Asset Management Platform

This project is a full-stack financial asset management web application developed as part of a Final Year Project. The platform allows users to securely manage and track financial assets, including stocks and cryptocurrencies, set financial goals, view a portfolio overview, and access personalised financial news within a single, unified system.

---

## Overview

The application provides:

- Secure user authentication using Firebase (JWT-based)
- Asset tracking for stocks and cryptocurrencies with separate backend flows
- Transaction-based portfolio management (buy/sell history as source of truth)
- Portfolio overview with backend-driven aggregation (PnL, allocation, performance)
- Financial goal creation and progress tracking through recorded savings
- Personalised news generated based on user-held assets
- Live market data integration with caching and controlled refresh
- Foreign exchange conversion to a consistent base currency (GBP)

The system follows modern software engineering practices, including layered architecture, RESTful APIs, backend-driven data processing, and token-based authentication.

---

## Technology Stack

**Frontend**

- React
- Vite
- Tailwind CSS
- React Router
- Axios

**Backend**

- Java 21
- Spring Boot
- Spring Security
- Maven

**Database**

- PostgreSQL

**Authentication**

- Firebase Authentication (JWT-based)

---

## Project Structure

```text
PROJECT/
├── product/
│   ├── frontend/
│   └── backend/
├── documents/
├── diary.md
├── .gitignore
└── gitlab-ci.yml
```

## Prerequisites

To run the project locally, the following must be installed:

- Java 21
- Maven 3.9+
- Node.js
- PostgreSQL
- pgAdmin 4 (for database management)
- A Firebase project with Authentication enabled

---

## Installation

### 1. Clone the Repository

git clone <https://github.com/kayzCodes/financial-asset-management-platform.git>

### 2. Backend Configuration

- Create a PostgreSQL database
- Configure database credentials in `application.properties`
- Place the Firebase service account file (`firebase-service-account.json`) in the backend configuration directory
- Add API keys for external services in your environment or configuration:
  - Alpha Vantage (market data)
  - MarketAux (financial news)
- Ensure the application can access ECB exchange rate data (no API key required)

### 3. Frontend Configuration

- Ensure Firebase client configuration is set up in the frontend

## Running the Application

### Start the Backend

cd PROJECT/product/backend
mvn spring-boot:run

Backend runs at:  
`http://localhost:8080`

### Start the Frontend

cd PROJECT/product/frontend
npm install
npm run dev

Frontend runs at:  
`http://localhost:5173`

---

## Features Implemented

- User authentication with Firebase
- Protected frontend routes
- Secure backend API endpoints with token validation
- Portfolio overview dashboard
- Stock and cryptocurrency tracking
- Financial goal management
- Personalised news generation based on user-held assets

---

## Testing

- Backend testing implemented using JUnit
- Controller tests using MockMvc and Mockito
- Service and configuration tests to ensure correct application behaviour

---

## Security

- Firebase Authentication for user identity management
- JWT tokens included in API request headers
- Backend token validation enforced before controller execution
- Protected frontend routes to prevent unauthorised access

---

## Project Status and Future Work

The project is currently complete as a functional full-stack application, with core features including secure authentication, transaction-based portfolio management, backend-driven aggregation, and personalised news delivery fully implemented and integrated.

Future development will focus on refining the system towards an industry-standard solution. This includes improving the accuracy of financial modelling, enhancing data visualisation, expanding automated test coverage, and strengthening performance and security for production-level reliability. Additional improvements such as more advanced news filtering and integration with real-world financial services would further extend the system’s practical applicability.

## Author

**Olukorede Oduniyi**  
BSc (Hons) Computer Science (Software Engineering)  
Final Year Project
