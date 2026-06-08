import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TransactionTypeService } from '../../core/services/transaction-type.service';

@Component({
  selector: 'app-txn-type-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="page">
      <h2>{{ isEdit ? 'Edit Transaction Type ' + model.typeCode : 'New Transaction Type' }}</h2>
      <form (ngSubmit)="save()" class="form-card">
        @if (!isEdit) {
          <div class="form-group">
            <label>Type Code</label>
            <input type="text" [(ngModel)]="model.typeCode" name="typeCode" required maxlength="2" />
          </div>
        }
        <div class="form-group">
          <label>Description</label>
          <input type="text" [(ngModel)]="model.description" name="description" maxlength="100" />
        </div>
        @if (error()) { <div class="error">{{ error() }}</div> }
        <div class="actions">
          <button type="submit" class="btn btn-primary">{{ isEdit ? 'Update' : 'Create' }}</button>
          <a routerLink="/transaction-types" class="btn btn-cancel">Cancel</a>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; }
    .form-card { background: #fff; border: 1px solid #e0e0e0; border-radius: 8px; padding: 1.5rem; max-width: 400px; }
    .form-group { margin-bottom: 0.75rem; }
    label { display: block; margin-bottom: 0.2rem; font-weight: 600; font-size: 0.85rem; color: #555; }
    input { width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 6px; font-size: 0.9rem; box-sizing: border-box; }
    .actions { display: flex; gap: 0.5rem; margin-top: 1rem; }
    .btn { padding: 0.5rem 1.25rem; border: none; border-radius: 6px; font-size: 0.9rem; cursor: pointer; text-decoration: none; display: inline-block; }
    .btn-primary { background: #4361ee; color: white; }
    .btn-cancel { background: #e0e0e0; color: #333; }
    .error { background: #fee; color: #c00; padding: 0.5rem; border-radius: 6px; margin-bottom: 0.5rem; font-size: 0.85rem; }
  `]
})
export class TxnTypeFormComponent implements OnInit {
  model: any = { typeCode: '', description: '' };
  isEdit = false;
  error = signal('');

  constructor(private svc: TransactionTypeService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const code = this.route.snapshot.paramMap.get('typeCode');
    if (code) {
      this.isEdit = true;
      this.svc.get(code).subscribe(t => this.model = { ...t });
    }
  }

  save(): void {
    const obs = this.isEdit
      ? this.svc.update(this.model.typeCode, this.model)
      : this.svc.create(this.model);
    obs.subscribe({
      next: () => this.router.navigate(['/transaction-types']),
      error: (e) => this.error.set(e.error?.message || 'Save failed')
    });
  }
}
