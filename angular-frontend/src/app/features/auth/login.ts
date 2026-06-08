import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <h1>CardDemo</h1>
        <p class="subtitle">Credit Card Management System</p>
        <form (ngSubmit)="onLogin()">
          <div class="form-group">
            <label for="userId">User ID</label>
            <input id="userId" type="text" [(ngModel)]="userId" name="userId"
                   placeholder="e.g. admin01" required maxlength="8" />
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input id="password" type="password" [(ngModel)]="password" name="password"
                   placeholder="Password" required />
          </div>
          @if (error()) {
            <div class="error">{{ error() }}</div>
          }
          <button type="submit" [disabled]="loading()">
            {{ loading() ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>
        <p class="hint">Default: admin01 / password</p>
      </div>
    </div>
  `,
  styles: [`
    .login-container { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #1a1a2e; }
    .login-card { background: #fff; padding: 2.5rem; border-radius: 12px; box-shadow: 0 8px 32px rgba(0,0,0,0.2); width: 100%; max-width: 400px; }
    h1 { margin: 0 0 0.25rem; color: #1a1a2e; font-size: 1.75rem; }
    .subtitle { color: #666; margin: 0 0 1.5rem; font-size: 0.9rem; }
    .form-group { margin-bottom: 1rem; }
    label { display: block; margin-bottom: 0.25rem; font-weight: 600; font-size: 0.85rem; color: #333; }
    input { width: 100%; padding: 0.65rem; border: 1px solid #ddd; border-radius: 6px; font-size: 0.95rem; box-sizing: border-box; }
    input:focus { outline: none; border-color: #4361ee; box-shadow: 0 0 0 3px rgba(67,97,238,0.15); }
    button { width: 100%; padding: 0.75rem; background: #4361ee; color: white; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; margin-top: 0.5rem; }
    button:hover:not(:disabled) { background: #3a56d4; }
    button:disabled { opacity: 0.6; cursor: not-allowed; }
    .error { background: #fee; color: #c00; padding: 0.5rem 0.75rem; border-radius: 6px; margin-bottom: 0.75rem; font-size: 0.85rem; }
    .hint { text-align: center; color: #999; font-size: 0.8rem; margin-top: 1rem; }
  `]
})
export class LoginComponent {
  userId = '';
  password = '';
  error = signal('');
  loading = signal(false);

  constructor(private auth: AuthService, private router: Router) {}

  onLogin(): void {
    this.loading.set(true);
    this.error.set('');
    this.auth.login(this.userId, this.password).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.authenticated) {
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Invalid User ID or Password');
      }
    });
  }
}
