import { Routes } from '@angular/router';
import { HomeComponent } from './../components/home/home.component';
import { LoginComponent } from './../components/login/login.component';
import { RegisterComponent } from '../components/register/register.component';
import { AddPostComponent } from '../components/add-post/add-post.component';
import { CommentsComponent } from '../components/comments/comments.component';

export const routes: Routes = [
    {'path': '', component: HomeComponent },
    {'path': 'login', component: LoginComponent },
    {'path': 'register', component: RegisterComponent },
    {'path': 'add-post', component: AddPostComponent},
    {'path': 'comments/:id', component: CommentsComponent}
];
