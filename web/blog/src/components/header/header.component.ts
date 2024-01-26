import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NavigationEnd, Router } from '@angular/router';
import { ConfirmDialogComponent } from '../../modals/confirm-dialog/confirm-dialog.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatDialogModule} from '@angular/material/dialog';
import {MatToolbarModule} from '@angular/material/toolbar';
import { AuthService } from '../../services/auth/auth-service.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, ConfirmDialogComponent, MatDialogModule, MatIconModule, MatButtonModule, MatMenuModule, MatToolbarModule],
  providers: [AuthService],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

  constructor(
    private authService: AuthService, 
    public dialog: MatDialog, 
    private router: Router,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.checkIfHomePage(event.url);
      }
    });
    this.checkTokenExpiry();
  }

  isHomePage: boolean = true;

  checkIfHomePage(url: string) {
    this.isHomePage = url === '/';
    this.changeDetectorRef.detectChanges();
  }

  navigateToHome() {
    this.router.navigate(['/']);
  }

  isLoggedIn(): boolean {
    if (typeof window !== 'undefined') {
      return !!localStorage.getItem('token');
    }
    return false;
  }
  

  checkTokenExpiry() {
    if (this.authService.isTokenExpired()) {
      this.authService.logout();
    }
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  openConfirmLogoutDialog() {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '250px',
      data: { message: "Tem certeza que deseja sair?" } 
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.logout();
      }
    });
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['']);
    this.changeDetectorRef.detectChanges(); 
  }
}
