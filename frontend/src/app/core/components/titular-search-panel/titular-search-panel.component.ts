import {
    Component,
    ElementRef,
    EventEmitter,
    HostListener,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
    Subject,
    Subscription,
    catchError,
    debounceTime,
    distinctUntilChanged,
    of,
    switchMap,
    tap,
} from 'rxjs';
import { TitularResponse, TitularSearchPage } from '../../../modules/titulares/models/titular.model';
import { TitularesService } from '../../../modules/titulares/services/titulares.service';

@Component({
    selector: 'app-titular-search-panel',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './titular-search-panel.component.html',
    styleUrls: ['./titular-search-panel.component.scss'],
})
export class TitularSearchPanelComponent implements OnInit, OnDestroy {
    @Input() label = 'Localizar cliente / titular';
    /** Layout compacto dentro de um painel de filtros (sem label em caixa alta). */
    @Input() embedded = false;
    @Input() inputId = `titular-search-${++TitularSearchPanelComponent.instanceSeq}`;
    @Output() titularChange = new EventEmitter<TitularResponse | null>();

    private static instanceSeq = 0;
    @ViewChild('searchInput') searchInput?: ElementRef<HTMLInputElement>;

    query = '';
    results: TitularResponse[] = [];
    selected: TitularResponse | null = null;
    panelOpen = false;
    searching = false;
    searchError = '';
    totalElements = 0;
    currentPage = 0;
    hasMore = false;
    private readonly pageSize = 15;
    private readonly minChars = 2;

    private readonly query$ = new Subject<string>();
    private sub?: Subscription;

    constructor(private titularesService: TitularesService) { }

    ngOnInit(): void {
        this.sub = this.query$.pipe(
            debounceTime(350),
            distinctUntilChanged(),
            tap(() => {
                this.searchError = '';
                this.currentPage = 0;
            }),
            switchMap(q => {
                const term = q.trim();
                if (term.length < this.minChars) {
                    this.searching = false;
                    this.results = [];
                    this.totalElements = 0;
                    this.hasMore = false;
                    return of(null);
                }
                this.searching = true;
                return this.titularesService.search(term, 0, this.pageSize).pipe(
                    catchError(() => {
                        this.searchError = 'Nao foi possivel buscar titulares.';
                        this.searching = false;
                        return of(null);
                    }),
                );
            }),
        ).subscribe(page => this.applyPage(page, false));
    }

    ngOnDestroy(): void {
        this.sub?.unsubscribe();
    }

    onQueryChange(): void {
        if (this.selected) {
            return;
        }
        this.panelOpen = true;
        this.query$.next(this.query);
    }

    onFocus(): void {
        if (!this.selected) {
            this.panelOpen = true;
        }
    }

    loadMore(): void {
        const term = this.query.trim();
        if (term.length < this.minChars || !this.hasMore || this.searching) {
            return;
        }
        const nextPage = this.currentPage + 1;
        this.searching = true;
        this.titularesService.search(term, nextPage, this.pageSize).subscribe({
            next: page => this.applyPage(page, true),
            error: () => {
                this.searchError = 'Erro ao carregar mais resultados.';
                this.searching = false;
            },
        });
    }

    selectTitular(t: TitularResponse): void {
        this.selected = t;
        this.panelOpen = false;
        this.query = '';
        this.results = [];
        this.titularChange.emit(t);
    }

    clearSelection(): void {
        this.selected = null;
        this.query = '';
        this.results = [];
        this.panelOpen = false;
        this.titularChange.emit(null);
        setTimeout(() => this.searchInput?.nativeElement.focus(), 0);
    }

    /** Seleção programática (ex.: pré-preencher formulário ou limpar filtro). */
    applySelection(titular: TitularResponse | null): void {
        if (titular) {
            this.selectTitular(titular);
        } else {
            this.clearSelection();
        }
    }

    @HostListener('document:click', ['$event'])
    onDocumentClick(event: MouseEvent): void {
        const target = event.target as HTMLElement;
        if (!target.closest('.titular-search-panel')) {
            this.panelOpen = false;
        }
    }

    private applyPage(page: TitularSearchPage | null, append: boolean): void {
        this.searching = false;
        if (!page) {
            return;
        }
        this.currentPage = page.page;
        this.totalElements = page.totalElements;
        this.hasMore = page.hasMore;
        this.results = append ? [...this.results, ...page.items] : [...page.items];

        if (!append && page.items.length === 1 && this.query.trim().length >= this.minChars) {
            this.selectTitular(page.items[0]);
        }
    }
}
