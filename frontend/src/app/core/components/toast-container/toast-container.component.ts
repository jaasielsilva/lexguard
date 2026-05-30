import { Component } from '@angular/core';
import { ToastMessage } from '../../models/toast.model';
import { ToastService } from '../../services/toast.service';

@Component({
    selector: 'app-toast-container',
    templateUrl: './toast-container.component.html',
    styleUrls: ['./toast-container.component.scss'],
})
export class ToastContainerComponent {
    toasts: ToastMessage[] = [];

    constructor(private toast: ToastService) {
        this.toast.toasts$.subscribe(list => {
            this.toasts = list;
        });
    }

    close(id: number): void {
        this.toast.dismiss(id);
    }

    icon(type: ToastMessage['type']): string {
        switch (type) {
            case 'success': return 'bi-check-circle-fill';
            case 'warning': return 'bi-exclamation-circle-fill';
            case 'info': return 'bi-info-circle-fill';
            default: return 'bi-shield-exclamation';
        }
    }
}
