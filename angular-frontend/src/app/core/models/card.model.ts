export interface Card {
  cardNumber: string;
  accountId: number;
  cvvCode: string | null;
  embossedName: string | null;
  expirationDate: string | null;
  activeStatus: string;
}
