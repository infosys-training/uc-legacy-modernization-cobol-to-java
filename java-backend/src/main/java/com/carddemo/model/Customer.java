package com.carddemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Maps to COBOL copybook CVCUS01Y.cpy — CUSTOMER-RECORD (500-byte VSAM KSDS).
 *
 * COBOL layout:
 *   CUST-ID                PIC 9(09)
 *   CUST-FIRST-NAME        PIC X(25)
 *   CUST-MIDDLE-NAME       PIC X(25)
 *   CUST-LAST-NAME         PIC X(25)
 *   CUST-ADDR-LINE-1..3    PIC X(50) each
 *   CUST-ADDR-STATE-CD     PIC X(02)
 *   CUST-ADDR-COUNTRY-CD   PIC X(03)
 *   CUST-ADDR-ZIP          PIC X(10)
 *   CUST-PHONE-NUM-1/2     PIC X(15)
 *   CUST-SSN               PIC 9(09)
 *   CUST-GOVT-ISSUED-ID    PIC X(20)
 *   CUST-DOB-YYYY-MM-DD    PIC X(10)
 *   CUST-EFT-ACCOUNT-ID    PIC X(10)
 *   CUST-PRI-CARD-HOLDER   PIC X(01)
 *   CUST-FICO-CREDIT-SCORE PIC 9(03)
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "cust_id")
    private Long custId;

    @Column(name = "first_name", length = 25, nullable = false)
    private String firstName;

    @Column(name = "middle_name", length = 25)
    private String middleName;

    @Column(name = "last_name", length = 25, nullable = false)
    private String lastName;

    @Column(name = "addr_line_1", length = 50)
    private String addressLine1;

    @Column(name = "addr_line_2", length = 50)
    private String addressLine2;

    @Column(name = "addr_line_3", length = 50)
    private String addressLine3;

    @Column(name = "addr_state_cd", length = 2)
    private String addressStateCode;

    @Column(name = "addr_country_cd", length = 3)
    private String addressCountryCode;

    @Column(name = "addr_zip", length = 10)
    private String addressZip;

    @Column(name = "phone_num_1", length = 15)
    private String phoneNumber1;

    @Column(name = "phone_num_2", length = 15)
    private String phoneNumber2;

    @Column(name = "ssn", length = 9)
    private String ssn;

    @Column(name = "govt_issued_id", length = 20)
    private String governmentIssuedId;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "eft_account_id", length = 10)
    private String eftAccountId;

    @Column(name = "pri_card_holder_ind", length = 1)
    private String primaryCardHolderIndicator;

    @Column(name = "fico_credit_score")
    private Integer ficoCreditScore;

    public Customer() {}

    public Long getCustId() { return custId; }
    public void setCustId(Long custId) { this.custId = custId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getAddressLine3() { return addressLine3; }
    public void setAddressLine3(String addressLine3) { this.addressLine3 = addressLine3; }

    public String getAddressStateCode() { return addressStateCode; }
    public void setAddressStateCode(String addressStateCode) { this.addressStateCode = addressStateCode; }

    public String getAddressCountryCode() { return addressCountryCode; }
    public void setAddressCountryCode(String addressCountryCode) { this.addressCountryCode = addressCountryCode; }

    public String getAddressZip() { return addressZip; }
    public void setAddressZip(String addressZip) { this.addressZip = addressZip; }

    public String getPhoneNumber1() { return phoneNumber1; }
    public void setPhoneNumber1(String phoneNumber1) { this.phoneNumber1 = phoneNumber1; }

    public String getPhoneNumber2() { return phoneNumber2; }
    public void setPhoneNumber2(String phoneNumber2) { this.phoneNumber2 = phoneNumber2; }

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public String getGovernmentIssuedId() { return governmentIssuedId; }
    public void setGovernmentIssuedId(String governmentIssuedId) { this.governmentIssuedId = governmentIssuedId; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getEftAccountId() { return eftAccountId; }
    public void setEftAccountId(String eftAccountId) { this.eftAccountId = eftAccountId; }

    public String getPrimaryCardHolderIndicator() { return primaryCardHolderIndicator; }
    public void setPrimaryCardHolderIndicator(String primaryCardHolderIndicator) { this.primaryCardHolderIndicator = primaryCardHolderIndicator; }

    public Integer getFicoCreditScore() { return ficoCreditScore; }
    public void setFicoCreditScore(Integer ficoCreditScore) { this.ficoCreditScore = ficoCreditScore; }
}
