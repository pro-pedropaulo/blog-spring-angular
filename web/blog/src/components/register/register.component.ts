import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';
import { UserService } from '../../services/users/user-service.service';
import { User } from '../../model/user.model';
import { HttpClientModule } from '@angular/common/http';
import { UserCreatedModalComponent } from '../../modals/user-created-modal/user-created-modal.component';
import { MatDialog } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import {MatIconModule} from '@angular/material/icon';


@Component({
  selector: 'app-register',
  standalone: true,
  imports: [MatCardModule, MatInputModule, MatButtonModule, ReactiveFormsModule, HttpClientModule, UserCreatedModalComponent, MatSelectModule, CommonModule, MatIconModule ],
  providers: [UserService],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})

export class RegisterComponent {
  registerForm!: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true; 

  constructor(private fb: FormBuilder,
     private router: Router,
      private userService: UserService,
      public dialog: MatDialog
     ) { }

     ngOnInit(): void {
      this.registerForm = this.fb.group({
        username: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.pattern(/^(?=.*\d)(?=.*[A-Z]).{8,}$/)]],
        confirmPassword: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]],
        gender: ['', [Validators.required]]
      }, { validator: this.passwordMatchValidator });
  
      this.registerForm.get('confirmPassword')!.valueChanges.subscribe(() => {
        this.checkPasswordMatch();
      });
    }
    
    checkPasswordMatch() {
      const password = this.registerForm.get('password')!.value;
      const confirmPassword = this.registerForm.get('confirmPassword')!.value;
    
      if (password !== confirmPassword) {
        this.registerForm.get('confirmPassword')!.setErrors({ mismatch: true });
      } else {
        this.registerForm.get('confirmPassword')!.setErrors(null);
      }
    }
    
    passwordMatchValidator(g: FormGroup) {
      return g.get('password')?.value === g.get('confirmPassword')?.value
        ? null : { 'mismatch': true };
    }

  onSubmit() {
    if (this.registerForm.valid) {
      const newUser = new User(
        this.registerForm.value.username,
        this.registerForm.value.password,
        this.registerForm.value.email,
        this.registerForm.value.gender
      );
  
      this.userService.createUser(newUser).subscribe({
        next: (user) => {
          this.dialog.open(UserCreatedModalComponent);
        },
        error: (error) => {
          alert('Erro ao criar usuário: ' + error.error);
        }        
      });
    }
  }

  navigateBack() {
    this.router.navigate(['/']); 
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  toggleConfirmPasswordVisibility() {
    this.hideConfirmPassword = !this.hideConfirmPassword;
  }

}
