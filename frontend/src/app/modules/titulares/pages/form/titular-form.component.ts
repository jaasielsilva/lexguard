import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { getApiErrorMessage } from '../../../../core/utils/api-error.util';
import { TitularesService } from '../../services/titulares.service';

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
        private route: ActivatedRoute
    ) {
        this.form = this.fb.group({
            nome: ['', [Validators.required, Validators.minLength(3)]],
            cpf: ['', [Validators.required]],
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
                next: t => { this.form.patchValue(t); this.loading = false; },
                error: () => { this.error = 'Erro ao carregar titular.'; this.loading = false; },
            });
        }
    }

    onSubmit(): void {
        if (this.form.invalid) { this.form.markAllAsTouched(); return; }
        this.saving = true;
        this.error = '';
        const data = this.form.value;
        const req = this.isEdit
            ? this.service.update(this.id!, data)
            : this.service.create(data);
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
