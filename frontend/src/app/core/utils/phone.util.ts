/**
 * Utilitários de telefone — formatação e limpeza.
 * Suporta celular (11 dígitos) e fixo (10 dígitos), ambos com DDD.
 */

/** Remove tudo que não for dígito. */
export function phoneOnlyDigits(value: string): string {
    return (value ?? '').replace(/\D/g, '');
}

/**
 * Aplica a máscara conforme o usuário digita:
 *   10 dígitos → (00) 0000-0000   (fixo)
 *   11 dígitos → (00) 00000-0000  (celular)
 */
export function phoneMask(value: string): string {
    const d = phoneOnlyDigits(value).slice(0, 11);
    if (d.length <= 2) return d.length ? `(${d}` : '';
    if (d.length <= 6) return `(${d.slice(0, 2)}) ${d.slice(2)}`;
    if (d.length <= 10) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`;
    // 11 dígitos — celular
    return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`;
}

/**
 * Formata um telefone já completo para exibição.
 */
export function phoneFormat(value: string): string {
    if (!value) return '';
    return phoneMask(value);
}
