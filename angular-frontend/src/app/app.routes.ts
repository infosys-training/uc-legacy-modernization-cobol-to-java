import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/auth/login').then(m => m.LoginComponent) },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard').then(m => m.DashboardComponent)
  },
  {
    path: 'accounts',
    canActivate: [authGuard],
    children: [
      { path: '', loadComponent: () => import('./features/accounts/account-list').then(m => m.AccountListComponent) },
      { path: 'new', loadComponent: () => import('./features/accounts/account-form').then(m => m.AccountFormComponent) },
      { path: ':id', loadComponent: () => import('./features/accounts/account-view').then(m => m.AccountViewComponent) },
      { path: ':id/edit', loadComponent: () => import('./features/accounts/account-form').then(m => m.AccountFormComponent) },
    ]
  },
  {
    path: 'cards',
    canActivate: [authGuard],
    children: [
      { path: '', loadComponent: () => import('./features/cards/card-list').then(m => m.CardListComponent) },
      { path: ':cardNumber', loadComponent: () => import('./features/cards/card-view').then(m => m.CardViewComponent) },
      { path: ':cardNumber/edit', loadComponent: () => import('./features/cards/card-form').then(m => m.CardFormComponent) },
    ]
  },
  {
    path: 'transactions',
    canActivate: [authGuard],
    children: [
      { path: '', loadComponent: () => import('./features/transactions/transaction-list').then(m => m.TransactionListComponent) },
      { path: 'new', loadComponent: () => import('./features/transactions/transaction-add').then(m => m.TransactionAddComponent) },
      { path: ':id', loadComponent: () => import('./features/transactions/transaction-view').then(m => m.TransactionViewComponent) },
    ]
  },
  {
    path: 'users',
    canActivate: [authGuard, adminGuard],
    children: [
      { path: '', loadComponent: () => import('./features/users/user-list').then(m => m.UserListComponent) },
      { path: 'new', loadComponent: () => import('./features/users/user-form').then(m => m.UserFormComponent) },
      { path: ':userId/edit', loadComponent: () => import('./features/users/user-form').then(m => m.UserFormComponent) },
    ]
  },
  {
    path: 'transaction-types',
    canActivate: [authGuard, adminGuard],
    children: [
      { path: '', loadComponent: () => import('./features/transaction-types/txn-type-list').then(m => m.TxnTypeListComponent) },
      { path: 'new', loadComponent: () => import('./features/transaction-types/txn-type-form').then(m => m.TxnTypeFormComponent) },
      { path: ':typeCode/edit', loadComponent: () => import('./features/transaction-types/txn-type-form').then(m => m.TxnTypeFormComponent) },
    ]
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' },
];
