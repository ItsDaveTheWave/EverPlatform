import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  tokenUrl: string = 'http://localhost:8080/oauth/token';
  checkTokenUrl: string = 'http://localhost:8080/oauth/check_token?token=';

  accessToken: string = '';
  clientId: string = 'web';
  clientSecret: string = 'pass';

  constructor(private httpClient: HttpClient) { }

  public token(username: string, password: string): Observable<any> {
    const body = new HttpParams()
      .set('username', username)
      .set('password', password)
      .set('grant_type', 'password');
    return this.httpClient.post(this.tokenUrl, body.toString(), {
      headers: new HttpHeaders()
        .set('Content-Type', 'application/x-www-form-urlencoded')
        .set('Authorization', "Basic " + btoa(this.clientId + ":" + this.clientSecret))
    });
  }

  public checkToken(): Observable<any> {
    return this.httpClient.post(this.checkTokenUrl + this.accessToken, null, {
      headers: new HttpHeaders()
        .set('Authorization', "Basic " + btoa(this.clientId + ":" + this.clientSecret))
    });
  }
}