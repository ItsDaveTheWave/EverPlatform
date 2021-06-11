import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Course } from '../model/course';

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  url: string = 'http://localhost:8080/api/course/';

  constructor(private httpClient: HttpClient) { }

  public getAll(): Observable<Course[]> {
    return this.httpClient.get<Course[]>(this.url);
  }
}