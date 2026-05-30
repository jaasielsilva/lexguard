import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { getAccessDeniedMessage, isAccessDeniedError } from '../utils/api-error.util';
import { ToastService } from '../services/toast.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
    const toast = inject(ToastService);

    return next(req).pipe(
        catchError((err: unknown) => {
            if (err instanceof HttpErrorResponse && isAccessDeniedError(err)) {
                toast.accessDenied(getAccessDeniedMessage(err));
            }
            return throwError(() => err);
        }),
    );
};
