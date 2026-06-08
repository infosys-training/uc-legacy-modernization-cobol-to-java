export interface TransactionType {
  typeCode: string;
  description: string | null;
}

export interface TransactionCategory {
  id: {
    typeCode: string;
    categoryCode: number;
  };
  description: string | null;
}
