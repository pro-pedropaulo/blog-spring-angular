import { Component } from '@angular/core';
import { HeaderComponent } from '../header/header.component';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Post } from '../../model/post.model';
import { PostService } from '../../services/post/post.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../services/auth/auth-service.service';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule} from '@angular/material/icon';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'; 


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeaderComponent, CommonModule, HttpClientModule,
    MatCardModule, MatInputModule, MatButtonModule, MatIconModule,
    NgbModule],
  providers: [PostService, AuthService],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  posts: Post[] = [];

  constructor(
    private router: Router,
    private postService: PostService,
    private sanitizer: DomSanitizer,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loadPosts();
    console.log('Usuário logado:', this.getLoggedInUsername());
  }

  sanitizeHtml(post: Post): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(post.content || '');
  }
  
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  navigateToAddPost(): void {
    this.router.navigate(['/add-post']);
  }

  loadPosts(): void {
    this.postService.getAllPosts().subscribe({
      next: (data) => {
        this.posts = data;
      },
      error: (err) => {
        console.error('Erro ao buscar posts:', err);
      }
    });
  }

  deletePost(postId: number): void {
    this.postService.deletePost(postId).subscribe({
      next: () => {
        console.log('Post excluído com sucesso');
        this.loadPosts();
      },
      error: (err) => {
        console.error('Erro ao excluir post:', err);
      }
    });
  }

  isPostAuthor(post: Post): boolean {
    const loggedInUsername = this.authService.getLoggedInUsername(); 
    return post.app_user?.username === loggedInUsername;
  }

  getLoggedInUsername(): string | null {
    return this.authService.getLoggedInUsername();
  }

  navigateToComments(postId: number): void {
    this.router.navigate(['/comments', postId]);
  }
  
  
  
}