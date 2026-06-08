import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { AccountService } from '../../core/services/account.service';
import { AccountDetails } from '../../core/models/account.model';

@Component({
  selector: 'app-account-view',
  standalone: true,
  imports: [RouterLink, CurrencyPipe],
  template: `
    @if (details()) {
      <div class="page">
        <div class="header">
          <h2>Account {{ details()!.account.acctId }}</h2>
          <a [routerLink]="['/accounts', details()!.account.acctId, 'edit']" class="btn btn-primary">Edit</a>
        </div>

        <div class="card-grid">
          <div class="detail-card">
            <h3>Account Info</h3>
            <dl>
              <dt>Status</dt><dd><span [class]="details()!.account.activeStatus === 'Y' ? 'badge active' : 'badge inactive'">{{ details()!.account.activeStatus === 'Y' ? 'Active' : 'Inactive' }}</span></dd>
              <dt>Balance</dt><dd>{{ details()!.account.currentBalance | currency }}</dd>
              <dt>Credit Limit</dt><dd>{{ details()!.account.creditLimit | currency }}</dd>
              <dt>Cash Limit</dt><dd>{{ details()!.account.cashCreditLimit | currency }}</dd>
              <dt>Open Date</dt><dd>{{ details()!.account.openDate }}</dd>
              <dt>Expiration</dt><dd>{{ details()!.account.expirationDate }}</dd>
              <dt>Cycle Credit</dt><dd>{{ details()!.account.currentCycleCredit | currency }}</dd>
              <dt>Cycle Debit</dt><dd>{{ details()!.account.currentCycleDebit | currency }}</dd>
              <dt>ZIP</dt><dd>{{ details()!.account.addressZip }}</dd>
              <dt>Group</dt><dd>{{ details()!.account.groupId }}</dd>
            </dl>
          </div>

          @if (details()!.customer) {
            <div class="detail-card">
              <h3>Customer</h3>
              <dl>
                <dt>Name</dt><dd>{{ details()!.customer!.firstName }} {{ details()!.customer!.lastName }}</dd>
                <dt>SSN</dt><dd>{{ details()!.customer!.ssn }}</dd>
                <dt>FICO</dt><dd>{{ details()!.customer!.ficoCreditScore }}</dd>
                <dt>DOB</dt><dd>{{ details()!.customer!.dateOfBirth }}</dd>
                <dt>State</dt><dd>{{ details()!.customer!.addressStateCode }}</dd>
                <dt>Phone</dt><dd>{{ details()!.customer!.phoneNumber1 }}</dd>
              </dl>
            </div>
          }

          @if (details()!.cardXrefs.length) {
            <div class="detail-card">
              <h3>Linked Cards</h3>
              <table>
                <thead><tr><th>Card Number</th><th>Customer ID</th></tr></thead>
                <tbody>
                  @for (x of details()!.cardXrefs; track x.cardNumber) {
                    <tr>
                      <td><a [routerLink]="['/cards', x.cardNumber]">{{ x.cardNumber }}</a></td>
                      <td>{{ x.customerId }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          }
        </div>
        <a routerLink="/accounts" class="back-link">&larr; Back to Accounts</a>
      </div>
    }
  `,
  styles: [`
    .page { padding: 1.5rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .card-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1rem; }
    .detail-card { background: #fff; border: 1px solid #e0e0e0; border-radius: 8px; padding: 1.25rem; }
    h3 { margin: 0 0 0.75rem; font-size: 1rem; color: #333; }
    dl { display: grid; grid-template-columns: 120px 1fr; gap: 0.3rem 0.5rem; margin: 0; }
    dt { font-weight: 600; font-size: 0.85rem; color: #666; }
    dd { margin: 0; font-size: 0.9rem; }
    table { width: 100%; border-collapse: collapse; margin-top: 0.5rem; }
    th, td { padding: 0.4rem 0.5rem; text-align: left; border-bottom: 1px solid #eee; font-size: 0.85rem; }
    th { color: #666; }
    td a { color: #4361ee; text-decoration: none; }
    .badge { padding: 0.15rem 0.4rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .active { background: #d4edda; color: #155724; }
    .inactive { background: #f8d7da; color: #721c24; }
    .btn { padding: 0.5rem 1rem; border-radius: 6px; text-decoration: none; font-size: 0.85rem; }
    .btn-primary { background: #4361ee; color: white; }
    .back-link { display: inline-block; margin-top: 1rem; color: #4361ee; text-decoration: none; font-size: 0.85rem; }
  `]
})
export class AccountViewComponent implements OnInit {
  details = signal<AccountDetails | null>(null);

  constructor(private svc: AccountService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.svc.getDetails(id).subscribe(d => this.details.set(d));
  }
}
