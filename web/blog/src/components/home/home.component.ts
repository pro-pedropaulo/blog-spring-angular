import { Component } from '@angular/core';
import { HeaderComponent } from '../header/header.component';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeaderComponent, CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  constructor(private router: Router) { }


  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  navigateToAddPost() {
    this.router.navigate(['/add-post']);
  }
}
