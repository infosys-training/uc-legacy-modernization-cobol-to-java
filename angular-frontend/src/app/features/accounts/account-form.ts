import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../core/services/account.service';
import { Account } from '../../core/models/account.model';

@Component({
  selector: 'app-account-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="page">
      <h2>{{ isEdit ? 'Edit Account ' + model.acctId : 'New Account' }}</h2>
      <form (ngSubmit)="save()" class="form-card">
        @if (!isEdit) {
          <div class="form-group">
            <label>Account ID</label>
            <input type="number" [(ngModel)]="model.acctId" name="acctId" required />
          </div>
        }
        <div class="form-group">
          <label>Active Status</label>
          <select [(ngModel)]="model.activeStatus" name="activeStatus">
            <option value="Y">Active (Y)</option>
            <option value="N">Inactive (N)</option>
          </select>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>Credit Limit</label>
            <input type="number" step="0.01" [(ngModel)]="model.creditLimit" name="creditLimit" />
          </div>
          <div class="form-group">
            <label>Cash Credit Limit</label>
            <input type="number" step="0.01" [(ngModel)]="model.cashCreditLimit" name="cashCreditLimit" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>Expiration Date</label>
            <input type="date" [(ngModel)]="model.expirationDate" name="expirationDate" />
          </div>
          <div class="form-group">
            <label>Reissue Date</label>
            <input type="date" [(ngModel)]="model.reissueDate" name="reissueDate" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>ZIP Code</label>
            <input type="text" [(ngModel)]="model.addressZip" name="addressZip" maxlength="10" />
          </div>
          <div class="form-group">
            <label>Group ID</label>
            <input type="text" [(ngModel)]="model.groupId" name="groupId" maxlength="10" />
          </div>
        </div>
        @if (error()) { <div class="error">{{ error() }}</div> }
        <div class="actions">
          <button type="submit" class="btn btn-primary">{{ isEdit ? 'Update' : 'Create' }}</button>
          <a routerLink="/accounts" class="btn btn-cancel">Cancel</a>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; }
    .form-card { background: #fff; border: 1px solid #e0e0e0; border-radius: 8px; padding: 1.5rem; max-width: 600px; }
    .form-group { margin-bottom: 0.75rem; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }
    label { display: block; margin-bottom: 0.2rem; font-weight: 600; font-size: 0.85rem; color: #555; }
    input, select { width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 6px; font-size: 0.9rem; box-sizing: border-box; }
    .actions { display: flex; gap: 0.5rem; margin-top: 1rem; }
    .btn { padding: 0.5rem 1.25rem; border: none; border-radius: 6px; font-size: 0.9rem; cursor: pointer; text-decoration: none; display: inline-block; }
    .btn-primary { background: #4361ee; color: white; }
    .btn-cancel { background: #e0e0e0; color: #333; }
    .error { background: #fee; color: #c00; padding: 0.5rem; border-radius: 6px; margin-bottom: 0.5rem; font-size: 0.85rem; }
  `]
})
export class AccountFormComponent implements OnInit {
  model: any = { activeStatus: 'Y', creditLimit: 0, cashCreditLimit: 0 };
  isEdit = false;
  error = signal('');

  constructor(private svc: AccountService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.svc.get(Number(id)).subscribe(a => this.model = { ...a });
    }
  }

  save(): void {
    const obs = this.isEdit
      ? this.svc.update(this.model.acctId, this.model)
      : this.svc.create(this.model);
    obs.subscribe({
      next: () => this.router.navigate(['/accounts']),
      error: (e) => this.error.set(e.error?.message || 'Save failed')
    });
  }
}
