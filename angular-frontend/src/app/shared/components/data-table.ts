import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Page } from '../../core/models/page.model';

@Component({
  selector: 'app-pager',
  standalone: true,
  template: `
    <div class="pager">
      <button (click)="prev()" [disabled]="page === 0">&laquo; Previous</button>
      <span>Page {{ page + 1 }} of {{ totalPages || 1 }}</span>
      <button (click)="next()" [disabled]="page >= totalPages - 1">Next &raquo;</button>
    </div>
  `,
  styles: [`
    .pager { display: flex; align-items: center; gap: 1rem; justify-content: center; margin: 1rem 0; }
    button { padding: 0.4rem 0.75rem; border: 1px solid #ddd; border-radius: 4px; background: #fff; cursor: pointer; }
    button:hover:not(:disabled) { background: #f0f0f0; }
    button:disabled { opacity: 0.4; cursor: not-allowed; }
    span { font-size: 0.85rem; color: #666; }
  `]
})
export class PagerComponent {
  @Input() page = 0;
  @Input() totalPages = 0;
  @Output() pageChange = new EventEmitter<number>();

  prev(): void { this.pageChange.emit(this.page - 1); }
  next(): void { this.pageChange.emit(this.page + 1); }
}
