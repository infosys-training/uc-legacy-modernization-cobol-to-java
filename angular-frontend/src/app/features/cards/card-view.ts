import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CardService } from '../../core/services/card.service';
import { Card } from '../../core/models/card.model';

@Component({
  selector: 'app-card-view',
  standalone: true,
  imports: [RouterLink],
  template: `
    @if (card()) {
      <div class="page">
        <div class="header">
          <h2>Card {{ card()!.cardNumber }}</h2>
          <a [routerLink]="['/cards', card()!.cardNumber, 'edit']" class="btn btn-primary">Edit</a>
        </div>
        <div class="detail-card">
          <dl>
            <dt>Account</dt><dd><a [routerLink]="['/accounts', card()!.accountId]">{{ card()!.accountId }}</a></dd>
            <dt>Name on Card</dt><dd>{{ card()!.embossedName }}</dd>
            <dt>CVV</dt><dd>{{ card()!.cvvCode }}</dd>
            <dt>Expiration</dt><dd>{{ card()!.expirationDate }}</dd>
            <dt>Status</dt><dd><span [class]="card()!.activeStatus === 'Y' ? 'badge active' : 'badge inactive'">{{ card()!.activeStatus === 'Y' ? 'Active' : 'Inactive' }}</span></dd>
          </dl>
        </div>
        <a routerLink="/cards" class="back-link">&larr; Back to Cards</a>
      </div>
    }
  `,
  styles: [`
    .page { padding: 1.5rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .detail-card { background: #fff; border: 1px solid #e0e0e0; border-radius: 8px; padding: 1.25rem; max-width: 500px; }
    dl { display: grid; grid-template-columns: 140px 1fr; gap: 0.3rem 0.5rem; margin: 0; }
    dt { font-weight: 600; font-size: 0.85rem; color: #666; }
    dd { margin: 0; }
    dd a { color: #4361ee; text-decoration: none; }
    .badge { padding: 0.15rem 0.4rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .active { background: #d4edda; color: #155724; }
    .inactive { background: #f8d7da; color: #721c24; }
    .btn { padding: 0.5rem 1rem; border-radius: 6px; text-decoration: none; font-size: 0.85rem; }
    .btn-primary { background: #4361ee; color: white; }
    .back-link { display: inline-block; margin-top: 1rem; color: #4361ee; text-decoration: none; font-size: 0.85rem; }
  `]
})
export class CardViewComponent implements OnInit {
  card = signal<Card | null>(null);
  constructor(private svc: CardService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const num = this.route.snapshot.paramMap.get('cardNumber')!;
    this.svc.get(num).subscribe(c => this.card.set(c));
  }
}
