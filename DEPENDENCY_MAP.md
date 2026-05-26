# CardDemo Dependency Map

This document maps inter-program calls, dataset lineage, and the batch processing pipeline.

---

## Call Graph

### Online CICS Program Flow (EXEC CICS XCTL / RETURN TRANSID)

```mermaid
graph TD
    COSGN00C["COSGN00C<br/>Signon (CC00)"]
    COADM01C["COADM01C<br/>Admin Menu (CA00)"]
    COMEN01C["COMEN01C<br/>Main Menu (CM00)"]

    COSGN00C -->|Admin user| COADM01C
    COSGN00C -->|Regular user| COMEN01C

    %% Admin menu targets
    COUSR00C["COUSR00C<br/>User List (CU00)"]
    COUSR01C["COUSR01C<br/>User Add (CU01)"]
    COUSR02C["COUSR02C<br/>User Update (CU02)"]
    COUSR03C["COUSR03C<br/>User Delete (CU03)"]
    COTRTLIC["COTRTLIC<br/>Tran Type List (CTLI)"]
    COTRTUPC["COTRTUPC<br/>Tran Type Update (CTTU)"]

    COADM01C --> COUSR00C
    COADM01C --> COUSR01C
    COADM01C --> COUSR02C
    COADM01C --> COUSR03C
    COADM01C --> COTRTLIC
    COADM01C --> COTRTUPC
    COUSR00C --> COUSR02C
    COUSR00C --> COUSR03C

    %% Main menu targets
    COACTVWC["COACTVWC<br/>Account View (CAVW)"]
    COACTUPC["COACTUPC<br/>Account Update (CAUP)"]
    COCRDLIC["COCRDLIC<br/>Card List (CCLI)"]
    COCRDSLC["COCRDSLC<br/>Card Detail (CCDL)"]
    COCRDUPC["COCRDUPC<br/>Card Update (CCUP)"]
    COTRN00C["COTRN00C<br/>Tran List (CT00)"]
    COTRN01C["COTRN01C<br/>Tran View (CT01)"]
    COTRN02C["COTRN02C<br/>Tran Add (CT02)"]
    CORPT00C["CORPT00C<br/>Reports (CR00)"]
    COBIL00C["COBIL00C<br/>Bill Pay (CB00)"]
    COPAUS0C["COPAUS0C<br/>Auth View"]

    COMEN01C --> COACTVWC
    COMEN01C --> COACTUPC
    COMEN01C --> COCRDLIC
    COMEN01C --> COCRDSLC
    COMEN01C --> COCRDUPC
    COMEN01C --> COTRN00C
    COMEN01C --> COTRN01C
    COMEN01C --> COTRN02C
    COMEN01C --> CORPT00C
    COMEN01C --> COBIL00C
    COMEN01C --> COPAUS0C

    %% Cross-navigation between programs
    COCRDLIC --> COCRDSLC
    COCRDLIC --> COCRDUPC
    COACTVWC --> COCRDLIC
    COACTVWC --> COCRDSLC
    COACTVWC --> COCRDUPC
    COACTUPC --> COCRDUPC
    COACTUPC --> COCRDLIC
    COACTUPC --> COCRDSLC
    COTRN00C --> COTRN01C

    %% Utility calls
    CSUTLDTC["CSUTLDTC<br/>Date Validation"]
    COTRN02C -->|CALL| CSUTLDTC
    CORPT00C -->|CALL| CSUTLDTC
```

### Batch Program Calls

| Caller | Callee | Mechanism | Purpose |
|---|---|---|---|
| CBSTM03A | CBSTM03B | CALL (subroutine) | Delegate all file I/O (open/close/read/write/rewrite) |
| CBACT01C | COBDATFT | CALL | Assembler date formatting routine |
| COTRN02C (online) | CSUTLDTC | CALL | Date validation via CEEDAYS LE API |
| CORPT00C | CSUTLDTC | CALL | Date validation via CEEDAYS LE API |
| All batch programs | CEE3ABD | CALL | LE abend handling |
| CORPT00C | (TDQ JOBS) | EXEC CICS WRITEQ TD | Submit batch JCL for report generation |

---

## Dataset Lineage

### VSAM Dataset → Program → JCL Mapping

| Dataset | VSAM Type | Defined By | Written By | Read By | I-O By |
|---|---|---|---|---|---|
| ACCTFILE / ACCTDAT | KSDS | ACCTFILE.jcl | (initial load) | CBACT01C, CBTRN01C, CBEXPORT, CBSTM03B, COACTVWC, COACTUPC, COBIL00C, COTRN02C | CBTRN02C, CBACT04C |
| CARDFILE / CARDDAT | KSDS | CARDFILE.jcl | (initial load) | CBACT02C, CBTRN01C, CBEXPORT, COCRDLIC, COCRDSLC, COCRDUPC | — |
| CUSTFILE / CUSTDAT | KSDS | CUSTFILE.jcl | (initial load) | CBCUS01C, CBTRN01C, CBEXPORT, CBSTM03B, COACTVWC, COACTUPC | — |
| XREFFILE / CCXREF / CXACAIX | KSDS + AIX | XREFFILE.jcl | (initial load) | CBACT03C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, CBSTM03B, CBACT04C, COACTVWC, COACTUPC, COBIL00C, COTRN02C | — |
| TRANSACT / TRANFILE | KSDS | TRANFILE.jcl | CBTRN02C, CBACT04C, COBIL00C, COTRN02C | CBTRN03C, CBEXPORT, COTRN00C, COTRN01C, CORPT00C | — |
| DALYTRAN | Sequential | (external feed) | (external) | CBTRN01C, CBTRN02C | — |
| DALYREJS | ESDS | DALYREJS.jcl | CBTRN02C | — | — |
| TCATBALF | KSDS | TCATBALF.jcl | — | CBACT04C | CBTRN02C |
| DISCGRP | KSDS | DISCGRP.jcl | (initial load) | CBACT04C | — |
| TRANTYPE | KSDS | TRANTYPE.jcl | (initial load) | CBTRN03C | — |
| TRANCATG | KSDS | TRANCATG.jcl | (initial load) | CBTRN03C | — |
| USRSEC | KSDS | DUSRSECJ.jcl | COUSR01C, COUSR02C, COUSR03C | COSGN00C, COADM01C, COUSR00C | — |
| CARDAIX | AIX | CARDFILE.jcl | (built by IDCAMS) | COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC | — |
| EXPFILE | Sequential | CBEXPORT.jcl | CBEXPORT | CBIMPORT | — |

### Non-VSAM Datasets

| Dataset | Format | Created By | Used By |
|---|---|---|---|
| STMTFILE | Sequential (text) | CBSTM03A | (printed output) |
| HTMLFILE | Sequential (HTML) | CBSTM03A | (browser/email output) |
| TRANREPT | Sequential (text) | CBTRN03C | (printed report) |
| DATEPARM | Sequential | TRANREPT.prc (SORT SYSIN) | CBTRN03C |
| OUTFILE, ARRYFILE, VBRCFILE | Sequential | CBACT01C | (debugging/export) |
| ERROUT | Sequential | CBIMPORT | (error/reject records) |

---

## Batch Pipeline Flow

The core batch processing pipeline runs in this sequence:

```mermaid
graph LR
    DALYTRAN["DALYTRAN<br/>(Daily Transaction Feed)"]
    CBTRN02C["CBTRN02C<br/>POSTTRAN.jcl<br/>Validate & Post"]
    TRANSACT["TRANSACT<br/>(Transaction VSAM)"]
    DALYREJS["DALYREJS<br/>(Rejected Transactions)"]
    ACCTFILE["ACCTFILE<br/>(Account Master)"]
    TCATBALF["TCATBALF<br/>(Category Balances)"]
    CBACT04C["CBACT04C<br/>INTCALC.jcl<br/>Interest Calculator"]
    DISCGRP["DISCGRP<br/>(Interest Rates)"]
    XREFFILE["XREFFILE<br/>(Card Cross-Ref)"]
    CBTRN03C["CBTRN03C<br/>TRANREPT.jcl<br/>Transaction Report"]
    TRANTYPE["TRANTYPE"]
    TRANCATG["TRANCATG"]
    TRANREPT["TRANREPT<br/>(Report Output)"]
    CBSTM03A["CBSTM03A<br/>CREASTMT.JCL<br/>Statement Generator"]
    CUSTFILE["CUSTFILE<br/>(Customer Master)"]
    STMTFILE["STMTFILE / HTMLFILE<br/>(Statements)"]

    DALYTRAN --> CBTRN02C
    CBTRN02C --> TRANSACT
    CBTRN02C --> DALYREJS
    CBTRN02C --> ACCTFILE
    CBTRN02C --> TCATBALF

    TCATBALF --> CBACT04C
    XREFFILE --> CBACT04C
    DISCGRP --> CBACT04C
    ACCTFILE --> CBACT04C
    CBACT04C -->|interest txns| TRANSACT
    CBACT04C -->|update balances| ACCTFILE

    TRANSACT --> CBTRN03C
    XREFFILE --> CBTRN03C
    TRANTYPE --> CBTRN03C
    TRANCATG --> CBTRN03C
    CBTRN03C --> TRANREPT

    TRANSACT --> CBSTM03A
    XREFFILE --> CBSTM03A
    CUSTFILE --> CBSTM03A
    ACCTFILE --> CBSTM03A
    CBSTM03A --> STMTFILE
```

### Pipeline Step Detail

| Step | JCL Job | Program | Inputs | Outputs | Description |
|---|---|---|---|---|---|
| 1 | POSTTRAN.jcl | CBTRN02C | DALYTRAN, XREFFILE | TRANFILE, DALYREJS; updates ACCTFILE, TCATBALF | Validate daily transactions (overlimit, expiration), post valid ones, write rejects |
| 2 | INTCALC.jcl | CBACT04C | TCATBALF, XREFFILE, DISCGRP, ACCTFILE | TRANSACT (interest txns); updates ACCTFILE | Compute monthly interest using category balances and disclosure group rates |
| 3 | TRANREPT.jcl | CBTRN03C (via TRANREPT.prc) | TRANFILE (sorted), CARDXREF, TRANTYPE, TRANCATG | TRANREPT | Unload/sort transactions by date, produce formatted report with page/account/grand totals |
| 4 | CREASTMT.JCL | CBSTM03A (+ CBSTM03B) | TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | STMTFILE, HTMLFILE | Generate account statements in text and HTML formats |

### Data Migration Pipeline

```
Export: CBEXPORT (CBEXPORT.jcl)
  Reads: CUSTFILE + ACCTFILE + XREFFILE + TRANSACT + CARDFILE
  Writes: EXPFILE (multi-record sequential, type C/A/X/T/D)

Import: CBIMPORT (CBIMPORT.jcl)
  Reads: EXPFILE
  Writes: CUSTOUT + ACCTOUT + XREFOUT + TRNXOUT + CARDOUT + ERROUT
  (Normalized files with validation; errors to ERROUT)
```

---

## CICS Resource Dependencies

| Resource Type | Name | Used By |
|---|---|---|
| VSAM File | USRSEC | COSGN00C, COADM01C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| VSAM File | ACCTDAT | COACTVWC, COACTUPC, COBIL00C, COTRN02C |
| VSAM File | CUSTDAT | COACTVWC, COACTUPC |
| VSAM File | CARDDAT | COCRDLIC, COCRDSLC, COCRDUPC |
| VSAM File | TRANSACT | COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C |
| VSAM File (AIX) | CXACAIX | COACTVWC, COACTUPC, COBIL00C, COTRN02C |
| VSAM File (AIX) | CARDAIX | COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC |
| VSAM File (AIX) | CCXREF | COTRN02C |
| TD Queue | JOBS | CORPT00C (writes JCL to submit batch report) |
| Transaction | CC00 | COSGN00C |
| Transaction | CA00 | COADM01C |
| Transaction | CM00 | COMEN01C |
| Transaction | CAVW | COACTVWC |
| Transaction | CAUP | COACTUPC |
| Transaction | CB00 | COBIL00C |
| Transaction | CCLI | COCRDLIC |
| Transaction | CCDL | COCRDSLC |
| Transaction | CCUP | COCRDUPC |
| Transaction | CT00 | COTRN00C |
| Transaction | CT01 | COTRN01C |
| Transaction | CT02 | COTRN02C |
| Transaction | CR00 | CORPT00C |
| Transaction | CU00 | COUSR00C |
| Transaction | CU01 | COUSR01C |
| Transaction | CU02 | COUSR02C |
| Transaction | CU03 | COUSR03C |
