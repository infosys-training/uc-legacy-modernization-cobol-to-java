import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CardService } from '../../core/services/card.service';

@Component({
  selector: 'app-card-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="page">
      <h2>Edit Card {{ model.cardNumber }}</h2>
      <form (ngSubmit)="save()" class="form-card">
        <div class="form-group">
          <label>Embossed Name</label>
          <input type="text" [(ngModel)]="model.embossedName" name="embossedName" maxlength="50" />
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>Expiration Date</label>
            <input type="date" [(ngModel)]="model.expirationDate" name="expirationDate" />
          </div>
          <div class="form-group">
            <label>Status</label>
            <select [(ngModel)]="model.activeStatus" name="activeStatus">
              <option value="Y">Active</option>
              <option value="N">Inactive</option>
            </select>
          </div>
        </div>
        @if (error()) { <div class="error">{{ error() }}</div> }
        <div class="actions">
          <button type="submit" class="btn btn-primary">Update</button>
          <a routerLink="/cards" class="btn btn-cancel">Cancel</a>
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
    input, select { width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 6px; font-size: 0.9rem; box-sizing: border-box; }
    .actions { display: flex; gap: 0.5rem; margin-top: 1rem; }
    .btn { padding: 0.5rem 1.25rem; border: none; border-radius: 6px; font-size: 0.9rem; cursor: pointer; text-decoration: none; display: inline-block; }
    .btn-primary { background: #4361ee; color: white; }
    .btn-cancel { background: #e0e0e0; color: #333; }
    .error { background: #fee; color: #c00; padding: 0.5rem; border-radius: 6px; margin-bottom: 0.5rem; font-size: 0.85rem; }
  `]
})
export class CardFormComponent implements OnInit {
  model: any = {};
  error = signal('');

  constructor(private svc: CardService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const num = this.route.snapshot.paramMap.get('cardNumber')!;
    this.svc.get(num).subscribe(c => this.model = { ...c });
  }

  save(): void {
    this.svc.update(this.model.cardNumber, this.model).subscribe({
      next: () => this.router.navigate(['/cards']),
      error: (e) => this.error.set(e.error?.message || 'Update failed')
    });
  }
}
