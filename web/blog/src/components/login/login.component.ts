import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth-service.service';
import { HttpClientModule } from '@angular/common/http';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [MatCardModule, MatInputModule, MatButtonModule, ReactiveFormsModule, HttpClientModule],
  providers: [AuthService],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm!: FormGroup;
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
    ) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const { username, password } = this.loginForm.value;
      this.authService.login(username, password).subscribe({
        next: (response) => {
          localStorage.setItem('token', response.token); // Armazenar o token recebido
          // Redirecionar para a página inicial ou painel de usuário
        },
        error: (error) => {
          console.error('Erro ao fazer login', error);
          // Tratar erros de login (usuário não encontrado, senha incorreta, etc.)
        }
      });
    }
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}
