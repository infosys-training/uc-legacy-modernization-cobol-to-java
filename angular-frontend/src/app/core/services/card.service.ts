import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Card } from '../models/card.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class CardService {
  private readonly url = '/api/v1/cards';

  constructor(private http: HttpClient) {}

  list(page = 0, size = 10): Observable<Page<Card>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Card>>(this.url, { params });
  }

  get(cardNumber: string): Observable<Card> {
    return this.http.get<Card>(`${this.url}/${cardNumber}`);
  }

  getByAccount(accountId: number, page = 0, size = 10): Observable<Page<Card>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Card>>(`${this.url}/by-account/${accountId}`, { params });
  }

  create(card: Partial<Card>, customerId: number): Observable<Card> {
    const params = new HttpParams().set('customerId', customerId);
    return this.http.post<Card>(this.url, card, { params });
  }

  update(cardNumber: string, card: Partial<Card>): Observable<Card> {
    return this.http.put<Card>(`${this.url}/${cardNumber}`, card);
  }
}
