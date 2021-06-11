import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Course } from 'src/app/model/course';
import { CourseService } from 'src/app/service/course.service';

@Component({
  selector: 'app-course-list',
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.css']
})
export class CourseListComponent implements OnInit {

  courses: Course[] = [];

  constructor(
    private courseService: CourseService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.courseService.getAll().subscribe(
      data => {
        this.courses = data;
      },
      err => {
        this.toastr.error("Sorry", 'Unexpected error', {
          timeOut: 3000,
          positionClass: 'toast-bottom-center'
        });
      }
    );
  }
}