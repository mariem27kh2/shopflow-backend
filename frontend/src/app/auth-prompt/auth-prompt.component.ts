import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth-prompt',
  templateUrl: './auth-prompt.component.html',
  styleUrls: ['./auth-prompt.component.css']
})
export class AuthPromptComponent {

  constructor(
    private dialogRef: MatDialogRef<AuthPromptComponent>,
    private router: Router
  ) {}

  goLogin() {
    this.dialogRef.close();
    this.router.navigate(['/login']);
  }

  goRegister() {
    this.dialogRef.close();
    this.router.navigate(['/register']);
  }

  close() {
    this.dialogRef.close();
  }
}
