import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service'; // Adjust path as needed
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatInputModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent implements OnInit {
  http = inject(HttpClient);
  profileForm: FormGroup;
  loading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  pictureUrl = './assets/pp.jpg';

  constructor(private fb: FormBuilder, private authService: AuthService, private sanitizer: DomSanitizer) {
    this.profileForm = this.fb.group({
      firstname: [{value: '', disabled: true}, Validators.required],
      lastname: [{value: '', disabled: true}, Validators.required],
      role: [{value: '', disabled: true}, Validators.required],
      email: [{value: '', disabled: true}, [Validators.required, Validators.email]],
      password: [{value: ''}, Validators.minLength(6)],
    });
  }

  ngOnInit(): void {

    this.getUserInfo().subscribe(
      (user: any) => {
        this.profileForm.patchValue({
          firstname: user.firstname,
          lastname: user.lastname,
          email: user.email,
          role: user.role
      });
      }
    );

    this.http.get(environment.apiUrl + '/user/picture', { responseType: 'blob' }).subscribe(
      (blob: any) => {
        this.pictureUrl = URL.createObjectURL(blob);
      }
    );
  }

  getUserInfo(): Observable<any> {
    return this.http.get(environment.apiUrl + '/user/current');
  } 

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const data = new FormData();
      data.append("file", target.files[0]);
      this.http.post(environment.apiUrl + '/user/picture', data).subscribe();
    } 
  }

  triggerFileInput() {
    const fileInput = document.getElementById('profile-upload') as HTMLElement;
    fileInput.click();
  }

  onSubmit() {
    // if (this.profileForm.valid) {
    //   this.loading = true;
    //   this.authService.updateUserProfile(this.profileForm.value).subscribe(
    //     () => {
    //       this.loading = false;
    //       this.successMessage = 'Profile updated successfully';
    //     },
    //     (error) => {
    //       this.loading = false;
    //       this.errorMessage = 'Failed to update profile';
    //       console.error('Error updating profile', error);
    //     }
    //   );
    // }
  }
}
