import { ApiErrorBody } from '../models/api-error.model';

export function getApiErrorMessages(err: unknown, fallback: string): string[] {
    const body = (err as { error?: ApiErrorBody })?.error;
    if (!body) return [fallback];

    if (Array.isArray(body.messages) && body.messages.length > 0) {
        return body.messages;
    }
    if (body.message) return [body.message];
    if (body.error) return [body.error];

    return [fallback];
}

export function getApiErrorMessage(err: unknown, fallback: string): string {
    return getApiErrorMessages(err, fallback).join(' • ');
}
