import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account, AccountDetails } from '../models/account.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly url = '/api/v1/accounts';

  constructor(private http: HttpClient) {}

  list(page = 0, size = 10): Observable<Page<Account>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Account>>(this.url, { params });
  }

  get(id: number): Observable<Account> {
    return this.http.get<Account>(`${this.url}/${id}`);
  }

  getDetails(id: number): Observable<AccountDetails> {
    return this.http.get<AccountDetails>(`${this.url}/${id}/details`);
  }

  create(account: Partial<Account>): Observable<Account> {
    return this.http.post<Account>(this.url, account);
  }

  update(id: number, account: Partial<Account>): Observable<Account> {
    return this.http.put<Account>(`${this.url}/${id}`, account);
  }
}
