import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('jwtToken');

    // Clone and modify the request if the token is available
    if (token) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `${token}`),
      });
      return next.handle(authReq);
    }
    return next.handle(req);
  }

}
