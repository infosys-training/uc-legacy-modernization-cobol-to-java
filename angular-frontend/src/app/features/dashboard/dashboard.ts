import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="dashboard">
      <div class="welcome-banner">
        <div class="welcome-text">
          <h2>Welcome back, {{ auth.user()?.firstName ?? auth.user()?.userId }}</h2>
          <p>{{ auth.isAdmin() ? 'Administrator Dashboard' : 'User Dashboard' }} &mdash; Manage accounts, cards, and transactions</p>
        </div>
        <div class="welcome-meta">
          <span class="meta-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            Last login: Today
          </span>
        </div>
      </div>

      <div class="section-title">Quick Access</div>
      <div class="grid">
        <a routerLink="/accounts" class="card">
          <div class="card-icon accounts">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/></svg>
          </div>
          <div class="card-content">
            <h3>Accounts</h3>
            <p>View and manage credit card accounts, balances, and limits</p>
          </div>
          <svg class="card-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
        </a>
        <a routerLink="/cards" class="card">
          <div class="card-icon cards">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
          </div>
          <div class="card-content">
            <h3>Cards</h3>
            <p>Credit card inventory, cardholder details, and status management</p>
          </div>
          <svg class="card-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
        </a>
        <a routerLink="/transactions" class="card">
          <div class="card-icon transactions">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
          </div>
          <div class="card-content">
            <h3>Transactions</h3>
            <p>Transaction history, new entries, and payment processing</p>
          </div>
          <svg class="card-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
        </a>
        @if (auth.isAdmin()) {
          <a routerLink="/users" class="card admin">
            <div class="card-icon users">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
            </div>
            <div class="card-content">
              <h3>Users</h3>
              <p>User administration, roles, and access control</p>
            </div>
            <svg class="card-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </a>
          <a routerLink="/transaction-types" class="card admin">
            <div class="card-icon types">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 7h16M4 12h16M4 17h10"/></svg>
            </div>
            <div class="card-content">
              <h3>Transaction Types</h3>
              <p>Manage transaction categories and type codes</p>
            </div>
            <svg class="card-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </a>
        }
      </div>
    </div>
  `,
  styles: [`
    .dashboard { padding: 1.75rem 2rem; max-width: 1280px; margin: 0 auto; }

    .welcome-banner {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: linear-gradient(135deg, #1e3a5f 0%, #2a5082 100%);
      border-radius: 12px;
      padding: 1.75rem 2rem;
      margin-bottom: 2rem;
      color: #fff;
    }
    .welcome-text h2 { font-size: 1.4rem; font-weight: 700; margin-bottom: 0.25rem; }
    .welcome-text p { color: rgba(255,255,255,0.7); font-size: 0.9rem; }
    .meta-item { display: flex; align-items: center; gap: 0.4rem; color: rgba(255,255,255,0.5); font-size: 0.8rem; }
    .meta-item svg { width: 14px; height: 14px; }

    .section-title { font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.08em; color: #64748b; font-weight: 600; margin-bottom: 0.75rem; }

    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 1rem; }

    .card {
      display: flex;
      align-items: center;
      gap: 1rem;
      background: #fff;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      padding: 1.25rem 1.5rem;
      text-decoration: none;
      color: inherit;
      transition: all 0.2s;
    }
    .card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); transform: translateY(-2px); border-color: #cbd5e1; }
    .card.admin { border-left: 3px solid #7c3aed; }

    .card-icon {
      width: 44px;
      height: 44px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }
    .card-icon svg { width: 22px; height: 22px; }
    .card-icon.accounts { background: #eff6ff; color: #2563eb; }
    .card-icon.cards { background: #f0fdf4; color: #16a34a; }
    .card-icon.transactions { background: #fefce8; color: #ca8a04; }
    .card-icon.users { background: #faf5ff; color: #7c3aed; }
    .card-icon.types { background: #fff1f2; color: #e11d48; }

    .card-content { flex: 1; }
    .card-content h3 { font-size: 0.95rem; font-weight: 600; color: #1e3a5f; margin-bottom: 0.2rem; }
    .card-content p { margin: 0; color: #64748b; font-size: 0.8rem; line-height: 1.4; }

    .card-arrow { width: 18px; height: 18px; color: #94a3b8; flex-shrink: 0; transition: transform 0.2s; }
    .card:hover .card-arrow { transform: translateX(3px); color: #4361ee; }
  `]
})
export class DashboardComponent {
  constructor(public auth: AuthService) {}
}
