import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    @if (auth.isLoggedIn()) {
      <!-- Production Header -->
      <header class="header">
        <div class="header-inner">
          <div class="header-left">
            <a routerLink="/dashboard" class="brand">
              <svg class="brand-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
                <line x1="1" y1="10" x2="23" y2="10"/>
              </svg>
              <div class="brand-text">
                <span class="brand-name">CardDemo</span>
                <span class="brand-tag">Enterprise</span>
              </div>
            </a>
          </div>

          <nav class="main-nav">
            <a routerLink="/accounts" routerLinkActive="active">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="nav-icon"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/></svg>
              Accounts
            </a>
            <a routerLink="/cards" routerLinkActive="active">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="nav-icon"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
              Cards
            </a>
            <a routerLink="/transactions" routerLinkActive="active">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="nav-icon"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
              Transactions
            </a>
            @if (auth.isAdmin()) {
              <a routerLink="/users" routerLinkActive="active">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="nav-icon"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                Users
              </a>
              <a routerLink="/transaction-types" routerLinkActive="active">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="nav-icon"><path d="M4 7h16M4 12h16M4 17h10"/></svg>
                Types
              </a>
            }
          </nav>

          <div class="header-right">
            <div class="user-menu">
              <div class="user-avatar">{{ (auth.user()?.firstName ?? 'U').charAt(0) }}</div>
              <div class="user-details">
                <span class="user-name">{{ auth.user()?.firstName }} {{ auth.user()?.lastName }}</span>
                <span class="user-role">{{ auth.isAdmin() ? 'Administrator' : 'User' }}</span>
              </div>
            </div>
            <button (click)="auth.logout()" class="logout-btn" title="Sign out">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="logout-icon"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            </button>
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <main class="main-content">
        <router-outlet />
      </main>

      <!-- Production Footer -->
      <footer class="footer">
        <div class="footer-inner">
          <div class="footer-left">
            <span class="footer-brand">CardDemo Enterprise</span>
            <span class="footer-sep">|</span>
            <span class="footer-copy">&copy; {{ currentYear }} Infosys Ltd. All rights reserved.</span>
          </div>
          <div class="footer-center">
            <span class="footer-env">Environment: Development</span>
            <span class="footer-sep">|</span>
            <span class="footer-version">v1.0.0</span>
          </div>
          <div class="footer-right">
            <a href="#" class="footer-link">Documentation</a>
            <a href="#" class="footer-link">Support</a>
            <a href="#" class="footer-link">Privacy Policy</a>
          </div>
        </div>
      </footer>
    } @else {
      <router-outlet />
    }
  `,
  styles: [`
    /* === Header === */
    .header {
      position: sticky;
      top: 0;
      z-index: 1000;
      background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      height: var(--header-height);
    }
    .header-inner {
      display: flex;
      align-items: center;
      height: 100%;
      padding: 0 1.5rem;
      max-width: 1440px;
      margin: 0 auto;
    }
    .header-left { display: flex; align-items: center; }
    .brand {
      display: flex;
      align-items: center;
      gap: 0.65rem;
      text-decoration: none;
      color: #fff;
    }
    .brand-icon { width: 28px; height: 28px; color: #60a5fa; }
    .brand-text { display: flex; flex-direction: column; line-height: 1.1; }
    .brand-name { font-size: 1.1rem; font-weight: 700; color: #fff; letter-spacing: -0.02em; }
    .brand-tag { font-size: 0.6rem; color: rgba(255,255,255,0.5); text-transform: uppercase; letter-spacing: 0.1em; font-weight: 500; }

    /* === Navigation === */
    .main-nav {
      display: flex;
      align-items: center;
      gap: 0.25rem;
      margin-left: 2.5rem;
    }
    .main-nav a {
      display: flex;
      align-items: center;
      gap: 0.4rem;
      color: rgba(255,255,255,0.7);
      text-decoration: none;
      padding: 0.5rem 0.9rem;
      border-radius: 8px;
      font-size: 0.85rem;
      font-weight: 500;
      transition: all 0.2s;
    }
    .main-nav a:hover { color: #fff; background: rgba(255,255,255,0.08); }
    .main-nav a.active {
      color: #fff;
      background: rgba(67, 97, 238, 0.4);
      box-shadow: inset 0 -2px 0 #60a5fa;
    }
    .nav-icon { width: 16px; height: 16px; flex-shrink: 0; }

    /* === User Menu === */
    .header-right {
      margin-left: auto;
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    .user-menu {
      display: flex;
      align-items: center;
      gap: 0.6rem;
    }
    .user-avatar {
      width: 34px;
      height: 34px;
      border-radius: 50%;
      background: linear-gradient(135deg, #60a5fa, #4361ee);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-weight: 700;
      font-size: 0.85rem;
    }
    .user-details { display: flex; flex-direction: column; line-height: 1.2; }
    .user-name { color: #fff; font-size: 0.82rem; font-weight: 500; }
    .user-role { color: rgba(255,255,255,0.5); font-size: 0.7rem; }
    .logout-btn {
      background: rgba(255,255,255,0.08);
      border: 1px solid rgba(255,255,255,0.15);
      border-radius: 8px;
      padding: 0.45rem;
      cursor: pointer;
      transition: all 0.2s;
      display: flex;
      align-items: center;
    }
    .logout-btn:hover { background: rgba(220, 53, 69, 0.2); border-color: rgba(220, 53, 69, 0.4); }
    .logout-icon { width: 18px; height: 18px; color: rgba(255,255,255,0.7); }
    .logout-btn:hover .logout-icon { color: #fca5a5; }

    /* === Main Content === */
    .main-content {
      min-height: calc(100vh - var(--header-height) - var(--footer-height));
      background: var(--bg);
    }

    /* === Footer === */
    .footer {
      background: var(--primary-dark);
      border-top: 1px solid rgba(255,255,255,0.05);
      height: var(--footer-height);
      display: flex;
      align-items: center;
    }
    .footer-inner {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      max-width: 1440px;
      margin: 0 auto;
      padding: 0 1.5rem;
    }
    .footer-left, .footer-center, .footer-right {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    .footer-brand { color: rgba(255,255,255,0.7); font-size: 0.75rem; font-weight: 600; }
    .footer-copy { color: rgba(255,255,255,0.4); font-size: 0.75rem; }
    .footer-sep { color: rgba(255,255,255,0.2); font-size: 0.75rem; }
    .footer-env { color: rgba(255,255,255,0.4); font-size: 0.72rem; background: rgba(255,255,255,0.05); padding: 0.15rem 0.5rem; border-radius: 4px; }
    .footer-version { color: rgba(96,165,250,0.7); font-size: 0.72rem; font-weight: 500; }
    .footer-link { color: rgba(255,255,255,0.45); font-size: 0.75rem; transition: color 0.2s; }
    .footer-link:hover { color: rgba(255,255,255,0.8); }
  `]
})
export class AppComponent {
  currentYear = new Date().getFullYear();
  constructor(public auth: AuthService) {}
}
