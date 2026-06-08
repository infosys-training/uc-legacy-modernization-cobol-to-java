import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserSecurity } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly url = '/api/v1/users';

  constructor(private http: HttpClient) {}

  list(): Observable<UserSecurity[]> {
    return this.http.get<UserSecurity[]>(this.url);
  }

  get(userId: string): Observable<UserSecurity> {
    return this.http.get<UserSecurity>(`${this.url}/${userId}`);
  }

  create(user: UserSecurity): Observable<UserSecurity> {
    return this.http.post<UserSecurity>(this.url, user);
  }

  update(userId: string, user: Partial<UserSecurity>): Observable<UserSecurity> {
    return this.http.put<UserSecurity>(`${this.url}/${userId}`, user);
  }

  delete(userId: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${userId}`);
  }
}
