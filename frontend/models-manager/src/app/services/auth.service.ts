import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authUrl = environment.apiUrl + '/auth';

  constructor(private http: HttpClient, private router: Router) {}

  // Method to log in and retrieve JWT
  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.authUrl}`, { "username": username, "password": password }).pipe(
      tap((response) => {
        this.setToken(response.Authorization);
        this.loadUserProfilePicture();
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An error occurred while logging in.';
        if (error.status === 0) {
          errorMessage = 'Unable to connect to the server. Please check your internet connection or try again later.';
        } else if (error.status === 401) {
          errorMessage = 'Invalid username or password.';
        } else if (error.status >= 500) {
          errorMessage = 'The server encountered an error. Please try again later.';
        }
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  loadUserProfilePicture() {
    this.http.get(environment.apiUrl + '/user/picture', { responseType: 'blob' }).subscribe(
      (blob: any) => {
        this.convertBlobToBase64(blob).then((base64Image) => {
          const base64Url = `data:image/png;base64,${base64Image.split(',')[1]}`;
          localStorage.setItem('profilePicture', base64Url);
        });
      }
    );
  }

  convertBlobToBase64(blob: Blob): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(blob);
      reader.onloadend = () => resolve(reader.result as string);
      reader.onerror = (error) => reject(error);
    });
  }

  // Store the JWT in local storage
  private setToken(token: string): void {
    localStorage.setItem('jwtToken', token);
    
  }

  // Get the JWT from local storage
  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  // Clear JWT from local storage on logout
  logout(): void {
    localStorage.removeItem('jwtToken');
    this.router.navigate(['/']);
  }

  // Check if the user is authenticated
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // get token expiration date UTC timestamp
  getTokenExpirationDate(token: string): number {
    return (JSON.parse(atob(token.split('.')[1]))).exp;
  }

  handleError = (operation = 'operation', result: any) => (error: any): Observable<any> => {
    console.error(`${operation} failed: ${error.message}`);
    return of(result);
  };
}
