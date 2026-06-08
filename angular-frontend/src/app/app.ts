import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    @if (auth.isLoggedIn()) {
      <nav class="navbar">
        <a routerLink="/dashboard" class="brand">CardDemo</a>
        <div class="nav-links">
          <a routerLink="/accounts" routerLinkActive="active">Accounts</a>
          <a routerLink="/cards" routerLinkActive="active">Cards</a>
          <a routerLink="/transactions" routerLinkActive="active">Transactions</a>
          @if (auth.isAdmin()) {
            <a routerLink="/users" routerLinkActive="active">Users</a>
            <a routerLink="/transaction-types" routerLinkActive="active">Types</a>
          }
        </div>
        <div class="nav-right">
          <span class="user-info">{{ auth.user()?.userId }}</span>
          <button (click)="auth.logout()" class="logout-btn">Logout</button>
        </div>
      </nav>
    }
    <main>
      <router-outlet />
    </main>
  `,
  styles: [`
    .navbar { display: flex; align-items: center; background: #1a1a2e; padding: 0 1.5rem; height: 48px; gap: 1.5rem; }
    .brand { color: #fff; font-weight: 700; font-size: 1.1rem; text-decoration: none; }
    .nav-links { display: flex; gap: 0.25rem; }
    .nav-links a { color: rgba(255,255,255,0.7); text-decoration: none; padding: 0.4rem 0.75rem; border-radius: 6px; font-size: 0.85rem; }
    .nav-links a:hover { color: #fff; background: rgba(255,255,255,0.1); }
    .nav-links a.active { color: #fff; background: rgba(67,97,238,0.5); }
    .nav-right { margin-left: auto; display: flex; align-items: center; gap: 0.75rem; }
    .user-info { color: rgba(255,255,255,0.6); font-size: 0.8rem; }
    .logout-btn { background: none; border: 1px solid rgba(255,255,255,0.3); color: rgba(255,255,255,0.8); padding: 0.3rem 0.75rem; border-radius: 6px; cursor: pointer; font-size: 0.8rem; }
    .logout-btn:hover { background: rgba(255,255,255,0.1); }
    main { min-height: calc(100vh - 48px); background: #f5f6fa; }
  `]
})
export class AppComponent {
  constructor(public auth: AuthService) {}
}
