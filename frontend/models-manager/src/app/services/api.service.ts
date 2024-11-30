import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

    // Common GET request
    get<T>(endpoint: string, params?: HttpParams): Observable<T> {
      return this.http.get<T>(`${this.baseUrl}/${endpoint}`, { params })
        .pipe(catchError(this.handleError));
    }
     // Common GET request
     getBlob(endpoint: string): Observable<Blob> {
      return this.http.get(`${this.baseUrl}/${endpoint}`, { responseType: 'blob' })
        .pipe(catchError(this.handleError));
    }
  
    // Common POST request
    post<T>(endpoint: string, body: any, headers?: HttpHeaders): Observable<T> {
      return this.http.post<T>(`${this.baseUrl}/${endpoint}`, body, { headers })
        .pipe(catchError(this.handleError));
    }
  
    // Common PUT request
    put<T>(endpoint: string, body: any): Observable<T> {
      return this.http.put<T>(`${this.baseUrl}/${endpoint}`, body)
        .pipe(catchError(this.handleError));
    }
  
    // Common DELETE request
    delete<T>(endpoint: string): Observable<T> {
      return this.http.delete<T>(`${this.baseUrl}/${endpoint}`)
        .pipe(catchError(this.handleError));
    }
  
    // Error handling function
    private handleError(error: any): Observable<never> {
      console.error('API error:', error);
      return throwError(() => error);
    }

    dataURLToBlob(dataURL: string): Blob {
      const byteString = atob(dataURL.split(',')[1]);
      const arrayBuffer = new ArrayBuffer(byteString.length);
      const intArray = new Uint8Array(arrayBuffer);
  
      for (let i = 0; i < byteString.length; i++) {
        intArray[i] = byteString.charCodeAt(i);
      }
  
      return new Blob([arrayBuffer], { type: 'image/png' });
    }

    loadFile(id: number): Observable<any> {
      return this.http.get('file/' + id, {responseType: 'blob'});
    }

}
