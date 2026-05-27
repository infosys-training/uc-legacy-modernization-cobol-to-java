# CardDemo Modernization Blueprint

## Executive Summary

CardDemo is a multi-tier mainframe credit card management system comprising 44 COBOL programs (16 online/CICS + 14 batch), 31 copybooks, 17 BMS screen maps, 38 JCL jobs, and Control-M scheduling across daily/weekly/monthly cycles. The application manages credit card accounts, customers, transactions, billing, user security, and reporting against VSAM KSDS data stores with alternate indexes.

This blueprint evaluates four modernization strategies for each functional area and recommends the optimal approach based on complexity, risk, business value, and interdependencies.

---

## Functional Area Inventory

| # | Functional Area | Programs | Data Stores | Type |
|---|-----------------|----------|-------------|------|
| 1 | Authentication & User Security | COSGN00C, COUSR00C–03C | USRSEC | Online (CICS) |
| 2 | Account Management | COACTVWC, COACTUPC, CBACT01C | ACCTDAT, CCXREF, CUSTDAT, CARDDAT | Online + Batch |
| 3 | Credit Card Management | COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C, CBACT03C | CARDDAT, CARDAIX, CCXREF, CXACAIX | Online + Batch |
| 4 | Transaction Processing | COTRN00C–02C, CBTRN01C, CBTRN02C | TRANSACT, DALYTRAN, CCXREF, ACCTDAT, TCATBALF | Online + Batch |
| 5 | Billing & Payments | COBIL00C | ACCTDAT, CCXREF, TRANSACT | Online (CICS) |
| 6 | Interest Calculation | CBACT04C | TCATBALF, CCXREF, ACCTDAT, DISCGRP | Batch |
| 7 | Reporting & Statements | CORPT00C, CBTRN03C, CBSTM03A/B, PRTCATBL | TRANSACT, ACCTDAT, CCXREF, CUSTDAT | Online trigger + Batch |
| 8 | Data Migration (Export/Import) | CBEXPORT, CBIMPORT | All entity files + CVEXPORT | Batch |
| 9 | Menu & Navigation | COMEN01C, COADM01C | USRSEC (role check) | Online (CICS) |
| 10 | Batch Infrastructure | CLOSEFIL, OPENFIL, WAITSTEP, TRANBKP, COMBTRAN | CICS file control, GDGs | Batch/JCL |
| 11 | Reference Data Management | DISCGRP, TRANCATG, TRANTYPE JCL; Db2 optional modules | DISCGRP, TRANCATG, TRANTYPE | Batch + Optional Db2 |
| 12 | Utilities & Cross-Cutting | CSUTLDTC, COBSWAIT, ASM routines (MVSWAIT, COBDATFT) | N/A | Batch |

---

## Strategy Evaluation by Functional Area

### 1. Authentication & User Security

**Programs:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C
**Data:** USRSEC VSAM KSDS (80-byte records: user ID, name, password, type)
**Copybooks:** CSUSR01Y, COCOM01Y

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | Medium | Wrap USRSEC with a REST API; let new apps authenticate via API while CICS continues using VSAM directly during transition. |
| **Replatform** | Low | RACF/USRSEC is inherently mainframe-centric; moving to cloud runtime preserves a weak security model (plaintext passwords, no MFA). |
| **Refactor** | Low | Restructuring COBOL won't address the fundamental inadequacy of a flat-file security model. |
| **Rewrite** | **High** | Replace with a modern identity provider (e.g., AWS Cognito, Keycloak, Spring Security) offering JWT tokens, MFA, RBAC, and password hashing. |

**Recommendation: Rewrite**
The current security model stores passwords in plaintext in a VSAM file with minimal role differentiation (A/U). This is a compliance liability. Replacing it with a modern IAM solution eliminates security debt and enables all other modernized components to use standard OAuth2/OIDC authentication. This should be one of the first areas migrated as it gates every other user-facing function.

---

### 2. Account Management

**Programs:** COACTVWC (view), COACTUPC (update), CBACT01C (batch read/export)
**Data:** ACCTDAT (300-byte records), CCXREF, CUSTDAT, CARDDAT
**Copybooks:** CVACT01Y, CVACT03Y, CVCUS01Y, CVCRD01Y, COCOM01Y
**Complexity:** COACTUPC is one of the most complex programs (~39 CSSETATY COPY REPLACING invocations for field-level attribute setting, extensive input validation with date, numeric, and alpha checks)

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | **High** | Expose Account View/Update via REST APIs. New frontends consume APIs while BMS screens continue to work via an API gateway adapter. |
| **Replatform** | Medium | Works on AWS M2 or UniKix but misses the opportunity to modernize the complex validation logic and 3270 UI dependency. |
| **Refactor** | Low | The heavy use of COPY REPLACING and tight coupling to BMS maps limits refactoring benefit. |
| **Rewrite** | High | Good candidate for rewrite to Java/Spring Boot with JPA entities, but the complex validation in COACTUPC (~1000+ lines) carries translation risk. |

**Recommendation: Strangler Pattern**
Account management is central to the application with multiple consumers (online screens, batch processes, export). Wrapping it with REST APIs allows incremental migration. Start by extracting read operations (COACTVWC) into a microservice backed by a relational database, then progressively migrate update operations. The COMMAREA structure (COCOM01Y) provides a natural API contract boundary.

---

### 3. Credit Card Management

**Programs:** COCRDLIC (list), COCRDSLC (view detail), COCRDUPC (update), CBACT02C (batch print), CBACT03C (batch xref print)
**Data:** CARDDAT (150-byte records), CARDAIX (alternate index), CCXREF, CXACAIX
**Copybooks:** CVCRD01Y, CVACT02Y, CVACT03Y

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | **High** | Card CRUD operations map cleanly to REST resources. The alternate index (CARDAIX) maps to a secondary database index. |
| **Replatform** | Medium | Functional on cloud mainframe runtimes but the BMS screens and AIX patterns are less maintainable than modern alternatives. |
| **Refactor** | Medium | Could consolidate the three online programs (list/view/update) but still limited by CICS dependency. |
| **Rewrite** | High | Clean domain model (Card entity with Account FK) translates well to JPA/Spring Data. |

**Recommendation: Strangler Pattern**
Credit card management shares significant data overlap with Account Management (both use CCXREF, ACCTDAT). Strangling both simultaneously via a unified API layer prevents data synchronization issues. The CCXREF cross-reference file is the natural join point and should become a relational foreign key in the target database.

---

### 4. Transaction Processing

**Programs:** COTRN00C (list), COTRN01C (view), COTRN02C (add), CBTRN01C (batch post), CBTRN02C (batch post with categorization)
**Data:** TRANSACT VSAM KSDS (350-byte records), DALYTRAN sequential, CCXREF, ACCTDAT, TCATBALF
**Copybooks:** CVTRA05Y, CVTRA06Y, CVTRA01Y
**Scheduling:** Daily backup (CLOSEFIL→TRANBKP→WAITSTEP→OPENFIL)

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | Medium | Online transaction entry can be API-wrapped, but the batch posting pipeline has tight coupling to VSAM lifecycle (close-process-open pattern). |
| **Replatform** | Medium | Batch transaction posting can run on AWS M2 during transition, but the GDG-based backup and SORT utility dependency require mainframe runtime. |
| **Refactor** | Low | The close-process-open pattern and GDG dependencies are fundamental to the architecture. |
| **Rewrite** | **High** | Transaction processing is the core value stream. Rewriting to an event-driven architecture (e.g., Kafka + Spring Boot) replaces the batch posting pattern with real-time processing, eliminating the daily maintenance window. |

**Recommendation: Rewrite**
Transaction processing is the highest-value modernization target. The current architecture requires daily CICS file closures for batch posting, creating an availability gap. A modern event-driven design eliminates this by processing transactions in near-real-time. The DALYTRAN→TRANSACT batch pipeline (CBTRN02C) becomes an event consumer. The TCATBALF category balance file becomes a materialized view or aggregation table. This removes the most operationally complex batch chain.

---

### 5. Billing & Payments

**Programs:** COBIL00C
**Data:** ACCTDAT, CCXREF, TRANSACT
**Copybooks:** CVACT01Y, CVACT03Y, CVTRA05Y

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | **High** | Bill payment is a self-contained operation (read balance, create payment transaction, update account). Easily exposed as an API. |
| **Replatform** | Medium | Works but perpetuates the 3270 UI for a high-frequency user operation. |
| **Refactor** | Low | Single program with straightforward logic; refactoring COBOL provides minimal benefit. |
| **Rewrite** | High | Payment processing benefits from modern security, audit trails, and integration with payment gateways. |

**Recommendation: Strangler Pattern**
Bill payment depends on Account and Transaction data stores. Once those are API-wrapped (Areas 2 and 4), bill payment becomes a thin orchestration layer calling existing APIs. Implement as a new microservice that coordinates account balance reads and transaction creation through the strangler APIs.

---

### 6. Interest Calculation

**Programs:** CBACT04C
**Data:** TCATBALF, CCXREF (via AIX), ACCTDAT, DISCGRP
**JCL:** INTCALC (monthly, receives date parameter)
**Scheduling:** Monthly cycle (CLOSEFIL→INTCALC→COMBTRAN→WAITSTEP→OPENFIL)

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | Low | Pure batch calculation with no UI; wrapping with API provides little value during transition. |
| **Replatform** | **High** | Self-contained batch program that reads multiple VSAM files and produces interest transactions. Can run on AWS M2 while other areas modernize. |
| **Refactor** | Medium | Business logic is well-encapsulated but depends on VSAM file access patterns. |
| **Rewrite** | High | Could become a scheduled cloud-native batch job (Spring Batch), but the financial calculation logic requires exact equivalence testing. |

**Recommendation: Replatform (interim) → Rewrite (final)**
Interest calculation is a critical financial process where calculation errors have direct monetary impact. Replatform it to AWS M2 first to maintain exact behavioral equivalence while other areas modernize. Once the Account and Transaction data stores are migrated to a relational database, rewrite as a Spring Batch job with the same calculation logic, validated via parallel-run comparison.

---

### 7. Reporting & Statements

**Programs:** CORPT00C (CICS trigger), CBTRN03C (report generator), CBSTM03A/B (statement creation)
**Data:** TRANSACT, ACCTDAT, CCXREF, CUSTDAT
**JCL:** TRANREPT (uses SORT utility, PROC REPROC), CREASTMT
**Copybooks:** CVTRA07Y (report headers), CVTRA03Y, CVTRA04Y

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | Low | Reports are output-only batch processes; API wrapping doesn't apply well. |
| **Replatform** | **High** | Reporting depends on mainframe SORT utility and PROC-based JCL orchestration. These are well-supported on AWS M2. |
| **Refactor** | Low | Report formatting is inherently procedural; no architectural improvement from COBOL restructuring. |
| **Rewrite** | Medium | Can be replaced with modern reporting tools (JasperReports, AWS QuickSight) but requires replicating exact report formats for regulatory compliance. |

**Recommendation: Replatform (interim) → Rewrite (final)**
Reports and statements have regulatory format requirements that make exact replication critical. Replatform to AWS M2 initially (the SORT utility and REPROC procedure work natively). After transaction data migrates to a relational database, rewrite using modern reporting frameworks that generate equivalent outputs, validated via diff comparison against M2-generated reports.

---

### 8. Data Migration (Export/Import)

**Programs:** CBEXPORT, CBIMPORT
**Data:** All entity files → CVEXPORT (multi-record type, 500-byte layout with REDEFINES for Customer/Account/Transaction/Card/XRef)
**Copybooks:** CVEXPORT (uses COMP/COMP-3 packed decimal fields, OCCURS, REDEFINES)

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | Low | These are infrastructure utilities, not user-facing functions. |
| **Replatform** | Medium | Can run on AWS M2 but the EBCDIC/packed-decimal format is not useful in a modern ecosystem. |
| **Refactor** | Low | No benefit from COBOL restructuring. |
| **Rewrite** | **High** | Replace with modern ETL pipelines (AWS Glue, Apache NiFi, Spring Batch) that export to JSON/CSV/Parquet formats. The CVEXPORT copybook documents the canonical data model for all entities. |

**Recommendation: Rewrite**
The export/import programs serve as the data bridge between the mainframe and branch systems. The current EBCDIC packed-decimal format (COMP/COMP-3 fields in CVEXPORT) is incompatible with modern systems. Rewrite as cloud-native ETL pipelines that read from the migrated relational database and produce standard formats. The CVEXPORT copybook serves as the definitive schema documentation for the migration.

---

### 9. Menu & Navigation

**Programs:** COMEN01C (user menu, 11 options), COADM01C (admin menu, 6 options)
**Data:** USRSEC (role check only)
**Copybooks:** COMEN02Y (menu option definitions), COADM02Y (admin option definitions)
**BMS Maps:** COMEN01, COADM01

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | Low | 3270 navigation paradigm doesn't translate to API wrapping. |
| **Replatform** | Low | Preserves an outdated terminal UI. |
| **Refactor** | Low | Menu logic is trivial (option→program dispatch). |
| **Rewrite** | **High** | Replace with a modern web/SPA frontend. The menu structure defined in COMEN02Y/COADM02Y provides the navigation requirements. |

**Recommendation: Rewrite**
The 3270 terminal interface is the most visible modernization target. Replace with a React/Angular SPA that calls the strangler APIs. The menu option tables in COMEN02Y and COADM02Y define exactly 11 user functions and 6 admin functions, providing a clear specification for the new UI. This should be built progressively as each backend function is API-wrapped.

---

### 10. Batch Infrastructure

**Programs/JCL:** CLOSEFIL, OPENFIL, WAITSTEP (COBSWAIT), TRANBKP, COMBTRAN
**Function:** CICS file lifecycle management (close files for batch, reopen after), transaction master backup via IDCAMS REPRO, GDG management

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | Low | Infrastructure utilities with no business logic to wrap. |
| **Replatform** | **High** | These are the most mainframe-specific components. On AWS M2, the CICS file close/open and IDCAMS REPRO commands work natively. |
| **Refactor** | Low | No business logic to restructure. |
| **Rewrite** | Low | Eliminated naturally when VSAM files are replaced with a relational database (no need to close files for batch access). |

**Recommendation: Replatform (then eliminate)**
The close-process-open pattern exists solely because CICS VSAM files cannot be concurrently accessed by batch programs. This entire infrastructure layer disappears when the data store migrates to a relational database with proper transaction isolation. Replatform initially to maintain the batch window on AWS M2, then retire these jobs as data stores migrate.

---

### 11. Reference Data Management

**Data:** DISCGRP (Disclosure Groups), TRANCATG (Transaction Categories), TRANTYPE (Transaction Types)
**JCL:** DISCGRP, TRANCATG, TRANTYPE (IDCAMS REPRO from sequential to VSAM)
**Optional Db2 modules:** COTRTLIC, COTRTUPC (admin screens for Db2 transaction type CRUD)

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | Medium | Reference data can be API-wrapped but the IDCAMS-based loading is hard to strangler. |
| **Replatform** | Medium | Works on AWS M2 but maintaining VSAM for reference data is low value. |
| **Refactor** | Low | Minimal logic in JCL-driven REPRO operations. |
| **Rewrite** | **High** | Reference data is a classic fit for a simple CRUD microservice with a relational database. Small dataset sizes make migration trivial. |

**Recommendation: Rewrite**
Reference data tables (disclosure groups, transaction categories, transaction types) are small, slowly changing, and already have optional Db2 equivalents. Migrate to a relational database with a simple admin API/UI. The existing Db2 DDL and COBOL-Db2 programs (COTRTLIC, COTRTUPC) can serve as specifications for the new implementation.

---

### 12. Utilities & Cross-Cutting Concerns

**Programs:** CSUTLDTC (date utility), COBSWAIT (wait timer), COBDATFT.asm (date format conversion), MVSWAIT.asm (MVS timer)
**Copybooks:** CSUTLDWY/CSUTLDPY (date validation), CSSETATY (BMS attribute setting), CSSTRPFY (strip function), CSLKPCDY (lookup codes for phone area codes, state codes)

| Strategy | Fit | Assessment |
|----------|-----|------------|
| **Strangler** | Low | Utility routines are internal; not exposed externally. |
| **Replatform** | Medium | Date utilities and assembler routines work on AWS M2 with TPE emulation. |
| **Refactor** | Low | Already well-encapsulated as copybooks. |
| **Rewrite** | **High** | Replace with Java standard library equivalents (java.time for dates, standard wait mechanisms). The CSLKPCDY lookup table becomes a configuration file or database table. |

**Recommendation: Rewrite (as part of consuming programs)**
Utility routines are not migrated independently. As each consuming program is rewritten, its utility dependencies are replaced with modern equivalents. The date validation logic in CSUTLDPY/CSUTLDWY maps to `java.time.LocalDate` validation. The assembler wait routines (MVSWAIT, COBDATFT) are replaced by `Thread.sleep()` and `DateTimeFormatter`. The CSLKPCDY lookup codes become a reference data table.

---

## Strategy Summary

| Functional Area | Recommended Strategy | Priority | Rationale |
|-----------------|---------------------|----------|-----------|
| Authentication & Security | **Rewrite** | P0 | Security compliance; gates all other work |
| Account Management | **Strangler** | P1 | Central domain; API-first migration |
| Credit Card Management | **Strangler** | P1 | Shares data with accounts; co-migrate |
| Transaction Processing | **Rewrite** | P1 | Highest business value; event-driven target |
| Billing & Payments | **Strangler** | P2 | Depends on Account + Transaction APIs |
| Interest Calculation | **Replatform → Rewrite** | P2 | Financial accuracy critical; parallel run |
| Reporting & Statements | **Replatform → Rewrite** | P3 | Regulatory format compliance |
| Data Migration (Export/Import) | **Rewrite** | P2 | EBCDIC format incompatible with modern |
| Menu & Navigation | **Rewrite** | P1 | New SPA frontend, built progressively |
| Batch Infrastructure | **Replatform → Eliminate** | P3 | Disappears when VSAM replaced |
| Reference Data | **Rewrite** | P1 | Small scope, quick win |
| Utilities | **Rewrite (inline)** | — | Migrated with consuming programs |

---

## Technology Target State

| Layer | Current | Target |
|-------|---------|--------|
| Language | COBOL | Java 17+ / Spring Boot 3.x |
| UI | BMS/3270 | React SPA or Angular |
| Transaction Manager | CICS | Spring Boot embedded (Tomcat/Netty) |
| Data Store | VSAM KSDS | PostgreSQL or Amazon Aurora |
| Batch | JCL + COBOL | Spring Batch + cloud scheduler |
| Messaging | None (sequential files) | Amazon SQS / Apache Kafka |
| Scheduling | Control-M | AWS Step Functions / CloudWatch Events |
| Security | USRSEC flat file | AWS Cognito / Spring Security + OAuth2 |
| Reporting | COBOL print + SORT | JasperReports / AWS QuickSight |
| ETL | CBEXPORT/CBIMPORT (EBCDIC) | AWS Glue / Spring Batch (JSON/CSV) |
| Infrastructure | z/OS | AWS ECS/EKS or Lambda |
