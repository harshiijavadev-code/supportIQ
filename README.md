# 🎯 SupportIQ - AI-Powered Support Ticket Management System

An intelligent SaaS platform that revolutionizes customer support by using AI to automatically categorize tickets, analyze sentiment, detect recurring issues, and suggest response drafts - helping support teams respond faster and managers gain actionable insights.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen)


## 🌟 Features

### ✅ Completed Features

#### 🔐 Authentication & Authorization
- JWT-based authentication with secure token generation
- Role-based access control (RBAC) for Customers, Agents, and Admins
- Password encryption with BCrypt
- Stateless session management

#### 🎫 Ticket Management
- Complete CRUD operations for support tickets
- Ticket assignment to agents
- Status tracking (Open, In Progress, Closed)
- Priority levels (Low, Medium, High)
- Comment system for ticket conversations
- Multi-tenant organization isolation

#### 👥 User Management
- User registration and authentication
- Multiple user roles (Customer, Agent, Admin)
- Organization-based user isolation
- Secure user profile management

#### 🛡️ Security & Validation
- Input validation on all endpoints
- Global exception handling with clean error messages
- CORS and CSRF protection
- HTTP-only authentication tokens

### 🚧 In Development

#### 🤖 AI-Powered Features (Coming Soon)
- **AI Ticket Categorization**: Automatically classify tickets (Technical, Billing, Feature Request, Bug)
- **Sentiment Analysis**: Detect customer emotions (Angry, Frustrated, Neutral, Happy)
- **Smart Response Suggestions**: AI-generated draft responses based on ticket content
- **Recurring Issue Detection**: Identify patterns and group similar problems
- **Manager Analytics Dashboard**: Insights into team performance and product issues

## 🏗️ Architecture
```
┌─────────────────────────────────────────────┐
│          Spring Boot Application            │
├─────────────────────────────────────────────┤
│  Controllers (REST API Endpoints)           │
│  ├── AuthController                         │
│  ├── TicketController                       │
│  ├── UserController                         │
│  └── OrganizationController                 │
├─────────────────────────────────────────────┤
│  Services (Business Logic)                  │
│  ├── AuthenticationService                  │
│  ├── TicketService                          │
│  ├── UserService                            │
│  └── AI Services (Coming Soon)              │
├─────────────────────────────────────────────┤
│  Security Layer                             │
│  ├── JWT Authentication Filter              │
│  ├── Role-Based Authorization               │
│  └── Global Exception Handler               │
├─────────────────────────────────────────────┤
│  Data Layer                                 │
│  ├── JPA Repositories                       │
│  ├── Entity Models                          │
│  └── Database (H2/PostgreSQL)               │
└─────────────────────────────────────────────┘
```

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend Framework** | Spring Boot 3.2.2 |
| **Language** | Java 17 |
| **Security** | Spring Security + JWT |
| **Database** | H2 (Dev), PostgreSQL (Prod) |
| **ORM** | Spring Data JPA / Hibernate |
| **AI Integration** | Spring AI (In Progress) |
| **Validation** | Bean Validation API |
| **Build Tool** | Maven |
| **API Testing** | Postman |

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/supportiq-spring-boot.git
cd supportiq-spring-boot
```

2. **Build the project**
```bash
./mvnw clean install
```

3. **Run the application**
```bash
./mvnw spring-boot:run
```

4. **Access the application**
- API Base URL: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (leave empty)

## 🧪 Test Users

The application automatically creates test users on startup:

| Role | Email | Password | Permissions |
|------|-------|----------|-------------|
| **Admin** | admin@techcorp.com | admin123 | Full system access |
| **Agent** | agent@techcorp.com | agent123 | View/assign tickets, manage customers |
| **Customer** | customer@techcorp.com | customer123 | Create tickets, add comments |

## 📡 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "CUSTOMER",
  "organizationId": 1
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "customer@techcorp.com",
  "password": "customer123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "customer@techcorp.com",
  "role": "CUSTOMER",
  "userId": 3,
  "message": "Login successful"
}
```

### Ticket Endpoints

#### Create Ticket
```http
POST /api/tickets
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Cannot access dashboard",
  "description": "Getting 404 error when clicking on dashboard link",
  "priority": "HIGH",
  "createdByUserId": 3,
  "organizationId": 1
}
```

#### Assign Ticket (Agent/Admin only)
```http
PUT /api/tickets/{id}/assign
Authorization: Bearer {token}
Content-Type: application/json

{
  "agentId": 2
}
```

#### Update Ticket Status (Agent/Admin only)
```http
PUT /api/tickets/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "IN_PROGRESS"
}
```

### Full API Documentation

For complete API documentation with all endpoints, see [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## 🔐 Security Features

- **JWT Authentication**: Stateless authentication with 10-hour token validity
- **Password Hashing**: BCrypt encryption for all passwords
- **Role-Based Access Control**: Fine-grained permissions based on user roles
- **Input Validation**: Comprehensive validation on all request DTOs
- **Exception Handling**: Global error handling with clean, consistent error messages
- **CORS Protection**: Configurable cross-origin resource sharing
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries

## 📊 Project Status

| Component | Status | Progress |
|-----------|--------|----------|
| **Foundation** | ✅ Complete | 100% |
| **Authentication & Authorization** | ✅ Complete | 100% |
| **Ticket Management** | ✅ Complete | 100% |
| **Input Validation** | ✅ Complete | 100% |
| **Exception Handling** | ✅ Complete | 100% |
| **AI Integration** | 🚧 In Progress | 0% |
| **Analytics Dashboard** | 📅 Planned | 0% |
| **PostgreSQL Migration** | 📅 Planned | 0% |
| **Deployment** | 📅 Planned | 0% |

**Overall Progress: 50%**

## 🗺️ Roadmap

### Phase 1: Foundation ✅ (Completed)
- [x] Project setup and structure
- [x] Database design and entities
- [x] JWT authentication
- [x] Role-based authorization
- [x] CRUD operations for all entities
- [x] Input validation
- [x] Exception handling

### Phase 2: AI Integration 🚧 (Next)
- [ ] Spring AI setup
- [ ] OpenAI/Claude API integration
- [ ] Automatic ticket categorization
- [ ] Sentiment analysis
- [ ] AI response suggestions
- [ ] Recurring issue detection

### Phase 3: Analytics & Insights 📅
- [ ] Manager dashboard
- [ ] Team performance metrics
- [ ] Customer satisfaction tracking
- [ ] Issue trend analysis

### Phase 4: Production Ready 📅
- [ ] PostgreSQL migration
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] Cloud deployment
- [ ] Performance optimization

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**harsh**
- GitHub: https://github.com/harshiijavadev-code


## 🙏 Acknowledgments

- Spring Boot Team for the amazing framework
- Spring AI for AI integration capabilities
- OpenAI/Anthropic for AI models
- The open-source community

## 📧 Contact

For questions or feedback, please reach out via [GitHub Issues](https://github.com/yourusername/supportiq-spring-boot/issues).

---

⭐ If you find this project useful, please consider giving it a star on GitHub!
