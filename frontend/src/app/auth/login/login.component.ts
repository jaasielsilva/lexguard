import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { getApiErrorMessage } from '../../core/utils/api-error.util';
import { AuthService } from '../services/auth.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
    form: FormGroup;
    loading = false;
    errorMessage = '';
    showPassword = false;

    constructor(
        private fb: FormBuilder,
        private auth: AuthService,
        private router: Router
    ) {
        this.form = this.fb.group({
            username: ['', [Validators.required, Validators.minLength(3)]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            rememberMe: [false],
        });
    }

    get username() { return this.form.get('username')!; }
    get password() { return this.form.get('password')!; }

    togglePassword(): void {
        this.showPassword = !this.showPassword;
    }

    onSubmit(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        this.loading = true;
        this.errorMessage = '';

        const { username, password } = this.form.value;

        this.auth.login({ username, password }).subscribe({
            next: () => {
                this.router.navigate(['/dashboard']);
            },
            error: (err) => {
                this.loading = false;
                if (err.status === 401) {
                    this.errorMessage = 'Usuário ou senha inválidos.';
                } else if (err.status === 0) {
                    this.errorMessage = 'Não foi possível conectar ao servidor. Verifique sua conexão.';
                } else {
                    this.errorMessage = getApiErrorMessage(err, 'Ocorreu um erro inesperado. Tente novamente.');
                }
            },
        });
    }
}
