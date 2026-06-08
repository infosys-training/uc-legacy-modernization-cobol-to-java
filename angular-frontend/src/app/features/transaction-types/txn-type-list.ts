import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TransactionTypeService } from '../../core/services/transaction-type.service';
import { TransactionType } from '../../core/models/transaction-type.model';

@Component({
  selector: 'app-txn-type-list',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="page">
      <div class="header">
        <h2>Transaction Types (Admin)</h2>
        <a routerLink="/transaction-types/new" class="btn btn-primary">+ Add Type</a>
      </div>
      <table>
        <thead>
          <tr><th>Type Code</th><th>Description</th><th>Actions</th></tr>
        </thead>
        <tbody>
          @for (t of types(); track t.typeCode) {
            <tr>
              <td>{{ t.typeCode }}</td>
              <td>{{ t.description }}</td>
              <td>
                <a [routerLink]="['/transaction-types', t.typeCode, 'edit']" class="btn-sm">Edit</a>
                <button (click)="deleteType(t.typeCode)" class="btn-sm btn-danger">Delete</button>
              </td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    th, td { padding: 0.6rem 0.75rem; text-align: left; border-bottom: 1px solid #eee; }
    th { background: #f8f9fa; font-weight: 600; font-size: 0.85rem; color: #555; }
    .btn { display: inline-block; padding: 0.5rem 1rem; border-radius: 6px; text-decoration: none; font-size: 0.85rem; }
    .btn-primary { background: #4361ee; color: white; }
    .btn-sm { padding: 0.25rem 0.5rem; background: #f0f0f0; border-radius: 4px; font-size: 0.8rem; text-decoration: none; color: #333; border: none; cursor: pointer; margin-right: 0.25rem; }
    .btn-danger { background: #f8d7da; color: #721c24; }
  `]
})
export class TxnTypeListComponent implements OnInit {
  types = signal<TransactionType[]>([]);

  constructor(private svc: TransactionTypeService) {}
  ngOnInit(): void { this.load(); }

  load(): void { this.svc.list().subscribe(t => this.types.set(t)); }

  deleteType(code: string): void {
    if (confirm(`Delete transaction type ${code}?`)) {
      this.svc.delete(code).subscribe(() => this.load());
    }
  }
}
