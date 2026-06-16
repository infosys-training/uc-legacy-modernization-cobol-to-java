# DEPENDENCY MAP — AWS CardDemo

> Program call graphs, dataset lineage, and end-to-end batch pipeline flows.

---

## 1. Program Call Graph

### 1.1 Online Navigation Tree (CICS XCTL/LINK)

```mermaid
graph TD
    COSGN00C["COSGN00C<br/>Sign-On"]
    COADM01C["COADM01C<br/>Admin Menu"]
    COMEN01C["COMEN01C<br/>Main Menu"]

    COSGN00C -->|"XCTL (Admin)"| COADM01C
    COSGN00C -->|"XCTL (User)"| COMEN01C

    %% Admin Hub
    COADM01C -->|XCTL| COUSR00C["COUSR00C<br/>User List"]
    COADM01C -->|XCTL| COUSR01C["COUSR01C<br/>User Add"]
    COADM01C -->|XCTL| COUSR02C["COUSR02C<br/>User Update"]
    COADM01C -->|XCTL| COUSR03C["COUSR03C<br/>User Delete"]
    COADM01C -->|XCTL| COTRTLIC["COTRTLIC<br/>Tran Type List (DB2)"]
    COADM01C -->|XCTL| COTRTUPC["COTRTUPC<br/>Tran Type Update (DB2)"]

    COUSR00C -->|XCTL| COUSR02C
    COUSR00C -->|XCTL| COUSR03C

    %% User Hub
    COMEN01C -->|XCTL| COACTVWC["COACTVWC<br/>Account View"]
    COMEN01C -->|XCTL| COACTUPC["COACTUPC<br/>Account Update"]
    COMEN01C -->|XCTL| COCRDLIC["COCRDLIC<br/>Card List"]
    COMEN01C -->|XCTL| COCRDSLC["COCRDSLC<br/>Card View"]
    COMEN01C -->|XCTL| COCRDUPC["COCRDUPC<br/>Card Update"]
    COMEN01C -->|XCTL| COTRN00C["COTRN00C<br/>Txn List"]
    COMEN01C -->|XCTL| COTRN01C["COTRN01C<br/>Txn View"]
    COMEN01C -->|XCTL| COTRN02C["COTRN02C<br/>Txn Add"]
    COMEN01C -->|XCTL| CORPT00C["CORPT00C<br/>Reports"]
    COMEN01C -->|XCTL| COBIL00C["COBIL00C<br/>Bill Payment"]
    COMEN01C -->|XCTL| COPAUS0C["COPAUS0C<br/>Auth Summary (IMS)"]

    COCRDLIC -->|XCTL| COCRDSLC
    COCRDLIC -->|XCTL| COCRDUPC
    COTRN00C -->|XCTL| COTRN01C
    COPAUS0C -->|LINK| COPAUS1C["COPAUS1C<br/>Auth Detail (IMS)"]
    COPAUS1C -->|LINK| COPAUS2C["COPAUS2C<br/>Mark Fraud (DB2)"]
```

### 1.2 Batch Program Call Relationships

| Caller | Callee | Mechanism | Purpose |
|--------|--------|-----------|---------|
| CBACT01C | COBDATFT | CALL | Assembler date formatting |
| CBACT02C | CEE3ABD | CALL | LE abnormal termination |
| CBACT03C | CEE3ABD | CALL | LE abnormal termination |
| CBCUS01C | CEE3ABD | CALL | LE abnormal termination |
| CBSTM03A | CBSTM03B | CALL | I/O submodule for statement generation |
| COBSWAIT | MVSWAIT | CALL | Assembler wait routine |
| CSUTLDTC | CEEDAYS | CALL | LE date conversion (Lilian days) |
| CORPT00C | CSUTLDTC | CALL (LINK) | Date validation for report parameters |
| COTRN02C | CSUTLDTC | CALL (LINK) | Date validation for transaction dates |

### 1.3 External Dependencies

| External Routine | Type | Called By | Purpose |
|-----------------|------|-----------|---------|
| COBDATFT | Assembler | CBACT01C | Date formatting for account reports |
| MVSWAIT | Assembler | COBSWAIT | Wait for specified centiseconds |
| CEE3ABD | LE Runtime | CBACT02C, CBACT03C, CBCUS01C | Abnormal termination (abend) |
| CEEDAYS | LE Runtime | CSUTLDTC | Convert date to Lilian day number |
| DFHCSDUP | CICS Utility | CBADMCDJ.jcl | CSD (resource definition) updates |
| DSNTIAC | DB2 Utility | COTRTLIC, COTRTUPC | Format DB2 error messages |

---

## 2. Dataset Lineage

### 2.1 VSAM File Usage Matrix

| Dataset (VSAM KSDS) | JCL Define Job | Writers (Programs) | Readers (Programs) |
|---------------------|---------------|-------------------|-------------------|
| **ACCTFILE** (ACCTDATA) | ACCTFILE.jcl | CBACT04C (rewrite), CBTRN02C (rewrite), COACTUPC, COBIL00C, CBIMPORT | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, COACTVWC, COACTUPC, COBIL00C, COTRN02C, CBEXPORT, CBSTM03A |
| **CARDFILE** (CARDDATA) | CARDFILE.jcl | COCRDUPC, CBIMPORT | CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, COACTUPC, CBTRN01C, CBEXPORT |
| **CUSTFILE** (CUSTDATA) | CUSTFILE.jcl | CBIMPORT | CBCUS01C, CBTRN01C, COACTVWC, COACTUPC, CBEXPORT, CBSTM03A |
| **CARDXREF** (CARDXREF) | XREFFILE.jcl | CBIMPORT | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, COACTVWC, COACTUPC, COTRN02C, COBIL00C, CBEXPORT, CBSTM03A |
| **TRANSACT** (TRANSACT) | TRANFILE.jcl | CBTRN02C, CBACT04C, COTRN02C, COBIL00C, CBIMPORT | COTRN00C, COTRN01C, CORPT00C, CBTRN03C, CBEXPORT, CBSTM03A |
| **TCATBALF** (TCATBALF) | TCATBALF.jcl | CBTRN02C (rewrite) | CBACT04C |
| **DISCGRP** (DISCGRP) | DISCGRP.jcl | (loaded externally) | CBACT04C |
| **TRANTYPE** (TRANTYPE) | TRANTYPE.jcl | (loaded externally) | CBTRN03C |
| **TRANCATG** (TRANCATG) | TRANCATG.jcl | (loaded externally) | CBTRN03C |
| **USRSEC** (USRSEC) | DUSRSECJ.jcl | COUSR01C, COUSR02C, COUSR03C | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |

### 2.2 Non-VSAM Datasets

| Dataset | Type | JCL Job | Writers | Readers |
|---------|------|---------|---------|---------|
| DALYTRAN | PS (Physical Sequential) | (external feed) | External system | CBTRN01C, CBTRN02C |
| DALYREJS | GDG | DALYREJS.jcl (base) | CBTRN02C | (review/audit) |
| TRANSACT (sequential output) | GDG | DEFGDGB.jcl | CBACT04C (interest txns) | (feeds back to VSAM) |
| REPTFILE | GDG | REPTFILE.jcl | CBTRN03C | (print/archive) |
| EXPFILE | PS | CBEXPORT.jcl | CBEXPORT | CBIMPORT |

### 2.3 JCL Job → Dataset → Program Mapping

```mermaid
graph LR
    subgraph "POSTTRAN.jcl"
        PT_PGM["CBTRN02C"]
    end

    DALYTRAN_DS[("DALYTRAN<br/>(PS Input)")]
    XREFFILE_DS[("CARDXREF<br/>(VSAM)")]
    ACCTFILE_DS[("ACCTFILE<br/>(VSAM)")]
    TCATBALF_DS[("TCATBALF<br/>(VSAM)")]
    TRANFILE_DS[("TRANSACT<br/>(VSAM)")]
    DALYREJS_DS[("DALYREJS<br/>(GDG Output)")]

    DALYTRAN_DS -->|READ| PT_PGM
    XREFFILE_DS -->|READ| PT_PGM
    PT_PGM -->|I-O| ACCTFILE_DS
    PT_PGM -->|I-O| TCATBALF_DS
    PT_PGM -->|WRITE| TRANFILE_DS
    PT_PGM -->|WRITE| DALYREJS_DS
```

```mermaid
graph LR
    subgraph "INTCALC.jcl"
        IC_PGM["CBACT04C"]
    end

    TCATBALF_IC[("TCATBALF<br/>(VSAM)")]
    XREFFILE_IC[("CARDXREF<br/>(VSAM)")]
    DISCGRP_IC[("DISCGRP<br/>(VSAM)")]
    ACCTFILE_IC[("ACCTFILE<br/>(VSAM)")]
    TRANSACT_IC[("TRANSACT<br/>(GDG Output)")]

    TCATBALF_IC -->|READ| IC_PGM
    XREFFILE_IC -->|READ| IC_PGM
    DISCGRP_IC -->|READ| IC_PGM
    IC_PGM -->|I-O| ACCTFILE_IC
    IC_PGM -->|WRITE| TRANSACT_IC
```

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Processing Pipeline

```mermaid
graph TD
    subgraph "Phase 1: Data Loading"
        A1["ACCTFILE.jcl<br/>Define VSAM KSDS"]
        A2["CARDFILE.jcl<br/>Define VSAM KSDS"]
        A3["CUSTFILE.jcl<br/>Define VSAM KSDS"]
        A4["XREFFILE.jcl<br/>Define VSAM KSDS"]
        A5["TRANFILE.jcl<br/>Define VSAM KSDS"]
    end

    subgraph "Phase 2: Daily Transaction Posting"
        B1["POSTTRAN.jcl<br/>PGM=CBTRN02C"]
        B1_IN["Input: DALYTRAN (daily feed)"]
        B1_OUT["Output: TRANSACT, DALYREJS"]
        B1_UPD["Updates: ACCTFILE, TCATBALF"]
    end

    subgraph "Phase 3: Interest Calculation"
        C1["INTCALC.jcl<br/>PGM=CBACT04C"]
        C1_IN["Input: TCATBALF, XREFFILE, DISCGRP"]
        C1_OUT["Output: TRANSACT (interest txns)"]
        C1_UPD["Updates: ACCTFILE (balance)"]
    end

    subgraph "Phase 4: Statement Generation"
        D1["CREASTMT.JCL<br/>PGM=CBSTM03A"]
        D1_IN["Input: XREFFILE, CUSTFILE,<br/>ACCTFILE, TRANSACT"]
        D1_OUT["Output: STMTFILE (text),<br/>HTMLFILE (HTML)"]
    end

    subgraph "Phase 5: Reporting"
        E1["TRANREPT.jcl<br/>PGM=CBTRN03C"]
        E1_IN["Input: TRANSACT, CARDXREF,<br/>TRANTYPE, TRANCATG"]
        E1_OUT["Output: REPTFILE"]
    end

    A1 --> B1
    A4 --> B1
    B1_IN --> B1
    B1 --> B1_OUT
    B1 --> B1_UPD
    B1_UPD --> C1
    C1_IN --> C1
    C1 --> C1_OUT
    C1 --> C1_UPD
    C1_UPD --> D1
    D1_IN --> D1
    D1 --> D1_OUT
    D1_OUT --> E1
    E1_IN --> E1
    E1 --> E1_OUT
```

### 3.2 Pipeline Step Details

| Step | JCL Job | Program | Input Datasets | Output Datasets | Updates | Condition |
|------|---------|---------|---------------|-----------------|---------|-----------|
| 1 | POSTTRAN.jcl | CBTRN02C | DALYTRAN (PS), XREFFILE (VSAM) | TRANFILE (VSAM), DALYREJS (GDG+1) | ACCTFILE, TCATBALF | Daily — processes incoming transactions |
| 2 | INTCALC.jcl | CBACT04C | TCATBALF, XREFFILE, DISCGRP (all VSAM) | TRANSACT (GDG+1) — interest transaction records | ACCTFILE (balance update) | Daily — after POSTTRAN completes |
| 3 | CREASTMT.JCL | CBSTM03A→CBSTM03B | XREFFILE, CUSTFILE, ACCTFILE, TRANSACT (VSAM) | STMTFILE (text), HTMLFILE (HTML) | (none) | Monthly/On-demand — statement generation |
| 4 | TRANREPT.jcl | CBTRN03C (via REPROC) | TRANSACT, CARDXREF, TRANTYPE, TRANCATG | REPTFILE (GDG) | (none) | Daily — after INTCALC |

### 3.3 Export/Import Pipeline (Branch Migration)

```mermaid
graph LR
    subgraph "Export (Source Branch)"
        EX["CBEXPORT.jcl<br/>PGM=CBEXPORT"]
        EX_IN["CUSTFILE + ACCTFILE +<br/>XREFFILE + TRANSACT + CARDFILE"]
        EX_OUT["EXPFILE<br/>(500-byte multi-record)"]
        EX_IN --> EX --> EX_OUT
    end

    EX_OUT -->|"File Transfer"| IM

    subgraph "Import (Target Branch)"
        IM["CBIMPORT.jcl<br/>PGM=CBIMPORT"]
        IM_OUT["CUSTOUT + ACCTOUT +<br/>XREFOUT + TRNXOUT +<br/>CARDOUT + ERROUT"]
        IM --> IM_OUT
    end
```

### 3.4 IMS Batch Pipeline (Authorization Subsystem)

| Step | Program | Purpose | IMS Operations |
|------|---------|---------|----------------|
| Load | PAUDBLOD | Load authorization data into IMS database | ISRT, GU |
| Purge | CBPAUP0C | Delete expired pending authorizations | GN, GNP, DLET |
| Unload | PAUDBUNL | Unload IMS database to sequential file | GN, GNP |
| GSAM Unload | DBUNLDGS | Unload via GSAM for external processing | GN, GNP, ISRT |

---

## 4. Control-M Scheduling (`app/scheduler/`)

### 4.1 Daily Cycle (Weekday)

```
POSTTRAN (CBTRN02C)
    ↓ [CC=0]
INTCALC (CBACT04C)
    ↓ [CC=0]
TRANREPT (CBTRN03C)
```

### 4.2 Monthly Cycle

```
CREASTMT (CBSTM03A) — Full statement generation for all accounts
```

### 4.3 Weekly Cycle

```
CBEXPORT — Branch data export for migration/backup
TRANBKP  — Transaction file backup
```

### 4.4 On-Demand (from CICS)

```
CORPT00C → submits INTRDRJ1.JCL → triggers INTRDRJ2.JCL
    (Internal reader chain for ad-hoc reports)
```

---

## 5. Cross-System Integration Points

### 5.1 CICS ↔ Batch Bridge

| Online Program | Batch Trigger | Mechanism |
|---------------|--------------|-----------|
| CORPT00C | INTRDRJ1/J2 | TDQ (Transient Data Queue) → Internal Reader |
| COBIL00C | (indirect) | Writes TRANSACT → picked up by TRANREPT batch |
| COTRN02C | (indirect) | Writes TRANSACT → picked up by INTCALC batch |

### 5.2 MQ Integration (Authorization Flow)

```mermaid
sequenceDiagram
    participant EXT as External System
    participant MQ as MQ Queue
    participant AUTH as COPAUA0C (CICS)
    participant IMS as IMS Database
    participant DB2 as DB2

    EXT->>MQ: MQPUT (Auth Request - CCPAURQY format)
    AUTH->>MQ: MQGET (Read request)
    AUTH->>IMS: GU/GNP (Lookup account/card status)
    AUTH->>AUTH: Decision Logic (approve/decline)
    AUTH->>IMS: REPL (Update auth summary - CIPAUSMY)
    AUTH->>DB2: INSERT (Log fraud flag if declined)
    AUTH->>MQ: MQPUT1 (Auth Response - CCPAURLY format)
    MQ->>EXT: Response delivered
```

### 5.3 IMS Database Hierarchy

```
Root Segment: CIPAUSMY (Authorization Summary)
    Key: PA-ACCT-ID
    │
    └── Dependent Segment: CIPAUDTY (Authorization Detail)
            Key: PA-AUTH-DATE-9C + PA-AUTH-TIME-9C
```

**DL/I Access Patterns:**
- `GU` (Get Unique) — Direct key lookup
- `GN` (Get Next) — Sequential forward read
- `GNP` (Get Next within Parent) — Read children of current root
- `ISRT` (Insert) — Add new segment
- `REPL` (Replace) — Update segment in place
- `DLET` (Delete) — Remove segment
