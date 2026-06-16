# DEPENDENCY MAP — CardDemo COBOL Estate

> **Scope:** 44 COBOL programs, 46 JCL jobs, 14 VSAM datasets
> **Key Patterns:** Hub-and-spoke CICS navigation, sequential batch pipeline, IMS DL/I chains, MQ request/reply

---

## 1. Online (CICS) Call Graph

### 1.1 Navigation Tree — XCTL Chains

```
COSGN00C  (Sign-On — Entry Point)
│
├─ [Admin path: SEC-USR-TYPE = 'A']
│  └── COADM01C  (Admin Menu — 6 options)
│      ├── COUSR00C  (User List) ──XCTL──► COUSR02C (User Update)
│      │                         ──XCTL──► COUSR03C (User Delete)
│      ├── COUSR01C  (User Add)
│      ├── COTRTLIC  (Transaction Type List — DB2 cursor)
│      │   └──XCTL──► COTRTUPC (Transaction Type Update — DB2 CRUD)
│      └── COTRTUPC  (Transaction Type Update — direct access)
│
└─ [Regular user path: SEC-USR-TYPE = 'U']
   └── COMEN01C  (Main Menu — Hub, 11 options)
       ├── COACTVWC  (Account View)
       ├── COACTUPC  (Account Update — 4,236 LOC)
       ├── COCRDLIC  (Card List) ──XCTL──► COCRDSLC (Card Detail)
       │                         ──XCTL──► COCRDUPC (Card Update)
       ├── COTRN00C  (Transaction List) ──XCTL──► COTRN01C (Transaction View)
       ├── COTRN02C  (Transaction Add)
       ├── CORPT00C  (Report Request) ──submit──► INTRDRJ1.JCL / INTRDRJ2.JCL
       ├── COBIL00C  (Bill Payment)
       └── COPAUS0C  (Auth Summary — IMS)
            └── COPAUS1C (Auth Detail — IMS)
                └── COPAUS2C (Mark Fraud — DB2 INSERT)
```

### 1.2 CICS Inter-Program Relationships

| Source Program | Target Program | Transfer Method | Context Passed |
|---------------|---------------|-----------------|---------------|
| COSGN00C | COADM01C | XCTL | COMMAREA (COCOM01Y) |
| COSGN00C | COMEN01C | XCTL | COMMAREA |
| COADM01C | COUSR00C | XCTL | COMMAREA |
| COADM01C | COUSR01C | XCTL | COMMAREA |
| COADM01C | COUSR02C | XCTL | COMMAREA |
| COADM01C | COUSR03C | XCTL | COMMAREA |
| COADM01C | COTRTLIC | XCTL | COMMAREA |
| COADM01C | COTRTUPC | XCTL | COMMAREA |
| COMEN01C | COACTVWC | XCTL | COMMAREA |
| COMEN01C | COACTUPC | XCTL | COMMAREA |
| COMEN01C | COCRDLIC | XCTL | COMMAREA |
| COMEN01C | COCRDSLC | XCTL | COMMAREA |
| COMEN01C | COCRDUPC | XCTL | COMMAREA |
| COMEN01C | COTRN00C | XCTL | COMMAREA |
| COMEN01C | COTRN01C | XCTL | COMMAREA |
| COMEN01C | COTRN02C | XCTL | COMMAREA |
| COMEN01C | CORPT00C | XCTL | COMMAREA |
| COMEN01C | COBIL00C | XCTL | COMMAREA |
| COMEN01C | COPAUS0C | XCTL | COMMAREA |
| COCRDLIC | COCRDSLC | XCTL | COMMAREA (card-num in context) |
| COCRDLIC | COCRDUPC | XCTL | COMMAREA (card-num in context) |
| COTRN00C | COTRN01C | XCTL | COMMAREA (tran-id in context) |
| COTRTLIC | COTRTUPC | XCTL | COMMAREA |
| COPAUS0C | COPAUS1C | LINK | COMMAREA (auth-msg-key) |
| COPAUS1C | COPAUS2C | LINK | COMMAREA (fraud marker) |
| CORPT00C | CSUTLDTC | CALL | LINKAGE SECTION (date params) |
| All online → | COMEN01C or COADM01C | XCTL (PF3 return) | COMMAREA |

### 1.3 Hub Dependency Counts

```
COMEN01C ──► 11 direct targets  (highest fan-out in online estate)
COADM01C ──► 6 direct targets
COSGN00C ──► 2 direct targets   (entry-point fan-out)
```

---

## 2. Batch Call Graph

### 2.1 Program-to-Program CALL Relationships

```
CBACT01C ──CALL──► COBDATFT    (Assembler: date formatter)
         ──CALL──► CEE3ABD     (LE: abnormal termination)

CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B   (I/O subroutine, called 13 times)
         ──CALL──► CEE3ABD

CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT    (Assembler: system wait)

CSUTLDTC ──CALL──► CEEDAYS    (LE: date conversion to Lilian days)

PAUDBLOD ──CALL──► CBLTDLI    (IMS DL/I: GU, ISRT)
PAUDBUNL ──CALL──► CBLTDLI    (IMS DL/I: GN, GNP)
DBUNLDGS ──CALL──► CBLTDLI    (IMS DL/I: GN, GNP, ISRT)
```

### 2.2 MQ API Calls (Cross-Subsystem)

```
COPAUA0C ──► MQOPEN  (open request queue)
         ──► MQGET   (get authorization request)
         ──► MQPUT1  (put response/error to reply queue)
         ──► MQCLOSE (close queue)

COACCT01 ──► MQOPEN  (3 queues: request, reply, dead-letter)
         ──► MQGET   (get account inquiry)
         ──► MQPUT   (put response)
         ──► MQCLOSE (3 queues)

CODATE01 ──► MQOPEN  (3 queues)
         ──► MQGET   (get date request)
         ──► MQPUT   (put date response)
         ──► MQCLOSE (3 queues)
```

### 2.3 IMS DL/I Call Graph

```
PAUDBLOD ──CBLTDLI──► PAUTBPCB (PCB)
         │            ├── GU  (Get Unique — position to root)
         │            └── ISRT (Insert summary + detail segments)
         └── Reads: INFILE1 (summary), INFILE2 (detail)

PAUDBUNL ──CBLTDLI──► PAUTBPCB (PCB)
         │            ├── GN  (Get Next — sequential scan)
         │            └── GNP (Get Next in Parent — child segments)
         └── Writes: OPFILE1 (summary), OPFILE2 (detail)

DBUNLDGS ──CBLTDLI──► PAUTBPCB + PASFLPCB + PADFLPCB (3 PCBs)
         │            ├── GN/GNP (read segments)
         │            └── ISRT (write to GSAM output)
         └── Writes: GSAM flat file

CBPAUP0C ──(IMS batch)──► GN, GNP, DLET (read + delete expired)

COPAUS0C ──(CICS/IMS)──► GU, GNP (browse summary/detail)
COPAUS1C ──(CICS/IMS)──► GU, GNP, REPL (read + update detail)
COPAUS2C ──(CICS/DB2)──► EXEC SQL INSERT (fraud log)
```

---

## 3. Dataset Lineage

### 3.1 VSAM Dataset — Program Mapping

```
┌──────────────────────────────────────────────────────────────────┐
│                      VSAM DATASET LINEAGE                        │
├──────────────┬──────────────────────┬────────────────────────────┤
│ Dataset      │ Writers (W/Rewrite)  │ Readers                    │
├──────────────┼──────────────────────┼────────────────────────────┤
│ ACCTDATA     │ CBACT04C (rewrite)   │ CBACT01C, CBACT04C,        │
│ (Account)    │ COACTUPC (rewrite)   │ CBTRN01C, CBTRN02C,        │
│              │ COBIL00C (rewrite)   │ COACTUPC, COACTVWC,        │
│              │ CBTRN02C (rewrite)   │ COBIL00C, COTRN02C,        │
│              │ CBIMPORT (write)     │ COPAUA0C, COPAUS0C,        │
│              │                      │ COACCT01, CBEXPORT         │
├──────────────┼──────────────────────┼────────────────────────────┤
│ CARDDATA     │ COCRDUPC (rewrite)   │ CBACT02C, COCRDLIC,        │
│ (Card)       │ CBIMPORT (write)     │ COCRDSLC, COCRDUPC,        │
│              │                      │ COACTVWC, COPAUS0C,        │
│              │                      │ CBEXPORT                   │
├──────────────┼──────────────────────┼────────────────────────────┤
│ CUSTDATA     │ CBIMPORT (write)     │ CBCUS01C, CBTRN01C,        │
│ (Customer)   │                      │ COACTUPC, COACTVWC,        │
│              │                      │ COCRDSLC, COCRDUPC,        │
│              │                      │ COPAUA0C, COPAUS0C,        │
│              │                      │ CBEXPORT                   │
├──────────────┼──────────────────────┼────────────────────────────┤
│ CARDXREF     │ CBIMPORT (write)     │ CBACT03C, CBACT04C,        │
│ (XREF)       │                      │ CBTRN01C, CBTRN02C,        │
│              │                      │ CBTRN03C, COACTUPC,        │
│              │                      │ COACTVWC, COTRN02C,        │
│              │                      │ COBIL00C, COPAUA0C,        │
│              │                      │ CBSTM03A, CBEXPORT         │
├──────────────┼──────────────────────┼────────────────────────────┤
│ TRANSACT     │ CBTRN02C (write)     │ CBTRN03C, COTRN00C,        │
│ (Transaction)│ COTRN02C (write)     │ COTRN01C, COTRN02C,        │
│              │ COBIL00C (write)     │ COBIL00C, CORPT00C,        │
│              │ CBIMPORT (write)     │ CBSTM03A, CBEXPORT         │
├──────────────┼──────────────────────┼────────────────────────────┤
│ TCATBALF     │ CBTRN02C (write)     │ CBACT04C                   │
│ (Cat Bal)    │                      │                            │
├──────────────┼──────────────────────┼────────────────────────────┤
│ DISCGRP      │ *(setup JCL only)*   │ CBACT04C                   │
│ (Disc Group) │                      │                            │
├──────────────┼──────────────────────┼────────────────────────────┤
│ TRANTYPE     │ COTRTUPC (DB2 sync)  │ CBTRN03C, COTRTLIC         │
├──────────────┼──────────────────────┼────────────────────────────┤
│ TRANCATG     │ *(setup JCL only)*   │ CBTRN03C                   │
├──────────────┼──────────────────────┼────────────────────────────┤
│ USRSEC       │ COUSR01C (write)     │ COSGN00C, COUSR00C,        │
│ (Security)   │ COUSR02C (rewrite)   │ COUSR01C, COUSR02C,        │
│              │ COUSR03C (delete)    │ COUSR03C                   │
├──────────────┼──────────────────────┼────────────────────────────┤
│ DALYTRAN     │ *(external feed)*    │ CBTRN01C, CBTRN02C         │
│ (Daily PS)   │                      │                            │
├──────────────┼──────────────────────┼────────────────────────────┤
│ DALYREJS     │ CBTRN02C (write)     │ *(human review)*           │
│ (Rejects GDG)│                      │                            │
├──────────────┼──────────────────────┼────────────────────────────┤
│ TRANSACT.BKUP│ TRANBKP.jcl (SORT)  │ COMBTRAN.jcl, TRANREPT.jcl │
│ (Backup GDG) │                      │                            │
├──────────────┼──────────────────────┼────────────────────────────┤
│ STATEMNT.PS  │ CBSTM03A (write)     │ TXT2PDF1.JCL               │
│ (Statement)  │                      │                            │
└──────────────┴──────────────────────┴────────────────────────────┘
```

### 3.2 JCL Job — Dataset Mapping

| JCL Job | Action | Datasets |
|---------|--------|----------|
| **ACCTFILE.jcl** | DEFINE CLUSTER | ACCTDATA.VSAM.KSDS |
| **CARDFILE.jcl** | DEFINE CLUSTER+AIX | CARDDATA.VSAM.KSDS, CARDDATA.AIX |
| **CUSTFILE.jcl** | DEFINE CLUSTER | CUSTDATA.VSAM.KSDS |
| **XREFFILE.jcl** | DEFINE CLUSTER+AIX | CARDXREF.VSAM.KSDS, CARDXREF.AIX |
| **TRANFILE.jcl** | DEFINE CLUSTER+AIX | TRANSACT.VSAM.KSDS |
| **TRANIDX.jcl** | DEFINE AIX+PATH | TRANSACT.PROC.TS.AIX (on TRAN-PROC-TS) |
| **TCATBALF.jcl** | DEFINE CLUSTER | TCATBALF.VSAM.KSDS |
| **DISCGRP.jcl** | DEFINE CLUSTER | DISCGRP.VSAM.KSDS |
| **TRANTYPE.jcl** | DEFINE CLUSTER | TRANTYPE.VSAM.KSDS |
| **TRANCATG.jcl** | DEFINE CLUSTER | TRANCATG.VSAM.KSDS |
| **DEFGDGB.jcl** | DEFINE GDG | TRANSACT.BKUP (5 gen), TRANSACT.DALY (5 gen), TRANREPT (5 gen) |
| **DEFGDGD.jcl** | DEFINE GDG | TRANTYPE.BKUP (5 gen) |
| **DALYREJS.jcl** | DEFINE GDG | DALYREJS (5 gen) |
| **REPTFILE.jcl** | DEFINE GDG | TRANREPT (10 gen) |
| **POSTTRAN.jcl** | EXEC CBTRN02C | DALYTRAN (read), TRANSACT/ACCOUNT/XREF (read/write), DALYREJS/TCATBAL (write) |
| **INTCALC.jcl** | EXEC CBACT04C | TCATBAL/XREF/DISCGRP (read), ACCOUNT (read/rewrite), TRANSACT (write) |
| **CREASTMT.JCL** | EXEC CBSTM03A | XREF/CUST/ACCT/TRNX (read), STMT-FILE/HTML-FILE (write) |
| **TRANREPT.jcl** | SORT+EXEC CBTRN03C | TRANSACT (read/sort), TRANTYPE/TRANCATG/XREF (read), REPTFILE (write) |
| **READACCT.jcl** | EXEC CBACT01C | ACCTDATA (read), sequential output (write) |
| **READCARD.jcl** | EXEC CBACT02C | CARDDATA (read) |
| **READCUST.jcl** | EXEC CBCUS01C | CUSTDATA (read) |
| **READXREF.jcl** | EXEC CBACT03C | CARDXREF (read) |
| **CBEXPORT.jcl** | EXEC CBEXPORT | ALL entity files (read), EXPORT-FILE (write) |
| **CBIMPORT.jcl** | EXEC CBIMPORT | EXPORT-FILE (read), ALL entity files (write), ERROR-FILE (write) |
| **TRANBKP.jcl** | REPROC SORT | TRANSACT (read/VSAM→PS), TRANSACT.BKUP (GDG write) |
| **COMBTRAN.jcl** | SORT MERGE | TRANSACT.BKUP + SYSTRAN (read), COMBINED (write) |
| **PRTCATBL.jcl** | REPROC | TCATBALF (read/VSAM→PS report) |
| **DUSRSECJ.jcl** | IEBGENER | In-stream data → USRSEC (PS) |
| **CLOSEFIL.jcl** | CEMT SET CLO | TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| **OPENFIL.jcl** | CEMT SET OPE | TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| **CBADMCDJ.jcl** | DFHCSDUP | CICS CSD resource definitions |
| **WAITSTEP.jcl** | EXEC COBSWAIT | (no dataset — timer only) |
| **FTPJCL.JCL** | FTP | File transfer to/from mainframe |
| **INTRDRJ1.JCL** | IEBGENER | Submit INTRDRJ2 via internal reader |
| **INTRDRJ2.JCL** | IDCAMS REPRO | Copy backup data to INTRDR |
| **TXT2PDF1.JCL** | TXT2PDF REXX | STATEMNT.PS (read) → STATEMNT.PS.PDF (write) |
| **ESDSRRDS.jcl** | IEBGENER | Load ESDS/RRDS test datasets |

#### Sub-Application JCL Jobs

| JCL Job | Action | Datasets |
|---------|--------|----------|
| **LOADPADB.JCL** | EXEC PAUDBLOD | Flat files → IMS PAUT database |
| **UNLDPADB.JCL** | EXEC PAUDBUNL | IMS PAUT → flat files |
| **UNLDGSAM.JCL** | EXEC DBUNLDGS | IMS → GSAM flat file |
| **CBPAUP0J.jcl** | EXEC CBPAUP0C | Purge expired IMS auth records |
| **DBPAUTP0.jcl** | IMS UNLOAD | IMS PAUT → sequential unload dataset |
| **CREADB21.jcl** | DB2 DDL | Create TR_TYPE and TR_CAT tables |
| **MNTTRDB2.jcl** | EXEC COBTUPDT | INPFILE (read) → DB2 TR_TYPE (update/insert) |
| **TRANEXTR.jcl** | DB2 UNLOAD | TR_TYPE table → sequential file |

---

## 4. End-to-End Batch Pipeline Flow

### 4.1 Daily Batch Cycle

```
┌─────────────────────────────────────────────────────────────────────┐
│                     DAILY BATCH PIPELINE                            │
│                                                                     │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐     │
│  │ POSTTRAN │───►│ INTCALC  │───►│ CREASTMT │───►│ TRANREPT │     │
│  │(CBTRN02C)│    │(CBACT04C)│    │(CBSTM03A)│    │(CBTRN03C)│     │
│  └─────┬────┘    └─────┬────┘    └─────┬────┘    └─────┬────┘     │
│        │               │               │               │           │
│   Input:          Input:          Input:          Input:           │
│   DALYTRAN        TCATBALF        XREF            TRANSACT         │
│   TRANSACT        XREF            CUSTDATA        CARDXREF         │
│   CARDXREF        DISCGRP         ACCTDATA        TRANTYPE         │
│   ACCTDATA        ACCTDATA        TRANSACT        TRANCATG         │
│        │               │               │               │           │
│   Output:         Output:         Output:         Output:          │
│   TRANSACT(upd)   ACCTDATA(rwr)   STMT-FILE       REPTFILE         │
│   ACCTDATA(rwr)   TRANSACT(wrt)   HTML-FILE       (GDG)            │
│   DALYREJS                             │                           │
│   TCATBALF                        ┌────▼────┐                      │
│                                   │TXT2PDF1 │                      │
│                                   │(REXX)   │                      │
│                                   └─────────┘                      │
│                                   Output:                          │
│                                   STATEMNT.PDF                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Step 1: POSTTRAN (JCL: POSTTRAN.jcl → CBTRN02C)**
- Reads daily transaction flat file (DALYTRAN)
- Validates transactions against CARDXREF and ACCTDATA
- Writes valid records to TRANSACT master
- Updates account balances (REWRITE ACCTDATA)
- Accumulates category balances (WRITE TCATBALF)
- Rejects invalid records to DALYREJS (GDG)

**Step 2: INTCALC (JCL: INTCALC.jcl → CBACT04C)**
- Reads category balances (TCATBALF) for each account
- Looks up interest rates by group (DISCGRP via ACCT-GROUP-ID)
- Calculates interest and fees
- Updates account balances (REWRITE ACCTDATA)
- Writes interest transactions to TRANSACT

**Step 3: CREASTMT (JCL: CREASTMT.JCL → CBSTM03A → CBSTM03B)**
- Reads cross-reference (CARDXREF) to link cards to customers
- Reads customer (CUSTDATA) and account (ACCTDATA) details
- Reads transactions (TRANSACT via COSTM01 layout)
- Generates plain-text statement file (STMT-FILE)
- Generates HTML statement file (HTML-FILE)
- Optional: TXT2PDF1.JCL converts text to PDF

**Step 4: TRANREPT (JCL: TRANREPT.jcl → CBTRN03C)**
- Pre-step: SORT TRANSACT by account+type+category
- Reads sorted transactions
- Enriches with type descriptions (TRANTYPE) and category descriptions (TRANCATG)
- Joins with CARDXREF for account linkage
- Generates formatted report with page/account/grand totals (REPTFILE GDG)

### 4.2 Data Setup Pipeline (One-Time / Periodic)

```
                    ┌───────────────────────────┐
                    │     VSAM SETUP (once)     │
                    └───────────┬───────────────┘
                                │
    ┌──────────┬────────┬───────┼───────┬──────────┬──────────┐
    ▼          ▼        ▼       ▼       ▼          ▼          ▼
ACCTFILE  CARDFILE  CUSTFILE  XREFFILE  TRANFILE  TCATBALF  DISCGRP
(KSDS)    (KSDS     (KSDS)   (KSDS     (KSDS     (KSDS)    (KSDS)
           +AIX)              +AIX)     +AIX)
                                │
                    ┌───────────▼───────────────┐
                    │  GDG SETUP  (DEFGDGB)     │
                    │  TRANSACT.BKUP (5 gen)    │
                    │  TRANSACT.DALY (5 gen)    │
                    │  TRANREPT (5 gen)         │
                    └───────────────────────────┘
```

### 4.3 Data Migration Pipeline (Export/Import)

```
Source System                    Target System
┌──────────┐                    ┌──────────┐
│ CBEXPORT │                    │ CBIMPORT │
│ (Batch)  │                    │ (Batch)  │
├──────────┤                    ├──────────┤
│ READ:    │     EXPORT-FILE    │ READ:    │
│ CUSTDATA ├──────────────────►│ EXPORT   │
│ ACCTDATA │   (RECLN 500,     │          │
│ CARDXREF │    multi-record)  │ WRITE:   │
│ TRANSACT │                    │ CUSTDATA │
│ CARDDATA │                    │ ACCTDATA │
└──────────┘                    │ CARDXREF │
                                │ TRANSACT │
                                │ CARDDATA │
                                │ ERROR    │
                                └──────────┘
```

### 4.4 IMS Data Pipeline

```
Flat Files ──► LOADPADB.JCL (PAUDBLOD) ──► IMS PAUT Database
                                                │
                        ┌───────────────────────┼───────────────────┐
                        ▼                       ▼                   ▼
               COPAUS0C (Browse)      COPAUA0C (Auth)      CBPAUP0C (Purge)
               COPAUS1C (Detail)      MQ Request/Reply     Expired records
               COPAUS2C (Fraud)
                        │                                           │
                        ▼                                           ▼
               DB2 fraud_log table                    UNLDPADB.JCL (PAUDBUNL) ──► Flat Files
                                                      UNLDGSAM.JCL (DBUNLDGS) ──► GSAM File
```

---

## 5. Copybook Dependency Matrix

### 5.1 Most-Referenced Copybooks (by program count)

| Rank | Copybook | Programs Using | Business Role |
|------|---------|---------------|--------------|
| 1 | COCOM01Y | 21 | CICS COMMAREA — inter-program data |
| 2 | DFHAID | 19 | CICS AID key definitions |
| 3 | DFHBMSCA | 19 | BMS attribute constants |
| 4 | COTTL01Y | 19 | Screen title constants |
| 5 | CSDAT01Y | 19 | Date/time working storage |
| 6 | CSMSG01Y | 19 | Common message text |
| 7 | CSUSR01Y | 17 | User security record layout |
| 8 | CVACT01Y | 14 | Account master record |
| 9 | CVACT03Y | 14 | Card cross-reference record |
| 10 | CVTRA05Y | 9 | Transaction master record |
| 11 | CVACT02Y | 8 | Card master record |
| 12 | CVCUS01Y | 10 | Customer master record |
| 13 | CVCRD01Y | 8 | Card working areas |
| 14 | CSSETATY | 3 | Screen attribute macro (×25 in COACTUPC) |
| 15 | CSSTRPFY | 6 | PFKey mapping procedure |

### 5.2 Program → Copybook Matrix (Top 10 by Count)

| Program | Copybook Count | Category |
|---------|---------------|----------|
| COACTUPC | 58 (incl. 25× CSSETATY REPLACING) | CICS |
| COPAUA0C | 17 | CICS/IMS/MQ |
| COACTVWC | 16 | CICS |
| COCRDSLC | 16 | CICS |
| COCRDUPC | 16 | CICS |
| COPAUS0C | 15 | CICS/IMS |
| COTRTUPC | 15 | CICS/DB2 |
| COCRDLIC | 14 | CICS |
| COTRTLIC | 12 | CICS/DB2 |
| COBIL00C | 11 | CICS |

---

## 6. External Dependency Graph

```
                    ┌─────────────────────────────────┐
                    │        EXTERNAL SYSTEMS          │
                    ├─────────────────────────────────┤
                    │                                  │
    Assembler       │  COBDATFT ◄── CBACT01C          │
    Programs        │  MVSWAIT  ◄── COBSWAIT          │
                    │                                  │
    LE Runtime      │  CEE3ABD  ◄── 8 batch programs   │
    Services        │  CEEDAYS  ◄── CSUTLDTC           │
                    │                                  │
    MQ API          │  MQOPEN/GET/PUT/CLOSE            │
                    │     ◄── COPAUA0C, COACCT01,      │
                    │         CODATE01                  │
                    │                                  │
    IMS DL/I        │  CBLTDLI                          │
                    │     ◄── PAUDBLOD, PAUDBUNL,      │
                    │         DBUNLDGS                  │
                    │                                  │
    DB2             │  EXEC SQL                         │
                    │     ◄── COTRTLIC, COTRTUPC,      │
                    │         COBTUPDT, COPAUS2C        │
                    │                                  │
    CICS Services   │  DFHCSDUP ◄── CBADMCDJ (JCL)    │
                    │  CEMT     ◄── CLOSEFIL/OPENFIL   │
                    │                                  │
    Utilities       │  IEBGENER ◄── DUSRSECJ, ESDSRRDS │
                    │  IKJEFT01 ◄── MNTTRDB2, TXT2PDF1 │
                    │  IEFBR14  ◄── (scratch/delete)   │
                    │  SORT/MERGE ◄── TRANREPT, COMBTRAN│
                    │  FTP      ◄── FTPJCL              │
                    └─────────────────────────────────┘
```

---

## 7. Suggested Microservice Boundaries

Based on the dependency analysis, the following service boundaries minimize cross-service data access:

| Microservice | Programs | Shared Datasets | Cross-Dependencies |
|-------------|----------|----------------|-------------------|
| **Account Service** | COACTUPC, COACTVWC, COACCT01, CBACT01C | ACCTDATA, CUSTDATA, CARDXREF | Card Service (XREF), Auth Service (account lookup) |
| **Card Service** | COCRDLIC, COCRDSLC, COCRDUPC | CARDDATA, CARDXREF | Account Service (ACCT-ID) |
| **Transaction Service** | COTRN00C, COTRN01C, COTRN02C, CBTRN01C, CBTRN02C | TRANSACT, DALYTRAN, TCATBALF | Account Service (balance update) |
| **Authorization Service** | COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C | IMS PAUT DB, MQ Queues | Account Service (limit check), Card Service (card lookup) |
| **User Service** | COSGN00C, COUSR00C–03C | USRSEC | None (self-contained) |
| **Report Service** | CORPT00C, CBTRN03C, CBSTM03A/B | TRANSACT, XREF, TRANTYPE | Transaction Service (data source) |
| **Data Migration** | CBEXPORT, CBIMPORT | All entity files | All services (full data set) |
| **Interest/Billing** | CBACT04C, COBIL00C | ACCTDATA, TCATBALF, DISCGRP | Account Service, Transaction Service |
