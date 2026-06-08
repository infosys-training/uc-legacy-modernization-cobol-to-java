import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { TransactionService } from '../../core/services/transaction.service';
import { Transaction } from '../../core/models/transaction.model';
import { PagerComponent } from '../../shared/components/data-table';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, PagerComponent],
  template: `
    <div class="page">
      <div class="header">
        <h2>Transactions</h2>
        <a routerLink="/transactions/new" class="btn btn-primary">+ New Transaction</a>
      </div>
      <table>
        <thead>
          <tr><th>ID</th><th>Type</th><th>Card</th><th>Amount</th><th>Description</th><th>Timestamp</th></tr>
        </thead>
        <tbody>
          @for (t of txns(); track t.transactionId) {
            <tr>
              <td><a [routerLink]="['/transactions', t.transactionId]">{{ t.transactionId }}</a></td>
              <td>{{ t.typeCode }}</td>
              <td><a [routerLink]="['/cards', t.cardNumber]">{{ t.cardNumber }}</a></td>
              <td class="num">{{ t.amount | currency }}</td>
              <td>{{ t.description }}</td>
              <td>{{ t.originTimestamp }}</td>
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
    table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    th, td { padding: 0.6rem 0.75rem; text-align: left; border-bottom: 1px solid #eee; }
    th { background: #f8f9fa; font-weight: 600; font-size: 0.85rem; color: #555; }
    td a { color: #4361ee; text-decoration: none; }
    .num { text-align: right; font-variant-numeric: tabular-nums; }
    .btn { display: inline-block; padding: 0.5rem 1rem; border-radius: 6px; text-decoration: none; font-size: 0.85rem; }
    .btn-primary { background: #4361ee; color: white; }
  `]
})
export class TransactionListComponent implements OnInit {
  txns = signal<Transaction[]>([]);
  page = signal(0);
  totalPages = signal(0);

  constructor(private svc: TransactionService) {}
  ngOnInit(): void { this.loadPage(0); }

  loadPage(p: number): void {
    this.svc.list(p).subscribe(res => {
      this.txns.set(res.content);
      this.page.set(res.number);
      this.totalPages.set(res.totalPages);
    });
  }
}
