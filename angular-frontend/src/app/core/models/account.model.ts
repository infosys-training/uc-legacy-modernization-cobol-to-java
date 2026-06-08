export interface Account {
  acctId: number;
  activeStatus: string;
  currentBalance: number;
  creditLimit: number;
  cashCreditLimit: number;
  openDate: string | null;
  expirationDate: string | null;
  reissueDate: string | null;
  currentCycleCredit: number;
  currentCycleDebit: number;
  addressZip: string | null;
  groupId: string | null;
}

export interface AccountDetails {
  account: Account;
  cardXrefs: CardXref[];
  customer?: Customer;
}

export interface CardXref {
  cardNumber: string;
  accountId: number;
  customerId: number;
}

export interface Customer {
  custId: number;
  firstName: string;
  middleName: string | null;
  lastName: string;
  addressLine1: string | null;
  addressLine2: string | null;
  addressLine3: string | null;
  addressStateCode: string | null;
  addressCountryCode: string | null;
  addressZip: string | null;
  phoneNumber1: string | null;
  phoneNumber2: string | null;
  ssn: string | null;
  governmentIssuedId: string | null;
  dateOfBirth: string | null;
  eftAccountId: string | null;
  primaryCardHolderIndicator: string | null;
  ficoCreditScore: number | null;
}
