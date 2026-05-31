/**
 * Utilitários de CPF — formatação, limpeza e validação.
 */

/** Remove tudo que não for dígito. */
export function cpfOnlyDigits(value: string): string {
    return (value ?? '').replace(/\D/g, '');
}

/**
 * Aplica a máscara 000.000.000-00 conforme o usuário digita.
 */
export function cpfMask(value: string): string {
    const d = cpfOnlyDigits(value).slice(0, 11);
    if (d.length <= 3) return d;
    if (d.length <= 6) return `${d.slice(0, 3)}.${d.slice(3)}`;
    if (d.length <= 9) return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6)}`;
    return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}`;
}

/**
 * Formata um CPF já completo (11 dígitos ou formatado) para exibição.
 */
export function cpfFormat(value: string): string {
    const d = cpfOnlyDigits(value);
    if (d.length !== 11) return value;
    return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}`;
}

/**
 * Valida o CPF pelo algoritmo oficial (dígitos verificadores).
 * Aceita com ou sem formatação.
 */
export function cpfValid(value: string): boolean {
    const d = cpfOnlyDigits(value);
    if (d.length !== 11) return false;
    if (/^(\d)\1+$/.test(d)) return false; // todos iguais ex: 111.111.111-11

    const calc = (digits: string, factor: number) =>
        digits.split('').reduce((sum, n, i) => sum + Number(n) * (factor - i), 0);

    const r1 = calc(d.slice(0, 9), 10) % 11;
    const d1 = r1 < 2 ? 0 : 11 - r1;
    if (Number(d[9]) !== d1) return false;

    const r2 = calc(d.slice(0, 10), 11) % 11;
    const d2 = r2 < 2 ? 0 : 11 - r2;
    return Number(d[10]) === d2;
}
