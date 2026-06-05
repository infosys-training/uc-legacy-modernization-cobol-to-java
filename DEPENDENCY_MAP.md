# DEPENDENCY MAP — CardDemo Call Graph & Dataset Lineage

## 1. Online Program Call Graph (CICS XCTL/LINK Chains)

```
COSGN00C (Sign-On — Entry Point)
│
├── [User Type = 'A'] ──► COADM01C (Admin Menu)
│                           │
│                           ├── Option 1 ──► COUSR00C (User List)
│                           │                  ├── Select ──► COUSR02C (User Update)
│                           │                  └── Select ──► COUSR03C (User Delete)
│                           │
│                           ├── Option 2 ──► COUSR01C (User Add)
│                           │
│                           ├── Option 5 ──► COTRTLIC (Tran Type List — DB2)
│                           │
│                           └── Option 6 ──► COTRTUPC (Tran Type Update — DB2)
│
└── [User Type = 'U'] ──► COMEN01C (Main Menu — Central Hub)
                            │
                            ├── Option 1 ──► COACTVWC (Account View)
                            │
                            ├── Option 2 ──► COACTUPC (Account Update)
                            │
                            ├── Option 3 ──► COCRDLIC (Credit Card List)
                            │                  ├── View ──► COCRDSLC (Card View)
                            │                  └── Update ──► COCRDUPC (Card Update)
                            │
                            ├── Option 6 ──► COTRN00C (Transaction List)
                            │                  └── Select ──► COTRN01C (Tran View)
                            │
                            ├── Option 8 ──► COTRN02C (Transaction Add)
                            │
                            ├── Option 9 ──► CORPT00C (Report Request)
                            │                  └── Submits ──► INTRDRJ1 → INTRDRJ2 (batch)
                            │
                            ├── Option 10 ──► COBIL00C (Bill Payment)
                            │
                            └── Option 11 ──► COPAUS0C (Auth Summary — IMS)
                                               └── CICS LINK ──► COPAUS1C (Auth Detail)
                                                                   └── CICS LINK ──► COPAUS2C (Fraud Mark — DB2)
```

### Navigation Mechanisms
- **XCTL** (transfer control, no return): Used between menu programs and functional screens
- **LINK** (call with return): Used in COPAUS chain for sub-screen drilldown
- **RETURN TRANSID**: Used by all programs to return to CICS; re-entry handled via COMMAREA context

---

## 2. Batch Program Call Graph

```
CBACT01C ──CALL──► COBDATFT (assembler: date formatting)
         ──CALL──► CEE3ABD (LE: abnormal termination)

CBACT02C ──CALL──► CEE3ABD

CBACT03C ──CALL──► CEE3ABD

CBACT04C ──CALL──► CEE3ABD

CBCUS01C ──CALL──► CEE3ABD

CBTRN01C ──CALL──► CEE3ABD

CBTRN02C ──CALL──► CEE3ABD

CBTRN03C ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B (COBOL: I/O submodule — open/read/close files)

COBSWAIT ──CALL──► MVSWAIT (assembler: wait/delay routine)

CSUTLDTC ──CALL──► CEEDAYS (LE: date conversion utility)

CORPT00C ──CALL──► CSUTLDTC (date utility, called from online program)
COTRN02C ──CALL──► CSUTLDTC (date utility, called from online program)
```

### External Dependencies (Non-COBOL)
| Called Module | Type | Called By | Purpose |
|--------------|------|-----------|---------|
| COBDATFT | IBM Assembler | CBACT01C | Date formatting (YYYYMMDD ↔ display format) |
| MVSWAIT | IBM Assembler | COBSWAIT | Introduce timed wait (batch scheduling) |
| CEE3ABD | LE Runtime | 7 batch programs | Abnormal termination with dump |
| CEEDAYS | LE Runtime | CSUTLDTC | Convert date to Lilian day number |
| MQOPEN/MQGET/MQPUT1 | MQ API | COPAUA0C, COACCT01, CODATE01 | Message queue operations |
| CBLTDLI | IMS DL/I | PAUDBLOD, PAUDBUNL, DBUNLDGS | IMS database calls |
| DFSRRC00 | IMS Region Controller | JCL (batch IMS) | IMS batch region initialization |

---

## 3. Dataset Lineage — Which JCL Jobs Read/Write Which Files

### Core VSAM Files

#### ACCTDATA (Account Master)
```
Writers:                          Readers:
  CBACT04C (INTCALC.jcl)           CBACT01C (READACCT.jcl)
  CBTRN02C (POSTTRAN.jcl)          CBTRN01C (via DD)
  COBIL00C (online)                CBTRN02C (POSTTRAN.jcl)
  COACTUPC (online)                CBSTM03A (CREASTMT.JCL)
  CBIMPORT (CBIMPORT.jcl)          CBEXPORT (CBEXPORT.jcl)
                                   CBACT04C (INTCALC.jcl)
                                   COPAUA0C (online)
                                   COACTVWC, COBIL00C, COTRN02C (online)
```

#### CARDDATA (Card Master)
```
Writers:                          Readers:
  COCRDUPC (online)                CBACT02C (READCARD.jcl)
  CBIMPORT (CBIMPORT.jcl)          CBEXPORT (CBEXPORT.jcl)
                                   CBTRN01C (via DD)
                                   COCRDLIC, COCRDSLC, COCRDUPC (online)
                                   COACTUPC, COACTVWC (online via AIX)
```

#### CUSTDATA (Customer Master)
```
Writers:                          Readers:
  COACTUPC (online — REWRITE)      CBCUS01C (READCUST.jcl)
  CBIMPORT (CBIMPORT.jcl)          CBEXPORT (CBEXPORT.jcl)
                                   CBSTM03A (CREASTMT.JCL)
                                   CBTRN01C (via DD)
                                   COACTVWC, COPAUS0C (online)
```

#### CARDXREF (Card-Account Cross-Reference)
```
Writers:                          Readers:
  CBIMPORT (CBIMPORT.jcl)          CBACT03C (READXREF.jcl)
                                   CBACT04C (INTCALC.jcl)
                                   CBTRN01C, CBTRN02C (batch)
                                   CBTRN03C (TRANREPT.jcl)
                                   CBSTM03A (CREASTMT.JCL)
                                   CBEXPORT (CBEXPORT.jcl)
                                   COACTUPC, COACTVWC, COCRDLIC (online)
                                   COPAUS0C (online)
```

#### TRANSACT (Posted Transaction Master)
```
Writers:                          Readers:
  CBTRN02C (POSTTRAN.jcl)          CBTRN03C (TRANREPT.jcl)
  CBACT04C (INTCALC.jcl)           CBSTM03A (CREASTMT.JCL via SORT)
  COBIL00C (online)                CBEXPORT (CBEXPORT.jcl)
  COTRN02C (online)                COMBTRAN.jcl (SORT merge)
  CBIMPORT (CBIMPORT.jcl)          COTRN00C, COTRN01C (online browse)
```

#### DALYTRAN (Daily Transaction Input)
```
Writers:                          Readers:
  (External/online capture)         CBTRN01C (validation)
                                    CBTRN02C (POSTTRAN.jcl — main input)
```

#### TCATBALF (Transaction Category Balance)
```
Writers:                          Readers:
  CBTRN02C (POSTTRAN.jcl)          CBACT04C (INTCALC.jcl)
                                   PRTCATBL.jcl (report)
```

#### USRSEC (User Security)
```
Writers:                          Readers:
  COUSR01C (online — WRITE)        COSGN00C (online — sign-on READ)
  COUSR02C (online — REWRITE)      COUSR00C (online — browse)
  COUSR03C (online — DELETE)       COUSR02C (online — READ for update)
  DUSRSECJ.jcl (initial load)     COUSR03C (online — READ for delete)
```

### Reference Data Files

| File | Writers | Readers |
|------|---------|---------|
| TRANTYPE | TRANTYPE.jcl (IDCAMS load) | CBTRN03C (TRANREPT.jcl), COTRTLIC (online) |
| TRANCATG | TRANCATG.jcl (IDCAMS load) | CBTRN03C (TRANREPT.jcl) |
| DISCGRP | DISCGRP.jcl (IDCAMS load) | CBACT04C (INTCALC.jcl) |
| DATEPARM | (manually configured) | CBTRN03C (TRANREPT.jcl) |
| EXPORT.DATA | CBEXPORT (CBEXPORT.jcl) | CBIMPORT (CBIMPORT.jcl) |

### GDG (Generation Data Group) Files

| GDG Base | Producer Job | Consumer Job |
|----------|-------------|--------------|
| TRANSACT.BKUP | TRANBKP.jcl | COMBTRAN.jcl |
| SYSTRAN | INTCALC.jcl | COMBTRAN.jcl |
| TRANSACT.COMBINED | COMBTRAN.jcl | TRANFILE.jcl (reload) |
| TRANSACT.DALY | TRANREPT.jcl (SORT) | TRANREPT.jcl (CBTRN03C) |
| DALYREJS | POSTTRAN.jcl | (audit/review) |
| TRANREPT | TRANREPT.jcl | (print/archive) |
| TCATBALF.BKUP | PRTCATBL.jcl | (archive) |
| TRANTYPE.BKUP | DEFGDGD.jcl | (rollback) |
| TRANCATG.PS.BKUP | DEFGDGD.jcl | (rollback) |
| DISCGRP.BKUP | DEFGDGD.jcl | (rollback) |

---

## 4. End-to-End Batch Pipeline Flow

### Daily Transaction Processing Pipeline
```
┌─────────────────────────────────────────────────────────────────────────┐
│  CLOSEFIL.jcl — Close CICS files (release VSAM for batch exclusive)     │
└─────────────────────────┬───────────────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  TRANBKP.jcl — Backup TRANSACT VSAM to GDG                              │
│    Step: REPROC (REPRO VSAM→SEQ) → IDCAMS (verify/delete old)           │
└─────────────────────────┬───────────────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  POSTTRAN.jcl — Post daily transactions (CBTRN02C)                       │
│    Input:  DALYTRAN.PS (daily file), CARDXREF (validation)               │
│    I-O:    ACCTDATA (update balances), TCATBALF (update cat balance)     │
│    Output: TRANSACT (posted), DALYREJS(+1) (rejects)                    │
└─────────────────────────┬───────────────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  INTCALC.jcl — Calculate interest (CBACT04C)                             │
│    Input:  TCATBALF, CARDXREF, DISCGRP (interest rates)                  │
│    I-O:    ACCTDATA (apply interest to balances)                         │
│    Output: SYSTRAN(+1) (generated interest transactions)                 │
└─────────────────────────┬───────────────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  COMBTRAN.jcl — Merge transaction backup + system transactions           │
│    Input:  TRANSACT.BKUP(0), SYSTRAN(0)                                  │
│    Output: TRANSACT.COMBINED(+1) → reloaded into TRANSACT VSAM          │
└─────────────────────────┬───────────────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  CREASTMT.JCL — Generate customer statements (CBSTM03A→CBSTM03B)        │
│    Step 1: SORT TRANSACT by card+tran-id → TRXFL.SEQ                     │
│    Step 2: IDCAMS REPRO → TRXFL.VSAM.KSDS                               │
│    Step 3: IEFBR14 delete old statement files                            │
│    Step 4: CBSTM03A reads TRNX, XREF, CUST, ACCT → writes STMTFILE+HTML│
└─────────────────────────┬───────────────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  TRANREPT.jcl — Generate daily transaction report (CBTRN03C)             │
│    Step 1: REPROC backup TRANSACT → BKUP(+1)                            │
│    Step 2: SORT by account+type → TRANSACT.DALY(+1)                      │
│    Step 3: CBTRN03C reads sorted tran + CARDXREF + TRANTYPE + TRANCATG   │
│            → writes TRANREPT(+1) formatted report                        │
└─────────────────────────┬───────────────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  OPENFIL.jcl — Reopen CICS files (restore online access)                │
└─────────────────────────────────────────────────────────────────────────┘
```

### Weekly Export Pipeline
```
CBEXPORT.jcl — Export all entity data for branch migration
    Input:  CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA (all VSAM KSDS)
    Output: EXPORT.DATA (500-byte multi-record sequential file)
```

### Data Import Pipeline
```
CBIMPORT.jcl — Import from export file (branch migration inbound)
    Input:  EXPORT.DATA
    Output: CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT,
            TRANSACT.IMPORT, CARDOUT (all sequential)
    Errors: IMPORT.ERRORS
```

---

## 5. Program-to-File Dependency Matrix

| Program | ACCTFILE | CARDFILE | CUSTFILE | CARDXREF | TRANSACT | DALYTRAN | TCATBALF | USRSEC | DISCGRP |
|---------|:--------:|:--------:|:--------:|:--------:|:--------:|:--------:|:--------:|:------:|:-------:|
| CBACT01C | R | | | | | | | | |
| CBACT02C | | R | | | | | | | |
| CBACT03C | | | | R | | | | | |
| CBACT04C | RW | | | R | W | | R | | R |
| CBCUS01C | | | R | | | | | | |
| CBTRN01C | R | R | R | R | R | R | | | |
| CBTRN02C | RW | | | R | W | R | RW | | |
| CBTRN03C | | | | R | R | | | | |
| CBSTM03A/B | R | | R | R | R* | | | | |
| CBEXPORT | R | R | R | R | R | | | | |
| CBIMPORT | W | W | W | W | W | | | | |
| COSGN00C | | | | | | | | R | |
| COACTUPC | RW | | RW | R | | | | | |
| COACTVWC | R | R | R | R | | | | | |
| COCRDLIC | | R | | | | | | | |
| COCRDSLC | | R | | R | | | | | |
| COCRDUPC | | RW | | | | | | | |
| COTRN00C | | | | | R | | | | |
| COTRN01C | | | | | R | | | | |
| COTRN02C | R | | | R | RW | | | | |
| COBIL00C | RW | | | | W | | | | |
| COUSR00C | | | | | | | | R | |
| COUSR01C | | | | | | | | W | |
| COUSR02C | | | | | | | | RW | |
| COUSR03C | | | | | | | | RD | |

Legend: R=Read, W=Write, RW=Read+Write(Rewrite), RD=Read+Delete, R*=Via sorted intermediate

---

## 6. Inter-Program Dependency Counts

| Program | Depends On (calls/reads from) | Depended On By |
|---------|-------------------------------|----------------|
| COMEN01C | COSGN00C | 11 programs (XCTL targets) |
| COADM01C | COSGN00C | 6 programs (XCTL targets) |
| COSGN00C | _(entry point)_ | COMEN01C, COADM01C |
| CBSTM03B | _(standalone module)_ | CBSTM03A |
| CSUTLDTC | _(standalone utility)_ | CORPT00C, COTRN02C |
| COBDATFT | _(assembler)_ | CBACT01C |
| MVSWAIT | _(assembler)_ | COBSWAIT |
| CARDXREF | _(VSAM file)_ | 11 programs read it |
| ACCTFILE | _(VSAM file)_ | 13 programs read/write it |
