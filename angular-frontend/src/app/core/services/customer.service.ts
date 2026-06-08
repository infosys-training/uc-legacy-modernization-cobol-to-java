import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../models/account.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private readonly url = '/api/v1/customers';

  constructor(private http: HttpClient) {}

  list(page = 0, size = 10): Observable<Page<Customer>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Customer>>(this.url, { params });
  }

  get(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.url}/${id}`);
  }

  create(customer: Partial<Customer>): Observable<Customer> {
    return this.http.post<Customer>(this.url, customer);
  }

  update(id: number, customer: Partial<Customer>): Observable<Customer> {
    return this.http.put<Customer>(`${this.url}/${id}`, customer);
  }
}
