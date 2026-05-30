import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ToastMessage, ToastType } from '../models/toast.model';

@Injectable({ providedIn: 'root' })
export class ToastService {
    private readonly toastsSubject = new BehaviorSubject<ToastMessage[]>([]);
    readonly toasts$ = this.toastsSubject.asObservable();
    private seq = 0;

    success(title: string, message: string, durationMs = 4500): void {
        this.show(title, message, 'success', durationMs);
    }

    error(title: string, message: string, durationMs = 6000): void {
        this.show(title, message, 'error', durationMs);
    }

    warning(title: string, message: string, durationMs = 5500): void {
        this.show(title, message, 'warning', durationMs);
    }

    accessDenied(message?: string): void {
        this.error(
            'Acesso negado',
            message ?? 'Voce nao tem permissao para esta acao. Fale com o administrador se precisar de acesso.',
        );
    }

    dismiss(id: number): void {
        this.toastsSubject.next(this.toastsSubject.value.filter(t => t.id !== id));
    }

    private show(title: string, message: string, type: ToastType, durationMs: number): void {
        const toast: ToastMessage = {
            id: ++this.seq,
            title,
            message,
            type,
        };
        this.toastsSubject.next([...this.toastsSubject.value, toast]);
        setTimeout(() => this.dismiss(toast.id), durationMs);
    }
}
