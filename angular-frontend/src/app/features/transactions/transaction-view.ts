import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { TransactionService } from '../../core/services/transaction.service';
import { Transaction } from '../../core/models/transaction.model';

@Component({
  selector: 'app-transaction-view',
  standalone: true,
  imports: [RouterLink, CurrencyPipe],
  template: `
    @if (txn()) {
      <div class="page">
        <h2>Transaction {{ txn()!.transactionId }}</h2>
        <div class="detail-card">
          <dl>
            <dt>Type</dt><dd>{{ txn()!.typeCode }}</dd>
            <dt>Category</dt><dd>{{ txn()!.categoryCode }}</dd>
            <dt>Card</dt><dd><a [routerLink]="['/cards', txn()!.cardNumber]">{{ txn()!.cardNumber }}</a></dd>
            <dt>Amount</dt><dd>{{ txn()!.amount | currency }}</dd>
            <dt>Description</dt><dd>{{ txn()!.description }}</dd>
            <dt>Source</dt><dd>{{ txn()!.source }}</dd>
            <dt>Merchant</dt><dd>{{ txn()!.merchantName }}</dd>
            <dt>Merchant City</dt><dd>{{ txn()!.merchantCity }}</dd>
            <dt>Origin Time</dt><dd>{{ txn()!.originTimestamp }}</dd>
            <dt>Processed</dt><dd>{{ txn()!.processedTimestamp }}</dd>
          </dl>
        </div>
        <a routerLink="/transactions" class="back-link">&larr; Back to Transactions</a>
      </div>
    }
  `,
  styles: [`
    .page { padding: 1.5rem; }
    .detail-card { background: #fff; border: 1px solid #e0e0e0; border-radius: 8px; padding: 1.25rem; max-width: 500px; }
    dl { display: grid; grid-template-columns: 130px 1fr; gap: 0.3rem 0.5rem; margin: 0; }
    dt { font-weight: 600; font-size: 0.85rem; color: #666; }
    dd { margin: 0; }
    dd a { color: #4361ee; text-decoration: none; }
    .back-link { display: inline-block; margin-top: 1rem; color: #4361ee; text-decoration: none; font-size: 0.85rem; }
  `]
})
export class TransactionViewComponent implements OnInit {
  txn = signal<Transaction | null>(null);
  constructor(private svc: TransactionService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.svc.get(id).subscribe(t => this.txn.set(t));
  }
}
