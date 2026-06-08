import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransactionType, TransactionCategory } from '../models/transaction-type.model';

@Injectable({ providedIn: 'root' })
export class TransactionTypeService {
  private readonly url = '/api/v1/transaction-types';

  constructor(private http: HttpClient) {}

  list(): Observable<TransactionType[]> {
    return this.http.get<TransactionType[]>(this.url);
  }

  get(typeCode: string): Observable<TransactionType> {
    return this.http.get<TransactionType>(`${this.url}/${typeCode}`);
  }

  create(type: TransactionType): Observable<TransactionType> {
    return this.http.post<TransactionType>(this.url, type);
  }

  update(typeCode: string, type: Partial<TransactionType>): Observable<TransactionType> {
    return this.http.put<TransactionType>(`${this.url}/${typeCode}`, type);
  }

  delete(typeCode: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${typeCode}`);
  }

  listCategories(typeCode: string): Observable<TransactionCategory[]> {
    return this.http.get<TransactionCategory[]>(`${this.url}/${typeCode}/categories`);
  }
}
