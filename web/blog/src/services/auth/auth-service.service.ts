import { HttpClient } from "@angular/common/http";
import { Injectable, OnDestroy } from "@angular/core";
import { Observable, tap, interval, Subscription } from "rxjs";
import jwt_decode from 'jwt-decode';

@Injectable({ providedIn: 'root' })
export class AuthService implements OnDestroy {
  private loginUrl = 'http://localhost:8080/users/login';
  private tokenCheckSubscription!: Subscription;

  constructor(private http: HttpClient) {
    if (typeof window !== 'undefined') {
      this.startTokenCheck();
    }
  }

  ngOnDestroy(): void {
    this.stopTokenCheck();
  }

  private startTokenCheck(): void {
    this.tokenCheckSubscription = interval(5 * 60 * 1000).subscribe(() => {
      if (this.isTokenExpired()) {
        this.logout();
      }
    });
  }

  private stopTokenCheck(): void {
    if (this.tokenCheckSubscription) {
      this.tokenCheckSubscription.unsubscribe();
    }
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(this.loginUrl, { username, password }).pipe(
      tap(response => {
        if (response && response.token && typeof window !== 'undefined') {
          localStorage.setItem('access_token', response.token);
          localStorage.setItem('username', response.username);
        }
      })
    );
  }

  logout(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('access_token');
      localStorage.removeItem('username');
    }
    this.stopTokenCheck();
  }

  getToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('access_token');
    }
    return null;
  }

  getLoggedInUsername(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('username');
    }
    return null;
  }

  private decodeToken(token: string): any {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }

  getTokenExpirationDate(token: string): Date | null {
    const decoded = this.decodeToken(token);
    if (!decoded || decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0); 
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  isTokenExpired(token?: string): boolean {
    token = token! ?? this.getToken();
    if (!token) {
      return true;
    }

    const date = this.getTokenExpirationDate(token);
    return !date || date.valueOf() <= new Date().valueOf();
  }
}
