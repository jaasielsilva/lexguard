import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { getApiErrorMessage } from '../../../../core/utils/api-error.util';
import { cpfFormat, cpfMask, cpfOnlyDigits, cpfValid } from '../../../../core/utils/cpf.util';
import { phoneMask, phoneOnlyDigits } from '../../../../core/utils/phone.util';
import { TitularesService } from '../../services/titulares.service';

function cpfValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value ?? '';
    if (!value) return null;
    return cpfValid(value) ? null : { cpfInvalido: true };
}

@Component({
    selector: 'app-titular-form',
    templateUrl: './titular-form.component.html',
    styleUrls: ['./titular-form.component.scss'],
})
export class TitularFormComponent implements OnInit {
    form: FormGroup;
    isEdit = false;
    id: number | null = null;
    loading = false;
    saving = false;
    error = '';

    constructor(
        private fb: FormBuilder,
        private service: TitularesService,
        private router: Router,
        private route: ActivatedRoute,
    ) {
        this.form = this.fb.group({
            nome: ['', [Validators.required, Validators.minLength(3)]],
            cpf: ['', [Validators.required, cpfValidator]],
            email: ['', [Validators.required, Validators.email]],
            telefone: [''],
            classificacao: ['PESSOAL', Validators.required],
        });
    }

    ngOnInit(): void {
        const idParam = this.route.snapshot.paramMap.get('id');
        if (idParam) {
            this.isEdit = true;
            this.id = Number(idParam);
            this.loading = true;
            this.service.getById(this.id).subscribe({
                next: t => {
                    this.form.patchValue({
                        ...t,
                        cpf: cpfFormat(t.cpf),
                        telefone: phoneMask(t.telefone ?? ''),
                    });
                    this.loading = false;
                },
                error: () => { this.error = 'Erro ao carregar titular.'; this.loading = false; },
            });
        }
    }

    // ── Máscaras ao digitar ───────────────────────────────────────────────────

    onCpfInput(event: Event): void {
        const input = event.target as HTMLInputElement;
        const masked = cpfMask(input.value);
        this.form.get('cpf')!.setValue(masked, { emitEvent: false });
        input.value = masked;
    }

    onTelefoneInput(event: Event): void {
        const input = event.target as HTMLInputElement;
        const masked = phoneMask(input.value);
        this.form.get('telefone')!.setValue(masked, { emitEvent: false });
        input.value = masked;
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    onSubmit(): void {
        if (this.form.invalid) { this.form.markAllAsTouched(); return; }
        this.saving = true;
        this.error = '';

        const raw = this.form.value;
        const payload = {
            ...raw,
            cpf: cpfOnlyDigits(raw.cpf),
            telefone: phoneOnlyDigits(raw.telefone ?? ''),
        };

        const req = this.isEdit
            ? this.service.update(this.id!, payload)
            : this.service.create(payload);

        req.subscribe({
            next: () => this.router.navigate(['/dashboard/titulares']),
            error: err => {
                this.saving = false;
                this.error = getApiErrorMessage(err, 'Erro ao salvar titular.');
            },
        });
    }

    get f() { return this.form.controls; }
}
