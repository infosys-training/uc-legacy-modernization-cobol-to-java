# PROJECT SUMMARY

## Overview

**CardDemo** is a mainframe-based credit card management application written in COBOL, designed to run on IBM z/OS with CICS (online transactions), VSAM (file storage), and JCL (batch orchestration). This repository contains the legacy source code along with analysis artifacts produced to support a **COBOL-to-Java modernization** effort.

The application demonstrates a complete card-issuing back-office: customers, accounts, cards, transactions, interest calculation, statements, reporting, and data migration — implemented across batch programs, CICS online screens, and several sub-applications that exercise IMS DB, DB2, and MQ Series.

## Application Footprint

| Area | Count | Location |
|------|-------|----------|
| Main COBOL programs | 31 (14 batch + 17 CICS online) | `app/cbl/` |
| Sub-application programs | 13 (IMS/DB2/MQ variants) | `app/app-authorization-ims-db2-mq/`, `app/app-transaction-type-db2/`, `app/app-vsam-mq/` |
| Copybooks | 30 | `app/cpy/` |
| JCL jobs | 38 | `app/jcl/` |
| Total program lines of code | ~20,650 | — |

## Business Domains

The application is organized around six core business entities, each backed by a VSAM KSDS:

- **Customer** (`CUSTDATA`, 500-byte records) — PII, addresses, FICO score
- **Account** (`ACCTDATA`, 300 bytes) — balances, credit limits, lifecycle dates
- **Card** (`CARDDATA`, 150 bytes) — card numbers, CVV, embossed name
- **Cross-Reference** (`CARDXREF`, 50 bytes) — card ↔ account ↔ customer links
- **Transaction** (`TRANSACT`, 350 bytes) — posted financial transactions
- **Reference Data** — transaction types, categories, disclosure groups, category balances, user security

## Functional Capabilities

1. **Online (CICS/BMS):** sign-on, admin and user menus, account view/update, card list/view/update, transaction list/view/add, bill payment, reporting, user CRUD, pending authorization view.
2. **Daily batch pipeline:** transaction posting (`CBTRN02C`), interest calculation (`CBACT04C`), transaction reports (`CBTRN03C`), account statements (`CBSTM03A`/`CBSTM03B` text + HTML).
3. **Data migration:** export/import for branch migration (`CBEXPORT`/`CBIMPORT`) with a multi-record `REDEFINES` layout.
4. **Sub-systems:** IMS DB + MQ-based card authorization, DB2-backed transaction-type maintenance, VSAM-to-MQ account and date services.

## Architecture & Dependency Highlights

- CICS navigation is **dynamic** — programs `XCTL` to the target named in `CDEMO-TO-PROGRAM` from the shared `COCOM01Y` COMMAREA.
- `CSUTLDTC` is a shared date-validation utility wrapping `CEEDAYS`; called by `CORPT00C` and `COTRN02C`.
- `CBSTM03A` delegates all file I/O to `CBSTM03B` (called 11 times) — a classic main/subroutine split.
- All batch programs call `CEE3ABD` for abnormal termination.
- `CARDXREF` is the most-read dataset (10 programs read it).

## Complexity Hotspots

From `HOTSPOT_REPORT.md` (composite score across LOC, copybook count, I/O ops, IF/EVALUATE density, and dependencies):

| Program | LOC | COPY | I/O | IF/EVAL | Notes |
|---------|-----|------|-----|---------|-------|
| `COACTUPC.cbl` | 4,236 | 56 | 35 | 174 | Dominates every dimension; deep CICS+BMS coupling |
| `COCRDUPC.cbl` | 1,560 | 15 | — | 80 | Card update |
| `COCRDLIC.cbl` | 1,459 | 13 | 22 | 68 | Card list |
| `CBSTM03A.CBL` | 924 | 1 | 117 | — | Highest I/O density (statement generation) |
| `CBTRN02C.cbl` | 731 | — | 23 | 48 | Daily transaction posting (batch) |

## Modernization Approach

Recommended sequencing (from `HOTSPOT_REPORT.md`):

1. **Batch-first** — `CBTRN02C` and `CBACT04C` → Spring Batch jobs; no CICS coupling, easier to test in isolation.
2. **Shared utilities** — `CSUTLDTC` → `java.time`-based shared library; unblocks online program migration.
3. **High-complexity CICS** — `COACTUPC` → Spring Boot REST API + modern UI; tackle once batch patterns are proven.
4. **Card management** — `COCRDLIC`/`COCRDUPC`/`COCRDSLC` → cohesive Card Service.
5. **ETL** — `CBEXPORT`/`CBIMPORT` → Spring Batch with typed DTOs replacing `REDEFINES` layouts.

Key Java mapping concerns:
- `COMP-3` / `S9(n)V99` financial fields → `BigDecimal` (precision-critical).
- `REDEFINES` (e.g. `CVEXPORT.cpy`) → tagged unions or polymorphic DTOs.
- BMS maps → REST + modern frontend.
- VSAM KSDS → relational tables or document stores keyed on the existing primary keys.
- Clear-text passwords in `CSUSR01Y` (`SEC-USR-PWD PIC X(08)`) must be replaced with hashed credentials.

## Analysis Artifacts in This Repository

| Document | Purpose |
|----------|---------|
| `APPLICATION_INVENTORY.md` | Full catalog of programs, sub-applications, and JCL jobs |
| `DATA_DICTIONARY.md` | Field-level copybook documentation grouped by business entity |
| `DEPENDENCY_MAP.md` | Call graph, dataset lineage, and end-to-end batch pipeline (with Mermaid diagrams) |
| `HOTSPOT_REPORT.md` | Complexity rankings and modernization priorities |
| `PROJECT_SUMMARY.md` | This file — high-level entry point to the analysis |
