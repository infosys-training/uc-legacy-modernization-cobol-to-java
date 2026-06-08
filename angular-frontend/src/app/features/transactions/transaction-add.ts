import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TransactionService } from '../../core/services/transaction.service';

@Component({
  selector: 'app-transaction-add',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="page">
      <h2>New Transaction</h2>
      <form (ngSubmit)="save()" class="form-card">
        <div class="form-group">
          <label>Card Number</label>
          <input type="text" [(ngModel)]="model.cardNumber" name="cardNumber" required maxlength="16" />
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>Type Code</label>
            <input type="text" [(ngModel)]="model.typeCode" name="typeCode" required maxlength="2" />
          </div>
          <div class="form-group">
            <label>Category Code</label>
            <input type="number" [(ngModel)]="model.categoryCode" name="categoryCode" />
          </div>
        </div>
        <div class="form-group">
          <label>Amount</label>
          <input type="number" step="0.01" [(ngModel)]="model.amount" name="amount" required />
        </div>
        <div class="form-group">
          <label>Source</label>
          <input type="text" [(ngModel)]="model.source" name="source" maxlength="10" placeholder="e.g. POS, ATM, ONLINE" />
        </div>
        <div class="form-group">
          <label>Description</label>
          <input type="text" [(ngModel)]="model.description" name="description" maxlength="100" />
        </div>
        @if (error()) { <div class="error">{{ error() }}</div> }
        <div class="actions">
          <button type="submit" class="btn btn-primary">Submit</button>
          <a routerLink="/transactions" class="btn btn-cancel">Cancel</a>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; }
    .form-card { background: #fff; border: 1px solid #e0e0e0; border-radius: 8px; padding: 1.5rem; max-width: 500px; }
    .form-group { margin-bottom: 0.75rem; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
    label { display: block; margin-bottom: 0.2rem; font-weight: 600; font-size: 0.85rem; color: #555; }
    input { width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 6px; font-size: 0.9rem; box-sizing: border-box; }
    .actions { display: flex; gap: 0.5rem; margin-top: 1rem; }
    .btn { padding: 0.5rem 1.25rem; border: none; border-radius: 6px; font-size: 0.9rem; cursor: pointer; text-decoration: none; display: inline-block; }
    .btn-primary { background: #4361ee; color: white; }
    .btn-cancel { background: #e0e0e0; color: #333; }
    .error { background: #fee; color: #c00; padding: 0.5rem; border-radius: 6px; margin-bottom: 0.5rem; font-size: 0.85rem; }
  `]
})
export class TransactionAddComponent {
  model: any = { typeCode: '', amount: 0, cardNumber: '' };
  error = signal('');

  constructor(private svc: TransactionService, private router: Router) {}

  save(): void {
    this.svc.create(this.model).subscribe({
      next: () => this.router.navigate(['/transactions']),
      error: (e) => this.error.set(e.error?.message || 'Transaction failed')
    });
  }
}
