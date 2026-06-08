import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Transaction } from '../models/transaction.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private readonly url = '/api/v1/transactions';

  constructor(private http: HttpClient) {}

  list(page = 0, size = 10): Observable<Page<Transaction>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Transaction>>(this.url, { params });
  }

  get(id: string): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.url}/${id}`);
  }

  getByCard(cardNumber: string, page = 0, size = 10): Observable<Page<Transaction>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Transaction>>(`${this.url}/by-card/${cardNumber}`, { params });
  }

  getByAccount(accountId: number, page = 0, size = 10): Observable<Page<Transaction>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Transaction>>(`${this.url}/by-account/${accountId}`, { params });
  }

  create(txn: Partial<Transaction>): Observable<Transaction> {
    return this.http.post<Transaction>(this.url, txn);
  }
}
