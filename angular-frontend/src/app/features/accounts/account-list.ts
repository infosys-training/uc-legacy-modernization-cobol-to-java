import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { AccountService } from '../../core/services/account.service';
import { Account } from '../../core/models/account.model';
import { PagerComponent } from '../../shared/components/data-table';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, PagerComponent],
  template: `
    <div class="page">
      <div class="header">
        <h2>Accounts</h2>
        <a routerLink="/accounts/new" class="btn btn-primary">+ New Account</a>
      </div>
      <table>
        <thead>
          <tr>
            <th>Account ID</th>
            <th>Status</th>
            <th>Balance</th>
            <th>Credit Limit</th>
            <th>Open Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          @for (acct of accounts(); track acct.acctId) {
            <tr>
              <td><a [routerLink]="['/accounts', acct.acctId]">{{ acct.acctId }}</a></td>
              <td><span [class]="acct.activeStatus === 'Y' ? 'badge active' : 'badge inactive'">{{ acct.activeStatus === 'Y' ? 'Active' : 'Inactive' }}</span></td>
              <td class="num">{{ acct.currentBalance | currency }}</td>
              <td class="num">{{ acct.creditLimit | currency }}</td>
              <td>{{ acct.openDate }}</td>
              <td><a [routerLink]="['/accounts', acct.acctId, 'edit']" class="btn-sm">Edit</a></td>
            </tr>
          }
        </tbody>
      </table>
      <app-pager [page]="page()" [totalPages]="totalPages()" (pageChange)="loadPage($event)" />
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    h2 { margin: 0; }
    table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    th, td { padding: 0.6rem 0.75rem; text-align: left; border-bottom: 1px solid #eee; }
    th { background: #f8f9fa; font-weight: 600; font-size: 0.85rem; color: #555; }
    td a { color: #4361ee; text-decoration: none; }
    .num { text-align: right; font-variant-numeric: tabular-nums; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .active { background: #d4edda; color: #155724; }
    .inactive { background: #f8d7da; color: #721c24; }
    .btn { display: inline-block; padding: 0.5rem 1rem; border-radius: 6px; text-decoration: none; font-size: 0.85rem; }
    .btn-primary { background: #4361ee; color: white; }
    .btn-sm { padding: 0.25rem 0.5rem; background: #f0f0f0; border-radius: 4px; font-size: 0.8rem; text-decoration: none; color: #333; }
  `]
})
export class AccountListComponent implements OnInit {
  accounts = signal<Account[]>([]);
  page = signal(0);
  totalPages = signal(0);

  constructor(private svc: AccountService) {}

  ngOnInit(): void { this.loadPage(0); }

  loadPage(p: number): void {
    this.svc.list(p).subscribe(res => {
      this.accounts.set(res.content);
      this.page.set(res.number);
      this.totalPages.set(res.totalPages);
    });
  }
}
