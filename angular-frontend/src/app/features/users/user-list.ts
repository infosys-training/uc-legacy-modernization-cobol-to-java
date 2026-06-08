import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { UserSecurity } from '../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="page">
      <div class="header">
        <h2>Users (Admin)</h2>
        <a routerLink="/users/new" class="btn btn-primary">+ Add User</a>
      </div>
      <table>
        <thead>
          <tr><th>User ID</th><th>First Name</th><th>Last Name</th><th>Type</th><th>Actions</th></tr>
        </thead>
        <tbody>
          @for (u of users(); track u.userId) {
            <tr>
              <td>{{ u.userId }}</td>
              <td>{{ u.firstName }}</td>
              <td>{{ u.lastName }}</td>
              <td><span [class]="u.userType === 'A' ? 'badge admin' : 'badge user'">{{ u.userType === 'A' ? 'Admin' : 'User' }}</span></td>
              <td>
                <a [routerLink]="['/users', u.userId, 'edit']" class="btn-sm">Edit</a>
                <button (click)="deleteUser(u.userId)" class="btn-sm btn-danger">Delete</button>
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
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .badge.admin { background: #fff3cd; color: #856404; }
    .badge.user { background: #d4edda; color: #155724; }
    .btn { display: inline-block; padding: 0.5rem 1rem; border-radius: 6px; text-decoration: none; font-size: 0.85rem; }
    .btn-primary { background: #4361ee; color: white; }
    .btn-sm { padding: 0.25rem 0.5rem; background: #f0f0f0; border-radius: 4px; font-size: 0.8rem; text-decoration: none; color: #333; border: none; cursor: pointer; margin-right: 0.25rem; }
    .btn-danger { background: #f8d7da; color: #721c24; }
  `]
})
export class UserListComponent implements OnInit {
  users = signal<UserSecurity[]>([]);

  constructor(private svc: UserService) {}
  ngOnInit(): void { this.load(); }

  load(): void {
    this.svc.list().subscribe(u => this.users.set(u));
  }

  deleteUser(userId: string): void {
    if (confirm(`Delete user ${userId}?`)) {
      this.svc.delete(userId).subscribe(() => this.load());
    }
  }
}
