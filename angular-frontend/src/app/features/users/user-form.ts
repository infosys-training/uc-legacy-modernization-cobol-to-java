import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="page">
      <h2>{{ isEdit ? 'Edit User ' + model.userId : 'New User' }}</h2>
      <form (ngSubmit)="save()" class="form-card">
        @if (!isEdit) {
          <div class="form-group">
            <label>User ID</label>
            <input type="text" [(ngModel)]="model.userId" name="userId" required maxlength="8" />
          </div>
        }
        <div class="form-row">
          <div class="form-group">
            <label>First Name</label>
            <input type="text" [(ngModel)]="model.firstName" name="firstName" maxlength="20" />
          </div>
          <div class="form-group">
            <label>Last Name</label>
            <input type="text" [(ngModel)]="model.lastName" name="lastName" maxlength="20" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>Password</label>
            <input type="password" [(ngModel)]="model.password" name="password" [required]="!isEdit" />
          </div>
          <div class="form-group">
            <label>User Type</label>
            <select [(ngModel)]="model.userType" name="userType" required>
              <option value="U">User (U)</option>
              <option value="A">Admin (A)</option>
            </select>
          </div>
        </div>
        @if (error()) { <div class="error">{{ error() }}</div> }
        <div class="actions">
          <button type="submit" class="btn btn-primary">{{ isEdit ? 'Update' : 'Create' }}</button>
          <a routerLink="/users" class="btn btn-cancel">Cancel</a>
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
export class UserFormComponent implements OnInit {
  model: any = { userType: 'U' };
  isEdit = false;
  error = signal('');

  constructor(private svc: UserService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('userId');
    if (id) {
      this.isEdit = true;
      this.svc.get(id).subscribe(u => this.model = { ...u, password: '' });
    }
  }

  save(): void {
    const obs = this.isEdit
      ? this.svc.update(this.model.userId, this.model)
      : this.svc.create(this.model);
    obs.subscribe({
      next: () => this.router.navigate(['/users']),
      error: (e) => this.error.set(e.error?.message || 'Save failed')
    });
  }
}
