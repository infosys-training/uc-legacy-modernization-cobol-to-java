# CardDemo Dependency Map

Inter-program call graphs, dataset lineage, and end-to-end batch pipeline flows for the CardDemo application.

---

## A. Call Graph — Online (CICS) Programs

All online programs communicate through the `CARDDEMO-COMMAREA` (COCOM01Y) passed via `EXEC CICS XCTL`.

```mermaid
graph TD
    COSGN00C["COSGN00C (CC00)\nSignon"] -->|"Admin user"| COADM01C["COADM01C (CA00)\nAdmin Menu"]
    COSGN00C -->|"Regular user"| COMEN01C["COMEN01C (CM00)\nMain Menu"]

    COADM01C -->|"Option 1"| COUSR00C["COUSR00C (CU00)\nUser List"]
    COADM01C -->|"Option 2"| COUSR01C["COUSR01C (CU01)\nUser Add"]
    COADM01C -->|"Option 3"| COUSR02C["COUSR02C (CU02)\nUser Update"]
    COADM01C -->|"Option 4"| COUSR03C["COUSR03C (CU03)\nUser Delete"]
    COADM01C -->|"Option 5"| COTRTLIC["COTRTLIC (CTLI)\nTran Type List (DB2)"]
    COADM01C -->|"Option 6"| COTRTUPC["COTRTUPC (CTTU)\nTran Type Maint (DB2)"]

    COMEN01C -->|"Option 1"| COACTVWC["COACTVWC (CAVW)\nAccount View"]
    COMEN01C -->|"Option 2"| COACTUPC["COACTUPC (CAUP)\nAccount Update"]
    COMEN01C -->|"Option 3"| COCRDLIC["COCRDLIC (CCLI)\nCard List"]
    COMEN01C -->|"Option 6"| COTRN00C["COTRN00C (CT00)\nTransaction List"]
    COMEN01C -->|"Option 8"| COTRN02C["COTRN02C (CT02)\nAdd Transaction"]
    COMEN01C -->|"Option 9"| CORPT00C["CORPT00C (CR00)\nReport Submission"]
    COMEN01C -->|"Option 10"| COBIL00C["COBIL00C (CB00)\nBill Payment"]
    COMEN01C -->|"Option 11"| COPAUS0C["COPAUS0C (CPVS)\nAuth Summary"]

    COCRDLIC -->|"Select for view"| COCRDSLC["COCRDSLC (CCDL)\nCard Detail"]
    COCRDLIC -->|"Select for update"| COCRDUPC["COCRDUPC (CCUP)\nCard Update"]
    COTRN00C -->|"Select"| COTRN01C["COTRN01C (CT01)\nTransaction View"]

    COTRN02C -->|"CALL"| CSUTLDTC["CSUTLDTC\nDate Validation"]
    CORPT00C -->|"CALL"| CSUTLDTC

    COPAUS0C -->|"Select"| COPAUS1C["COPAUS1C (CPVD)\nAuth Detail"]
    COPAUS1C -->|"PF5 Fraud"| COPAUS2C["COPAUS2C\nFraud Mark (DB2)"]
```

### Return Navigation

All programs return to their parent menu via XCTL using `CDEMO-TO-PROGRAM` from the COMMAREA. Card/Account/Transaction programs return to COMEN01C; User management programs return to COADM01C.

---

## B. Call Graph — Batch Programs

```mermaid
graph TD
    CBSTM03A["CBSTM03A\nStatement Generator"] -->|"CALL (13x)"| CBSTM03B["CBSTM03B\nFile I/O Subroutine"]
    COBSWAIT["COBSWAIT\nWait Utility"] -->|"CALL"| MVSWAIT["MVSWAIT\n(External wait routine)"]
```

Batch programs are standalone executables invoked by JCL (see Section D). No batch-to-batch CALL relationships exist beyond CBSTM03A→CBSTM03B.

---

## C. Dataset Lineage

### C.1 Dataset Producer/Consumer Map

```mermaid
graph LR
    subgraph "Definition Jobs"
        ACCTFILE_JCL["ACCTFILE.jcl"]
        CARDFILE_JCL["CARDFILE.jcl"]
        CUSTFILE_JCL["CUSTFILE.jcl"]
        XREFFILE_JCL["XREFFILE.jcl"]
        TRANFILE_JCL["TRANFILE.jcl"]
        TCATBALF_JCL["TCATBALF.jcl"]
        DISCGRP_JCL["DISCGRP.jcl"]
        TRANTYPE_JCL["TRANTYPE.jcl"]
        TRANCATG_JCL["TRANCATG.jcl"]
        DUSRSECJ_JCL["DUSRSECJ.jcl"]
    end

    subgraph "VSAM Datasets"
        ACCTFILE["ACCTFILE\n(Account KSDS)"]
        CARDFILE["CARDFILE\n(Card KSDS)"]
        CUSTFILE["CUSTFILE\n(Customer KSDS)"]
        XREFFILE["XREFFILE\n(Card Xref KSDS)"]
        TRANSACT["TRANSACT/TRANFILE\n(Transaction KSDS)"]
        TCATBALF["TCATBALF\n(Category Balance KSDS)"]
        DISCGRP["DISCGRP\n(Discount Group KSDS)"]
        TRANTYPE["TRANTYPE\n(Tran Type KSDS)"]
        TRANCATG["TRANCATG\n(Tran Category KSDS)"]
        USRSEC["USRSEC\n(User Security KSDS)"]
    end

    ACCTFILE_JCL --> ACCTFILE
    CARDFILE_JCL --> CARDFILE
    CUSTFILE_JCL --> CUSTFILE
    XREFFILE_JCL --> XREFFILE
    TRANFILE_JCL --> TRANSACT
    TCATBALF_JCL --> TCATBALF
    DISCGRP_JCL --> DISCGRP
    TRANTYPE_JCL --> TRANTYPE
    TRANCATG_JCL --> TRANCATG
    DUSRSECJ_JCL --> USRSEC
```

### C.2 Batch Program File Access

| Dataset | Producers (Write/I-O) | Consumers (Read) |
|:--------|:----------------------|:------------------|
| ACCTFILE | ACCTFILE.jcl (define/load), CBTRN02C (I-O), CBACT04C (I-O), COBIL00C (CICS REWRITE) | CBACT01C, CBTRN01C, CBTRN02C, CBACT04C, CBEXPORT, CBSTM03A, COACTVWC, COACTUPC, COBIL00C |
| CARDFILE | CARDFILE.jcl (define/load) | CBACT02C, CBTRN01C, CBEXPORT, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC |
| CUSTFILE | CUSTFILE.jcl (define/load) | CBCUS01C, CBTRN01C, CBEXPORT, CBSTM03A, COACTVWC, COACTUPC, COCRDSLC, COCRDUPC |
| XREFFILE | XREFFILE.jcl (define/load) | CBACT03C, CBTRN01C, CBTRN02C, CBACT04C, CBEXPORT, CBSTM03A, COACTVWC, COACTUPC, COBIL00C, COTRN02C |
| TRANSACT / TRANFILE | TRANFILE.jcl (define/load), CBTRN01C (W), CBTRN02C (W), CBACT04C (W), COBIL00C (CICS WRITE) | CBEXPORT, CBTRN03C, CBSTM03A, COTRN00C, COTRN01C |
| DALYTRAN | *(External feed — input to batch)* | CBTRN01C, CBTRN02C |
| DALYREJS | CBTRN02C (W) | *(Reviewed manually or by downstream processes)* |
| TCATBALF | TCATBALF.jcl (define/load), CBTRN02C (I-O), CBACT04C (R) | CBTRN02C, CBACT04C |
| DISCGRP | DISCGRP.jcl (define/load) | CBACT04C |
| TRANTYPE | TRANTYPE.jcl (define/load) | CBTRN03C |
| TRANCATG | TRANCATG.jcl (define/load) | CBTRN03C |
| USRSEC | DUSRSECJ.jcl (define/load), COUSR01C (CICS WRITE), COUSR02C (CICS REWRITE), COUSR03C (CICS DELETE) | COSGN00C, COUSR00C, COUSR02C |
| EXPFILE | CBEXPORT (W) | CBIMPORT (R) |
| STMTFILE | CBSTM03A (W) | TXT2PDF1.JCL (convert to PDF) |
| HTMLFILE | CBSTM03A (W) | *(External — HTML statement output)* |
| TRANREPT | CBTRN03C (W) | *(External — printed report output)* |

---

## D. End-to-End Batch Pipeline Flow

```mermaid
graph TD
    DALYTRAN["Daily Transaction Feed\n(DALYTRAN)"] --> POSTTRAN

    subgraph "POSTTRAN.jcl"
        POSTTRAN["CBTRN02C\nValidate & Post Transactions"]
    end

    POSTTRAN -->|"Valid transactions"| TRANSACT["TRANSACT\n(Transaction VSAM)"]
    POSTTRAN -->|"Rejected transactions"| DALYREJS["DALYREJS\n(Daily Rejects)"]
    POSTTRAN -->|"Update balances"| ACCTFILE["ACCTFILE\n(Account VSAM)"]
    POSTTRAN -->|"Update category balances"| TCATBALF["TCATBALF\n(Category Balance)"]

    TRANSACT --> INTCALC
    TCATBALF --> INTCALC
    XREFFILE["XREFFILE\n(Card Xref)"] --> INTCALC
    DISCGRP["DISCGRP\n(Discount Groups)"] --> INTCALC

    subgraph "INTCALC.jcl"
        INTCALC["CBACT04C\nCalculate Interest"]
    end

    INTCALC -->|"Interest transactions"| TRANSACT2["TRANSACT\n(appended)"]
    INTCALC -->|"Update balances"| ACCTFILE2["ACCTFILE\n(updated)"]

    TRANSACT --> TRANREPT_JOB
    TRANTYPE["TRANTYPE\n(Transaction Types)"] --> TRANREPT_JOB
    TRANCATG["TRANCATG\n(Transaction Categories)"] --> TRANREPT_JOB
    CARDXREF["CARDXREF / XREFFILE"] --> TRANREPT_JOB
    DATEPARM["DATEPARM\n(Date Range)"] --> TRANREPT_JOB

    subgraph "TRANREPT.jcl"
        TRANREPT_JOB["CBTRN03C\nGenerate Transaction Report"]
    end

    TRANREPT_JOB --> TRANREPT_OUT["TRANREPT\n(Formatted Report)"]

    TRANSACT --> CREASTMT
    XREFFILE --> CREASTMT
    ACCTFILE --> CREASTMT
    CUSTFILE["CUSTFILE\n(Customer VSAM)"] --> CREASTMT

    subgraph "CREASTMT.JCL"
        CREASTMT["CBSTM03A + CBSTM03B\nGenerate Statements"]
    end

    CREASTMT --> STMTFILE["STMTFILE\n(Text Statements)"]
    CREASTMT --> HTMLFILE["HTMLFILE\n(HTML Statements)"]
```

### Pipeline Execution Order

1. **POSTTRAN.jcl** (CBTRN02C) — Reads DALYTRAN feed, validates each transaction against XREFFILE/ACCTFILE, posts valid transactions to TRANSACT, rejects invalid ones to DALYREJS, updates ACCTFILE balances and TCATBALF category balances.

2. **INTCALC.jcl** (CBACT04C) — Reads TCATBALF category balances, looks up interest rates via DISCGRP using ACCT-GROUP-ID, calculates interest, writes interest transactions to TRANSACT, updates ACCTFILE balances.

3. **TRANREPT.jcl** (CBTRN03C via TRANREPT.prc) — Unloads TRANSACT via REPROC procedure, sorts by card number, reads TRANTYPE and TRANCATG for descriptions, filters by DATEPARM date range, writes formatted report with page/account/grand totals.

4. **CREASTMT.JCL** (CBSTM03A calling CBSTM03B) — Sorts TRANSACT by card+ID into TRNXFILE, reads account/customer/xref data, generates text statements (STMTFILE) and HTML statements (HTMLFILE).

---

## E. Export/Import Data Flow

```mermaid
graph LR
    CUSTFILE["CUSTFILE"] --> CBEXPORT["CBEXPORT\n(Export)"]
    ACCTFILE["ACCTFILE"] --> CBEXPORT
    XREFFILE["XREFFILE"] --> CBEXPORT
    TRANSACT["TRANSACT"] --> CBEXPORT
    CARDFILE["CARDFILE"] --> CBEXPORT

    CBEXPORT --> EXPFILE["EXPFILE\n(Multi-record Export)"]

    EXPFILE --> CBIMPORT["CBIMPORT\n(Import)"]

    CBIMPORT --> CUSTOUT["CUSTOUT"]
    CBIMPORT --> ACCTOUT["ACCTOUT"]
    CBIMPORT --> XREFOUT["XREFOUT"]
    CBIMPORT --> TRNXOUT["TRNXOUT"]
    CBIMPORT --> CARDOUT["CARDOUT"]
    CBIMPORT --> ERROUT["ERROUT\n(Errors)"]
```

The export/import pipeline supports branch migration: CBEXPORT reads all five normalized VSAM files and writes a single sequential export file (EXPFILE) using the multi-record CVEXPORT copybook layout. CBIMPORT reads the export file, validates record types, and writes to individual output files for loading at the target branch.

---

## F. Shared Copybook Dependencies

All online CICS programs share a common set of infrastructure copybooks:

| Copybook | Purpose | Used By |
|:---------|:--------|:--------|
| COCOM01Y | CARDDEMO-COMMAREA — inter-program communication | All 18 online programs |
| COTTL01Y | Screen title constants | All 18 online programs |
| CSDAT01Y | Date/time working storage | All 18 online programs |
| CSMSG01Y | Common screen messages | All 18 online programs |
| DFHAID | CICS attention identifiers | All 18 online programs |
| DFHBMSCA | BMS screen attributes | All 18 online programs |
| CSUSR01Y | User security record | COSGN00C, COADM01C, COMEN01C, COUSR00C–03C, COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC |
| CVCRD01Y | CICS card work areas | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC |
| CSSTRPFY | Store PF key procedure | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC |
| CSSETATY | Set field attributes (template) | COACTUPC (21 inclusions), COCRDUPC |
| CSUTLDWY | Date validation working storage | COACTUPC |
| CSUTLDPY | Date validation procedure | COACTUPC |
| CSLKPCDY | Lookup codes (area codes, states) | COACTUPC |
