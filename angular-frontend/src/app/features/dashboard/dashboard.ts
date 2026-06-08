import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="dashboard">
      <h2>Welcome, {{ auth.user()?.firstName ?? auth.user()?.userId }}</h2>
      <p class="role">{{ auth.isAdmin() ? 'Administrator' : 'User' }}</p>
      <div class="grid">
        <a routerLink="/accounts" class="card">
          <span class="icon">&#x1F4B3;</span>
          <h3>Accounts</h3>
          <p>View and manage credit card accounts</p>
        </a>
        <a routerLink="/cards" class="card">
          <span class="icon">&#x1F4B4;</span>
          <h3>Cards</h3>
          <p>Credit card inventory and status</p>
        </a>
        <a routerLink="/transactions" class="card">
          <span class="icon">&#x1F4C4;</span>
          <h3>Transactions</h3>
          <p>Transaction list, view, and entry</p>
        </a>
        @if (auth.isAdmin()) {
          <a routerLink="/users" class="card admin">
            <span class="icon">&#x1F464;</span>
            <h3>Users</h3>
            <p>User administration</p>
          </a>
          <a routerLink="/transaction-types" class="card admin">
            <span class="icon">&#x1F3F7;</span>
            <h3>Transaction Types</h3>
            <p>Manage transaction types and categories</p>
          </a>
        }
      </div>
    </div>
  `,
  styles: [`
    .dashboard { padding: 1.5rem; }
    h2 { margin: 0; color: #1a1a2e; }
    .role { color: #666; margin: 0.25rem 0 1.5rem; font-size: 0.9rem; }
    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 1rem; }
    .card { display: block; background: #fff; border: 1px solid #e0e0e0; border-radius: 10px; padding: 1.5rem; text-decoration: none; color: inherit; transition: box-shadow 0.2s, transform 0.2s; }
    .card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.1); transform: translateY(-2px); }
    .card.admin { border-left: 4px solid #e74c3c; }
    .icon { font-size: 2rem; }
    h3 { margin: 0.5rem 0 0.25rem; }
    p { margin: 0; color: #777; font-size: 0.85rem; }
  `]
})
export class DashboardComponent {
  constructor(public auth: AuthService) {}
}
