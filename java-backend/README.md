# CardDemo — Java/Spring Boot Migration

Modernized Java implementation of the CardDemo mainframe application (originally COBOL/CICS/VSAM).

## Prerequisites

- **Java 17** or later
- **Maven 3.6+**

## Quick Start

```bash
cd java-backend
mvn spring-boot:run
```

The application starts on `http://localhost:8080` with an embedded H2 database pre-loaded with sample data.

### H2 Console

Available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:carddemo`, user: `sa`, no password).

## REST API Endpoints

### Accounts (replaces COACTUPC/COACTVWC)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/accounts` | List accounts (paginated) |
| GET | `/api/v1/accounts/{id}` | Get account by ID |
| GET | `/api/v1/accounts/{id}/details` | Get account with customer + card xrefs |
| POST | `/api/v1/accounts` | Create account |
| PUT | `/api/v1/accounts/{id}` | Update account |
| POST | `/api/v1/accounts/{id}/interest?rate=X` | Apply interest |

### Cards (replaces COCRDLIC/COCRDSLC/COCRDUPC)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/cards` | List cards (paginated) |
| GET | `/api/v1/cards/{cardNumber}` | Get card detail |
| GET | `/api/v1/cards/by-account/{accountId}` | Cards by account |
| POST | `/api/v1/cards?customerId=X` | Create card + XREF |
| PUT | `/api/v1/cards/{cardNumber}` | Update card |

### Transactions (replaces COTRN00C/01C/02C, CBTRN02C)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/transactions` | List transactions (paginated) |
| GET | `/api/v1/transactions/{id}` | Get transaction detail |
| GET | `/api/v1/transactions/by-card/{cardNumber}` | By card number |
| GET | `/api/v1/transactions/by-account/{accountId}` | By account |
| POST | `/api/v1/transactions` | Create transaction (validates card, account, credit limit) |

### Customers (extracted from COACTUPC)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/customers` | List customers (paginated) |
| GET | `/api/v1/customers/{id}` | Get customer |
| GET | `/api/v1/customers/search?lastName=X` | Search by name |
| POST | `/api/v1/customers` | Create customer |
| PUT | `/api/v1/customers/{id}` | Update customer |

### Users (replaces COSGN00C, COUSR00-03C)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users` | List all users |
| GET | `/api/v1/users/{userId}` | Get user |
| POST | `/api/v1/users/authenticate` | Login (body: `{"userId":"...","password":"..."}`) |
| POST | `/api/v1/users` | Create user |
| PUT | `/api/v1/users/{userId}` | Update user |
| DELETE | `/api/v1/users/{userId}` | Delete user |

### Transaction Types & Categories (replaces COTRTLIC/COTRTUPC — DB2)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/transaction-types` | List types |
| POST | `/api/v1/transaction-types` | Create type |
| GET | `/api/v1/transaction-types/{code}/categories` | List categories |

## Running Tests

```bash
mvn test
```

## Sample API Calls

```bash
# List accounts
curl http://localhost:8080/api/v1/accounts

# Get account details (includes linked customer and cards)
curl http://localhost:8080/api/v1/accounts/80001000001/details

# Create a transaction
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{"typeCode":"SA","categoryCode":5001,"source":"POS","description":"Test","amount":50.00,"cardNumber":"4111111111111111"}'

# Authenticate
curl -X POST http://localhost:8080/api/v1/users/authenticate \
  -H "Content-Type: application/json" \
  -d '{"userId":"admin01","password":"password"}'
```

## COBOL → Java Mapping

| COBOL Program | LOC | Java Class | Layer |
|---------------|-----|-----------|-------|
| COACTUPC.cbl | 4,236 | AccountService | Service |
| COACTVWC.cbl | 941 | AccountService.getAccountDetails | Service |
| COCRDLIC.cbl | 1,459 | CardService | Service |
| COCRDSLC.cbl | 887 | CardService.findByCardNumber | Service |
| COCRDUPC.cbl | 1,560 | CardService.updateCard | Service |
| COTRN00C.cbl | 699 | TransactionService | Service |
| COTRN01C.cbl | 330 | TransactionService.findById | Service |
| COTRN02C.cbl | 783 | TransactionService.createTransaction | Service |
| CBTRN02C.cbl | 731 | TransactionService (inline posting) | Service |
| COSGN00C.cbl | 260 | UserService.authenticate | Service |
| COUSR00C.cbl | 695 | UserService.findAll | Service |
| COUSR01C.cbl | 299 | UserService.createUser | Service |
| COUSR02C.cbl | 414 | UserService.updateUser | Service |
| COUSR03C.cbl | 359 | UserService.deleteUser | Service |
| COTRTLIC.cbl | — | TransactionTypeService | Service |
| COTRTUPC.cbl | — | TransactionTypeService | Service |
| CBACT04C.cbl | — | AccountService.applyInterest | Service |

| COBOL Copybook | Java Entity | Table |
|----------------|-----------|-------|
| CVACT01Y.cpy | Account | accounts |
| CVCUS01Y.cpy | Customer | customers |
| CVACT02Y.cpy | Card | cards |
| CVACT03Y.cpy | CardXref | card_xref |
| CVTRA05Y.cpy | Transaction | transactions |
| CVTRA03Y.cpy | TransactionType | transaction_types |
| CVTRA04Y.cpy | TransactionCategory | transaction_categories |
| CVTRA02Y.cpy | DisclosureGroup | disclosure_groups |
| CVTRA01Y.cpy | TransactionCategoryBalance | tran_cat_balances |
| CSUSR01Y.cpy | UserSecurity | users |
