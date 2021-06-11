import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CourseListComponent } from './components/course-list/course-list.component';

const routes: Routes = [
  { path: '', component: CourseListComponent },
  { path: '**', redirectTo: '', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
