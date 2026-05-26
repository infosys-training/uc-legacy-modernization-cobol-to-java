# DEPENDENCY MAP — CardDemo COBOL Estate

## 1. Program Call Graph

### 1.1 Online (CICS) Program Navigation Flow

All online programs communicate via COMMAREA (`COCOM01Y`) and transfer control using `EXEC CICS XCTL`.

```
                        ┌─────────────┐
                        │  COSGN00C   │  Sign-on / Authentication
                        │  (CC00)     │
                        └──────┬──────┘
                    ┌──────────┴──────────┐
                    ▼                     ▼
             ┌─────────────┐       ┌─────────────┐
             │  COMEN01C   │       │  COADM01C   │
             │  Main Menu  │       │  Admin Menu │
             │  (Users)    │       │  (Admins)   │
             └──────┬──────┘       └──────┬──────┘
                    │                     │
       ┌────────────┼────────────┐        ├──── COUSR00C (User List)
       │            │            │        ├──── COUSR01C (User Add)
       │            │            │        ├──── COUSR02C (User Update)
       │            │            │        ├──── COUSR03C (User Delete)
       │            │            │        ├──── COTRTLIC (Tran Type List, DB2)
       │            │            │        └──── COTRTUPC (Tran Type Update, DB2)
       │            │            │
       ▼            ▼            ▼
  ┌──────────┐ ┌──────────┐ ┌──────────┐
  │ Account  │ │  Card    │ │ Transact │
  │ Domain   │ │ Domain   │ │ Domain   │
  └────┬─────┘ └────┬─────┘ └────┬─────┘
       │             │            │
  COACTVWC ◄──┐  COCRDLIC     COTRN00C ──▶ COTRN01C (View)
  COACTUPC    │  COCRDSLC     COTRN02C (Add)
              │  COCRDUPC     CORPT00C (Reports)
              │               COBIL00C (Bill Pay)
              │               COPAUS0C (Auth Summary) ──▶ COPAUS1C
              │                                          ▶ COPAUS2C
              │
  COCRDLIC ───┤──▶ COCRDSLC (Card Detail)
              └──▶ COCRDUPC (Card Update)
```

### 1.2 Program-to-Program CALL/XCTL Dependencies

| Source Program | Target Program | Mechanism | Purpose |
|---------------|---------------|-----------|---------|
| COSGN00C | COMEN01C | XCTL | Route regular user to main menu |
| COSGN00C | COADM01C | XCTL | Route admin user to admin menu |
| COMEN01C | COSGN00C | XCTL | Return to sign-on (logout) |
| COMEN01C | _(dynamic)_ | XCTL | Route to selected menu option program |
| COADM01C | COSGN00C | XCTL | Return to sign-on (logout) |
| COADM01C | _(dynamic)_ | XCTL | Route to selected admin option program |
| COACTVWC | COMEN01C | XCTL | Return to main menu |
| COACTVWC | _(from-program)_ | XCTL | Return to calling program |
| COACTUPC | COMEN01C | XCTL | Return to main menu |
| COCRDLIC | COMEN01C | XCTL | Return to main menu |
| COCRDLIC | COCRDSLC | XCTL | View card details |
| COCRDLIC | COCRDUPC | XCTL | Update card |
| COCRDSLC | COMEN01C | XCTL | Return to main menu |
| COCRDUPC | COMEN01C | XCTL | Return to main menu |
| COTRN00C | COMEN01C | XCTL | Return to main menu |
| COTRN00C | COTRN01C | XCTL | View transaction detail |
| COTRN02C | COMEN01C | XCTL | Return to main menu |
| CORPT00C | COMEN01C | XCTL | Return to main menu |
| COBIL00C | COMEN01C | XCTL | Return to main menu |
| COUSR00C | COADM01C | XCTL | Return to admin menu |
| COUSR00C | COUSR02C | XCTL | Edit selected user |
| COUSR00C | COUSR03C | XCTL | Delete selected user |
| COUSR01C | COADM01C | XCTL | Return to admin menu |
| COUSR02C | COADM01C | XCTL | Return to admin menu |
| CBACT01C | COBDATFT | CALL | Date formatting assembler routine |
| CBSTM03A | CBSTM03B | CALL | File I/O subroutine (12 calls) |
| CORPT00C | CSUTLDTC | CALL | Date validation |
| COTRN02C | CSUTLDTC | CALL | Date validation |
| COBSWAIT | MVSWAIT | CALL | Wait (assembler) |
| CBACT01C | CEE3ABD | CALL | LE abend handler |
| CBACT02C | CEE3ABD | CALL | LE abend handler |
| CBACT03C | CEE3ABD | CALL | LE abend handler |
| CBACT04C | CEE3ABD | CALL | LE abend handler |
| CBCUS01C | CEE3ABD | CALL | LE abend handler |
| CBTRN01C | CEE3ABD | CALL | LE abend handler |
| CBTRN02C | CEE3ABD | CALL | LE abend handler |
| CBTRN03C | CEE3ABD | CALL | LE abend handler |
| CBEXPORT | CEE3ABD | CALL | LE abend handler |
| CBIMPORT | CEE3ABD | CALL | LE abend handler |
| CBSTM03A | CEE3ABD | CALL | LE abend handler |
| CSUTLDTC | CEEDAYS | CALL | LE date service |
| COPAUA0C | CBLTDLI | CALL | IMS DL/I database access |
| COPAUS0C | CBLTDLI | CALL | IMS DL/I database access |
| COPAUS1C | CBLTDLI | CALL | IMS DL/I database access |
| CBPAUP0C | CBLTDLI | CALL | IMS DL/I database access |
| PAUDBLOD | CBLTDLI | CALL | IMS DL/I database access |
| PAUDBUNL | CBLTDLI | CALL | IMS DL/I database access |

### 1.3 Copybook Dependency Matrix (Top 15)

| Copybook | # Programs | Programs Using It |
|----------|-----------|-------------------|
| CSSETATY | 40 | All online programs (via COPY REPLACING for each field) |
| DFHBMSCA | 22 | All CICS programs (BMS character attributes) |
| DFHAID | 21 | All CICS programs (AID key definitions) |
| CSMSG01Y | 21 | All CICS programs (standard messages) |
| CSDAT01Y | 21 | All CICS programs (date/time) |
| COTTL01Y | 21 | All CICS programs (screen titles) |
| COCOM01Y | 21 | All CICS programs (COMMAREA) |
| CVACT03Y | 16 | Account/card/transaction programs |
| CVACT01Y | 16 | Account/card/transaction programs |
| CSUSR01Y | 14 | All user-facing online programs |
| CVTRA05Y | 11 | Transaction programs |
| CVCUS01Y | 10 | Customer-facing programs |
| CVACT02Y | 10 | Card programs |
| CSSTRPFY | 9 | Online programs (PFKey handler) |
| CSMSG02Y | 8 | Programs with abend handling |

---

## 2. Dataset Lineage Map

### 2.1 VSAM File ↔ Program ↔ JCL Cross-Reference

| Dataset (VSAM) | DD Name | Programs that READ | Programs that WRITE/UPDATE | JCL Jobs |
|----------------|---------|-------------------|--------------------------|----------|
| ACCTDATA.VSAM.KSDS | ACCTFILE / ACCTDAT | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBEXPORT, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COTRN02C | CBACT04C, COACTUPC, COBIL00C, COTRN02C | ACCTFILE.jcl (define+load) |
| CARDDATA.VSAM.KSDS | CARDFILE / CARDDAT | CBACT02C, CBTRN01C, CBEXPORT, COCRDLIC, COCRDSLC, COCRDUPC | COCRDUPC | CARDFILE.jcl (define+load+AIX) |
| CARDXREF.VSAM.KSDS | XREFFILE / CCXREF / CXACAIX | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COBIL00C, COTRN02C | _(read-only cross-ref)_ | XREFFILE.jcl (define+load+AIX) |
| CUSTDATA.VSAM.KSDS | CUSTFILE / CUSTDAT | CBCUS01C, CBTRN01C, CBEXPORT, COCRDSLC, COCRDUPC, CBSTM03B | _(via CICS update in COCRDUPC)_ | CUSTFILE.jcl (define+load), DEFCUST.jcl (with SSN AIX) |
| TRANSACT.VSAM.KSDS | TRANFILE / TRANSACT | CBTRN03C, CBEXPORT, COTRN00C, COTRN01C, CORPT00C | CBTRN01C, CBTRN02C, COBIL00C, COTRN02C | TRANFILE.jcl (define+load), COMBTRAN.jcl (reload) |
| USRSEC | USRSEC | COSGN00C, COADM01C, COMEN01C, COUSR00C, COUSR02C, COUSR03C | COUSR01C, COUSR02C, COUSR03C | DUSRSECJ.jcl (define+load) |
| TRANTYPE.VSAM.KSDS | TRANTYPE | CBTRN03C | _(batch only, read-only)_ | TRANTYPE.jcl (define+load) |
| TRANCATG.VSAM.KSDS | TRANCATG | CBTRN03C | _(batch only, read-only)_ | TRANCATG.jcl (define+load) |
| TCATBALF.VSAM.KSDS | TCATBALF | CBACT04C | CBACT04C, CBTRN02C | TCATBALF.jcl (define+load) |
| DISCGRP.VSAM.KSDS | DISCGRP | CBACT04C | _(read-only)_ | DISCGRP.jcl (define+load) |
| DALYTRAN | DALYTRAN | CBTRN01C, CBTRN02C | _(external input)_ | — (input from external system) |
| DALYREJS | DALYREJS | — | CBTRN02C | DALYREJS.jcl (define) |
| EXPORT.DATA | EXPFILE | CBIMPORT | CBEXPORT | CBEXPORT.jcl (define+run) |
| DB2: TRNTYPE | — | COTRTLIC, COTRTUPC, COBTUPDT | COTRTUPC, COBTUPDT | CREADB21.jcl (DDL), MNTTRDB2.jcl |
| DB2: TRNTYCAT | — | COTRTLIC, COTRTUPC, COBTUPDT | COTRTUPC, COBTUPDT | CREADB21.jcl (DDL) |
| DB2: AUTHFRDS | — | COPAUA0C, COPAUS2C | COPAUS2C | — (authorization sub-app) |
| IMS: PAUTDB | — | COPAUA0C, COPAUS0C, COPAUS1C, CBPAUP0C | PAUDBLOD, CBPAUP0C | DBPAUTP0.jcl (DBD), LOADPADB.JCL |

### 2.2 GDG (Generation Data Group) Lineage

| GDG Base | Defined By | Written By | Read By |
|----------|-----------|-----------|---------|
| CARDDEMO.TRANSACT.BKUP | DEFGDGB.jcl | TRANBKP.jcl (SORT from VSAM) | COMBTRAN.jcl (SORT input) |
| CARDDEMO.SYSTRAN | DEFGDGD.jcl | _(system-generated transactions)_ | COMBTRAN.jcl (SORT input) |
| CARDDEMO.TRANSACT.COMBINED | _(implicit)_ | COMBTRAN.jcl (SORT output) | COMBTRAN.jcl (REPRO to VSAM) |

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Initial Data Load Pipeline

```
                    STEP 1: VSAM File Definition & Load
    ┌──────────────────────────────────────────────────────────────┐
    │  ACCTFILE.jcl ──▶ ACCTDATA.VSAM.KSDS (Account Master)      │
    │  CARDFILE.jcl ──▶ CARDDATA.VSAM.KSDS (Card Master + AIX)   │
    │  CUSTFILE.jcl ──▶ CUSTDATA.VSAM.KSDS (Customer Master)     │
    │  XREFFILE.jcl ──▶ CARDXREF.VSAM.KSDS (Cross-Ref + AIX)    │
    │  TRANFILE.jcl ──▶ TRANSACT.VSAM.KSDS (Transaction Master)  │
    │  DUSRSECJ.jcl ──▶ USRSEC (User Security)                   │
    │  TRANTYPE.jcl ──▶ TRANTYPE.VSAM.KSDS (Tran Types)          │
    │  TRANCATG.jcl ──▶ TRANCATG.VSAM.KSDS (Tran Categories)    │
    │  TCATBALF.jcl ──▶ TCATBALF.VSAM.KSDS (Category Balances)  │
    │  DISCGRP.jcl  ──▶ DISCGRP.VSAM.KSDS (Discount Groups)     │
    └──────────────────────────────────────────────────────────────┘

                    STEP 2: CICS Resource Setup
    ┌──────────────────────────────────────────────────────────────┐
    │  CBADMCDJ.jcl  ──▶ CICS CSD (programs, mapsets, files,     │
    │                     transactions, TDQueues)                   │
    │  OPENFIL.jcl   ──▶ Open all VSAM files in CICS region       │
    └──────────────────────────────────────────────────────────────┘
```

### 3.2 Daily Transaction Processing Pipeline

```
    ┌───────────────────────────────────────────────────────────────────┐
    │ DAILY INPUT: DALYTRAN (external daily transaction feed)           │
    └─────────────────────────────┬─────────────────────────────────────┘
                                  ▼
    ┌───────────────────────────────────────────────────────────────────┐
    │ POSTTRAN.jcl — Daily Transaction Posting                         │
    │                                                                   │
    │  STEP10: PGM=CBTRN01C (Validate & Insert)                        │
    │    Input:  DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE       │
    │    Output: TRANFILE (validated transactions)                      │
    │                                                                   │
    │  STEP20: PGM=CBTRN02C (Post & Update Balances)                   │
    │    Input:  DALYTRAN, XREFFILE, ACCTFILE                          │
    │    Output: TRANFILE (posted), TCATBALF (updated cat balances),   │
    │            DALYREJS (rejected transactions)                       │
    │                                                                   │
    │  STEP30: SORT + REPRO (Backup transactions to GDG)               │
    │    Input:  TRANSACT.VSAM.KSDS                                    │
    │    Output: TRANSACT.BKUP(+1) (new GDG generation)               │
    └───────────────────────────────────────────────────────────────────┘
                                  ▼
    ┌───────────────────────────────────────────────────────────────────┐
    │ COMBTRAN.jcl — Combine & Reload Transactions                     │
    │                                                                   │
    │  STEP05R: SORT                                                    │
    │    Input:  TRANSACT.BKUP(0) + SYSTRAN(0)                         │
    │    Output: TRANSACT.COMBINED(+1)                                 │
    │                                                                   │
    │  STEP10: IDCAMS REPRO                                            │
    │    Input:  TRANSACT.COMBINED(+1)                                 │
    │    Output: TRANSACT.VSAM.KSDS (reload master)                    │
    └───────────────────────────────────────────────────────────────────┘
                                  ▼
    ┌───────────────────────────────────────────────────────────────────┐
    │ INTCALC.jcl — Interest Calculation                               │
    │                                                                   │
    │  STEP10: PGM=CBACT04C                                            │
    │    Input:  TCATBALF, XREFFILE, ACCTFILE, DISCGRP                 │
    │    Output: TRANSACT (interest transactions), ACCTFILE (updated)  │
    └───────────────────────────────────────────────────────────────────┘
```

### 3.3 Reporting Pipeline

```
    ┌───────────────────────────────────────────────────────────────────┐
    │ CREASTMT.JCL — Statement Generation                              │
    │  PGM=CBSTM03A ──CALL──▶ CBSTM03B                                │
    │    Input:  TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE                │
    │    Output: Plain text statement + HTML statement                  │
    └───────────────────────────────────────────────────────────────────┘

    ┌───────────────────────────────────────────────────────────────────┐
    │ TRANREPT.jcl — Transaction Detail Report                         │
    │  PGM=CBTRN03C                                                    │
    │    Input:  TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM      │
    │    Output: TRANREPT (formatted report)                           │
    └───────────────────────────────────────────────────────────────────┘

    ┌───────────────────────────────────────────────────────────────────┐
    │ TXT2PDF1.JCL — PDF Conversion                                    │
    │    Input:  Text report file                                      │
    │    Output: PDF file                                              │
    └───────────────────────────────────────────────────────────────────┘
```

### 3.4 Data Migration Pipeline

```
    ┌───────────────────────────────────────────────────────────────────┐
    │ CBEXPORT.jcl — Export Customer Data                              │
    │  PGM=CBEXPORT                                                    │
    │    Input:  CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE       │
    │    Output: EXPORT.DATA (multi-record VSAM export file)           │
    └───────────────────────────────────────────────────────────────────┘
                                  ▼
    ┌───────────────────────────────────────────────────────────────────┐
    │ CBIMPORT.jcl — Import Customer Data                              │
    │  PGM=CBIMPORT                                                    │
    │    Input:  EXPORT.DATA                                           │
    │    Output: CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT,   │
    │            TRANSACT.IMPORT, IMPORT.ERRORS                        │
    └───────────────────────────────────────────────────────────────────┘
```

### 3.5 CICS-Submitted Batch (Online-to-Batch Bridge)

```
    ┌───────────────────────────────────────────────────────────────────┐
    │ CORPT00C (Online) submits batch JCL via Internal Reader (TDQ)    │
    │    ──▶ INTRDRJ1.JCL ──▶ POSTTRAN.jcl                            │
    │    ──▶ INTRDRJ2.JCL ──▶ COMBTRAN.jcl                            │
    └───────────────────────────────────────────────────────────────────┘
```

---

## 4. Inter-Application Dependency Summary

```
                ┌────────────────────────────────────────────┐
                │          Core CardDemo (VSAM/CICS)          │
                │                                             │
                │  Online Programs ◄──COMMAREA──▶ VSAM Files  │
                │  Batch Programs  ◄──JCL/DD───▶ VSAM Files   │
                └──────┬──────────────────┬──────────────────┘
                       │                  │
            ┌──────────▼──────┐  ┌────────▼────────────────┐
            │ Authorization   │  │ Transaction Type         │
            │ Sub-App         │  │ Sub-App                  │
            │ (IMS/DB2/MQ)    │  │ (DB2)                    │
            │                 │  │                          │
            │ COPAU*C ─IMS──▶│  │ COTRTLIC ──DB2──▶        │
            │ CBPAUP0C        │  │ COTRTUPC                 │
            │ PAUDBLOD/BUNL   │  │ COBTUPDT                 │
            └─────────────────┘  └──────────────────────────┘
                       │
            ┌──────────▼──────┐
            │ VSAM-MQ         │
            │ Sub-App         │
            │                 │
            │ COACCT01 ─MQ──▶│
            │ CODATE01        │
            └─────────────────┘
```
