import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorBody } from '../models/api-error.model';

const ACCESS_DENIED_FALLBACK =
    'Voce nao tem permissao para esta acao. Fale com o administrador se precisar de acesso.';

export function isAccessDeniedError(err: unknown): boolean {
    if (!(err instanceof HttpErrorResponse)) {
        return false;
    }
    if (err.status === 403) {
        return true;
    }
    const text = extractErrorText(err);
    return /access denied|forbidden|acesso negado/i.test(text);
}

export function getAccessDeniedMessage(err: unknown): string {
    if (!(err instanceof HttpErrorResponse)) {
        return ACCESS_DENIED_FALLBACK;
    }
    const messages = getApiErrorMessages(err, ACCESS_DENIED_FALLBACK);
    const joined = messages.join(' • ');
    if (/access denied|forbidden/i.test(joined) && joined.length < 80) {
        return ACCESS_DENIED_FALLBACK;
    }
    return joined;
}

export function getApiErrorMessages(err: unknown, fallback: string): string[] {
    const httpErr = err as HttpErrorResponse;
    const body = httpErr?.error;

    if (typeof body === 'string' && body.trim()) {
        return [body.trim()];
    }

    if (!body || typeof body !== 'object') {
        return [fallback];
    }

    const apiBody = body as ApiErrorBody;
    if (Array.isArray(apiBody.messages) && apiBody.messages.length > 0) {
        return apiBody.messages;
    }
    if (apiBody.message) {
        return [apiBody.message];
    }
    if (apiBody.error) {
        return [apiBody.error];
    }

    return [fallback];
}

/** Retorna vazio em 403: o interceptor ja exibe o toast de acesso negado. */
export function getApiErrorMessage(err: unknown, fallback: string): string {
    if (isAccessDeniedError(err)) {
        return '';
    }
    return getApiErrorMessages(err, fallback).join(' • ');
}

function extractErrorText(err: HttpErrorResponse): string {
    if (typeof err.error === 'string') {
        return err.error;
    }
    if (err.error && typeof err.error === 'object') {
        const body = err.error as ApiErrorBody;
        return [body.message, body.error, ...(body.messages ?? [])].filter(Boolean).join(' ');
    }
    return err.message ?? '';
}
