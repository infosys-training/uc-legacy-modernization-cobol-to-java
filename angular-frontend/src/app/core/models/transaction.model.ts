export interface Transaction {
  transactionId: string;
  typeCode: string;
  categoryCode: number | null;
  source: string | null;
  description: string | null;
  amount: number;
  merchantId: number | null;
  merchantName: string | null;
  merchantCity: string | null;
  merchantZip: string | null;
  cardNumber: string;
  originTimestamp: string | null;
  processedTimestamp: string | null;
}
