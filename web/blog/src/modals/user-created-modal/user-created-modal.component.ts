import { Component } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-user-created-modal',
  standalone: true,
  imports: [MatDialogModule],
  templateUrl: './user-created-modal.component.html',
  styleUrl: './user-created-modal.component.scss'
})
export class UserCreatedModalComponent {

  navigateToHome() {
    window.location.href = '/';
  }

}
