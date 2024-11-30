import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-file-uploader',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule
  ],
  template: `
      <div class="file-input-wrapper">
        <button mat-raised-button color="primary" (click)="fileInput.click()">
          Select File
        </button>
        <span *ngIf="selectedFileName">{{ selectedFileName }}</span>
        <input #fileInput type="file" hidden (change)="onFileSelected($event)" />
      </div>
  `,
  styles: `
    .file-input-wrapper {
        margin: 16px 0;
    display: flex;
    align-items: center;
    gap: 10px;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileUploaderComponent {
    selectedFile: File | null = null;
    selectedFileName: string | null = null;
  
    http = inject(HttpClient);

    onFileSelected(event: Event): void {
        const target = event.target as HTMLInputElement;
        if (target.files && target.files.length > 0) {
          this.selectedFile = target.files[0];
    
          this.uploadFile(this.selectedFile).subscribe();
        } 
      }
    
      uploadFile(file: File): Observable<any> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post(environment.apiUrl + '/file', formData);
      }
}
