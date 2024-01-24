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

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeaderComponent, CommonModule, HttpClientModule,
    MatCardModule, MatInputModule, MatButtonModule, MatIconModule ],
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
    let htmlContent = post.content || '';
  
    // Verifica se imageUrl está vazio e imageUrls está disponível
    if (!post.imageUrl && post.imageUrls && post.imageUrls.length > 0) {
      const imagesHtml = post.imageUrls.map(url => `<img src="${url}" class="post-image">`).join('');
      htmlContent = imagesHtml + htmlContent;
    } else if (post.imageUrl) {
      const imageHtml = `<img src="${post.imageUrl}" class="post-image">`;
      htmlContent = imageHtml + htmlContent;
    }
  
    return this.sanitizer.bypassSecurityTrustHtml(htmlContent);
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
  
}