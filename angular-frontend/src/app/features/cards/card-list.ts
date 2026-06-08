import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CardService } from '../../core/services/card.service';
import { Card } from '../../core/models/card.model';
import { PagerComponent } from '../../shared/components/data-table';

@Component({
  selector: 'app-card-list',
  standalone: true,
  imports: [RouterLink, PagerComponent],
  template: `
    <div class="page">
      <h2>Cards</h2>
      <table>
        <thead>
          <tr><th>Card Number</th><th>Account ID</th><th>Name on Card</th><th>Exp Date</th><th>Status</th><th>Actions</th></tr>
        </thead>
        <tbody>
          @for (c of cards(); track c.cardNumber) {
            <tr>
              <td><a [routerLink]="['/cards', c.cardNumber]">{{ c.cardNumber }}</a></td>
              <td><a [routerLink]="['/accounts', c.accountId]">{{ c.accountId }}</a></td>
              <td>{{ c.embossedName }}</td>
              <td>{{ c.expirationDate }}</td>
              <td><span [class]="c.activeStatus === 'Y' ? 'badge active' : 'badge inactive'">{{ c.activeStatus === 'Y' ? 'Active' : 'Inactive' }}</span></td>
              <td><a [routerLink]="['/cards', c.cardNumber, 'edit']" class="btn-sm">Edit</a></td>
            </tr>
          }
        </tbody>
      </table>
      <app-pager [page]="page()" [totalPages]="totalPages()" (pageChange)="loadPage($event)" />
    </div>
  `,
  styles: [`
    .page { padding: 1.5rem; }
    table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    th, td { padding: 0.6rem 0.75rem; text-align: left; border-bottom: 1px solid #eee; }
    th { background: #f8f9fa; font-weight: 600; font-size: 0.85rem; color: #555; }
    td a { color: #4361ee; text-decoration: none; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .active { background: #d4edda; color: #155724; }
    .inactive { background: #f8d7da; color: #721c24; }
    .btn-sm { padding: 0.25rem 0.5rem; background: #f0f0f0; border-radius: 4px; font-size: 0.8rem; text-decoration: none; color: #333; }
  `]
})
export class CardListComponent implements OnInit {
  cards = signal<Card[]>([]);
  page = signal(0);
  totalPages = signal(0);

  constructor(private svc: CardService) {}
  ngOnInit(): void { this.loadPage(0); }

  loadPage(p: number): void {
    this.svc.list(p).subscribe(res => {
      this.cards.set(res.content);
      this.page.set(res.number);
      this.totalPages.set(res.totalPages);
    });
  }
}
