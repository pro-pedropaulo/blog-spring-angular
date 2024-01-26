import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-login-popup',
  standalone: true,
  imports: [],
  templateUrl: './login-popup.component.html',
  styleUrl: './login-popup.component.scss'
})
export class LoginPopupComponent {

  constructor(public dialogRef: MatDialogRef<LoginPopupComponent>) {}

  ngOnInit() {
    setTimeout(() => this.dialogRef.close(), 2000);
  }

}
