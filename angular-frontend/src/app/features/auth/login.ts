import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="login-page">
      <div class="login-left">
        <div class="login-branding">
          <svg class="login-logo" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
            <line x1="1" y1="10" x2="23" y2="10"/>
          </svg>
          <h1>CardDemo</h1>
          <p class="tagline">Enterprise Credit Card Management System</p>
          <div class="features">
            <div class="feature">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <span>Secure JWT Authentication</span>
            </div>
            <div class="feature">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
              <span>Real-time Transaction Processing</span>
            </div>
            <div class="feature">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              <span>Role-based Access Control</span>
            </div>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-card">
          <h2>Sign In</h2>
          <p class="login-subtitle">Enter your credentials to access your account</p>
          <form (ngSubmit)="onLogin()">
            <div class="form-group">
              <label for="userId">User ID</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                <input id="userId" type="text" [(ngModel)]="userId" name="userId"
                       placeholder="e.g. admin01" required maxlength="8" />
              </div>
            </div>
            <div class="form-group">
              <label for="password">Password</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                <input id="password" type="password" [(ngModel)]="password" name="password"
                       placeholder="Enter your password" required />
              </div>
            </div>
            @if (error()) {
              <div class="error-msg">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
                {{ error() }}
              </div>
            }
            <button type="submit" class="submit-btn" [disabled]="loading()">
              @if (loading()) {
                <span class="spinner"></span> Authenticating...
              } @else {
                Sign In
              }
            </button>
          </form>
          <div class="login-footer">
            <p class="hint">Demo credentials: <strong>admin01</strong> / <strong>password</strong></p>
          </div>
        </div>
        <p class="copyright">&copy; {{ currentYear }} Infosys Ltd. CardDemo Enterprise v1.0.0</p>
      </div>
    </div>
  `,
  styles: [`
    .login-page { display: flex; min-height: 100vh; }

    /* Left panel */
    .login-left {
      flex: 1;
      background: linear-gradient(135deg, #1e3a5f 0%, #0f2744 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 3rem;
    }
    .login-branding { max-width: 420px; }
    .login-logo { width: 56px; height: 56px; color: #60a5fa; margin-bottom: 1.25rem; }
    .login-branding h1 { color: #fff; font-size: 2.25rem; font-weight: 700; margin-bottom: 0.5rem; letter-spacing: -0.02em; }
    .tagline { color: rgba(255,255,255,0.6); font-size: 1rem; margin-bottom: 2.5rem; }
    .features { display: flex; flex-direction: column; gap: 1rem; }
    .feature { display: flex; align-items: center; gap: 0.75rem; color: rgba(255,255,255,0.7); font-size: 0.9rem; }
    .feature svg { width: 20px; height: 20px; color: #60a5fa; flex-shrink: 0; }

    /* Right panel */
    .login-right {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 3rem;
      background: #f8fafc;
    }
    .login-card {
      background: #fff;
      padding: 2.5rem;
      border-radius: 16px;
      box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
      width: 100%;
      max-width: 400px;
    }
    .login-card h2 { color: #1e3a5f; font-size: 1.5rem; font-weight: 700; margin-bottom: 0.25rem; }
    .login-subtitle { color: #64748b; font-size: 0.875rem; margin-bottom: 1.75rem; }

    .form-group { margin-bottom: 1.25rem; }
    .input-wrapper { position: relative; }
    .input-icon { position: absolute; left: 0.75rem; top: 50%; transform: translateY(-50%); width: 18px; height: 18px; color: #94a3b8; pointer-events: none; }
    .input-wrapper input {
      width: 100%;
      padding: 0.75rem 0.85rem 0.75rem 2.5rem;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      font-size: 0.9rem;
      transition: border-color 0.2s, box-shadow 0.2s;
      background: #fff;
    }
    .input-wrapper input:focus { outline: none; border-color: #4361ee; box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.12); }
    label { display: block; margin-bottom: 0.4rem; font-weight: 500; font-size: 0.85rem; color: #2d3748; }

    .error-msg {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      background: #fef2f2;
      color: #dc2626;
      padding: 0.65rem 0.85rem;
      border-radius: 8px;
      margin-bottom: 1rem;
      font-size: 0.85rem;
      border: 1px solid #fecaca;
    }
    .error-msg svg { width: 16px; height: 16px; flex-shrink: 0; }

    .submit-btn {
      width: 100%;
      padding: 0.8rem;
      background: linear-gradient(135deg, #4361ee, #3451d1);
      color: white;
      border: none;
      border-radius: 8px;
      font-size: 0.95rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
    }
    .submit-btn:hover:not(:disabled) { background: linear-gradient(135deg, #3451d1, #2a41b0); box-shadow: 0 4px 12px rgba(67, 97, 238, 0.3); transform: translateY(-1px); }
    .submit-btn:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }

    .spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }

    .login-footer { margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid #f1f5f9; }
    .hint { text-align: center; color: #94a3b8; font-size: 0.8rem; }
    .hint strong { color: #64748b; }

    .copyright { margin-top: 2rem; color: #94a3b8; font-size: 0.75rem; }

    @media (max-width: 768px) {
      .login-page { flex-direction: column; }
      .login-left { display: none; }
      .login-right { padding: 2rem; }
    }
  `]
})
export class LoginComponent {
  userId = '';
  password = '';
  error = signal('');
  loading = signal(false);
  currentYear = new Date().getFullYear();

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
