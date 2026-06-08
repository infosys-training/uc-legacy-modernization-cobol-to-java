import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse } from '../models/user.model';

const TOKEN_KEY = 'carddemo_token';
const USER_KEY = 'carddemo_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = '/api/v1/users';
  private currentUser = signal<AuthResponse | null>(this.loadUser());

  readonly user = this.currentUser.asReadonly();
  readonly isLoggedIn = computed(() => !!this.currentUser());
  readonly isAdmin = computed(() => this.currentUser()?.userType === 'A');

  constructor(private http: HttpClient, private router: Router) {}

  login(userId: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/authenticate`, { userId, password })
      .pipe(tap(res => {
        if (res.authenticated && res.token) {
          localStorage.setItem(TOKEN_KEY, res.token);
          localStorage.setItem(USER_KEY, JSON.stringify(res));
          this.currentUser.set(res);
        }
      }));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private loadUser(): AuthResponse | null {
    const stored = localStorage.getItem(USER_KEY);
    return stored ? JSON.parse(stored) : null;
  }
}
