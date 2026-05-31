/**
 * Utilitários de CNPJ — formatação, limpeza e validação.
 */

/** Remove tudo que não for dígito. */
export function cnpjOnlyDigits(value: string): string {
    return (value ?? '').replace(/\D/g, '');
}

/**
 * Aplica a máscara XX.XXX.XXX/XXXX-XX conforme o usuário digita.
 * Aceita a string parcial e retorna ela formatada até onde foi digitado.
 */
export function cnpjMask(value: string): string {
    const d = cnpjOnlyDigits(value).slice(0, 14);
    if (d.length <= 2) return d;
    if (d.length <= 5) return `${d.slice(0, 2)}.${d.slice(2)}`;
    if (d.length <= 8) return `${d.slice(0, 2)}.${d.slice(2, 5)}.${d.slice(5)}`;
    if (d.length <= 12) return `${d.slice(0, 2)}.${d.slice(2, 5)}.${d.slice(5, 8)}/${d.slice(8)}`;
    return `${d.slice(0, 2)}.${d.slice(2, 5)}.${d.slice(5, 8)}/${d.slice(8, 12)}-${d.slice(12)}`;
}

/**
 * Formata um CNPJ já completo (14 dígitos ou formatado) para exibição.
 * Retorna o valor original se não tiver 14 dígitos.
 */
export function cnpjFormat(value: string): string {
    const d = cnpjOnlyDigits(value);
    if (d.length !== 14) return value;
    return `${d.slice(0, 2)}.${d.slice(2, 5)}.${d.slice(5, 8)}/${d.slice(8, 12)}-${d.slice(12)}`;
}

/**
 * Valida o CNPJ pelo algoritmo oficial (dígitos verificadores).
 * Aceita com ou sem formatação.
 */
export function cnpjValid(value: string): boolean {
    const d = cnpjOnlyDigits(value);
    if (d.length !== 14) return false;
    if (/^(\d)\1+$/.test(d)) return false; // todos iguais

    const calc = (digits: string, weights: number[]) =>
        digits.split('').reduce((sum, n, i) => sum + Number(n) * weights[i], 0);

    const w1 = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
    const r1 = calc(d.slice(0, 12), w1) % 11;
    const d1 = r1 < 2 ? 0 : 11 - r1;
    if (Number(d[12]) !== d1) return false;

    const w2 = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
    const r2 = calc(d.slice(0, 13), w2) % 11;
    const d2 = r2 < 2 ? 0 : 11 - r2;
    return Number(d[13]) === d2;
}
