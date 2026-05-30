export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface ToastMessage {
    id: number;
    title: string;
    message: string;
    type: ToastType;
}
