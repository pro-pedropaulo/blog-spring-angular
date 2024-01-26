import { ChangeDetectorRef, Component } from '@angular/core';
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
import { ConfirmDialogComponent } from '../../modals/confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { TranslateDatePipe } from '../../pipes/translate-date.pipe';
import { TranslateMonthPipe } from '../../pipes/translate-month.pipe';
import { ReactionService } from '../../services/reactions/reaction.service';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeaderComponent, CommonModule, HttpClientModule,
    MatCardModule, MatInputModule, MatButtonModule, MatIconModule,
    NgbModule, TranslateDatePipe, TranslateMonthPipe],
  providers: [PostService, AuthService, ReactionService],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  posts: Post[] = [];
  postsToShow: number = 5;
  postsToLoad: number = 5;
  hasMorePosts: boolean = true;

  constructor(
    private router: Router,
    private postService: PostService,
    private sanitizer: DomSanitizer,
    private authService: AuthService,
    private dialog: MatDialog,
    private reactionService: ReactionService,
    private cdRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadPosts();
    // console.log('Usuário logado:', this.getLoggedInUsername());
  }

  sanitizeHtml(post: Post): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(post.content || '');
  }
  
  isLoggedIn(): boolean {
    return this.authService.getToken() != null;
  }


  navigateToAddPost(): void {
    this.router.navigate(['/add-post']);
  }

  loadPosts(loadMore: boolean = false): void {
    this.postService.getAllPosts().subscribe({
      next: (data) => {
        if (loadMore) {
          this.posts = [...this.posts, ...data.slice(this.posts.length, this.postsToShow)];
        } else {
          this.posts = data.slice(0, this.postsToShow);
        }
        this.hasMorePosts = this.postsToShow < data.length;
      },
      error: (err) => {
        console.error('Erro ao buscar posts:', err);
      }
    });
  }
  
  loadMorePosts(): void {
    this.postsToShow += this.postsToLoad;
    console.log('PostsToShow após incremento:', this.postsToShow);
    this.loadPosts(true);
    this.cdRef.detectChanges();
  }
  
  
  openConfirmDialog(postId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '250px',
      data: { message: "Tem certeza que deseja excluir este post?" }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deletePostConfirmed(postId);
      }
    });
  }

  deletePostConfirmed(postId: number): void {
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
  
  handleReaction(post: Post, isLike: boolean): void {
    if (!this.isLoggedIn()) {
      this.showLoginPopup();
      return;
    }
  
    const reaction = { postId: post.id, isLike: isLike };
    this.reactionService.reactToPost(post.id!, isLike).subscribe(
      updatedPost => {
        console.log('Post atualizado:', updatedPost);
        const index = this.posts.findIndex(p => p.id === updatedPost.id);
        console.log('Index:', index);
        if (index !== -1) {
          this.posts[index] = updatedPost;
        }
      },
      error => console.error('Erro ao processar reação:', error)
    );
  }
  
  showLoginPopup(): void {
    alert('Crie uma conta para reagir a este post.');
  }
  
  handleDislike(post: Post): void {
    if (!this.isLoggedIn()) {
      this.showLoginPopup();
      return;
    }
    this.postService.dislikePost(post.id!).subscribe(
      updatedPost => {
        const index = this.posts.findIndex(p => p.id === updatedPost.id);
        if (index !== -1) {
          this.posts[index] = updatedPost;
        }
      },
      error => console.error('Erro ao processar dislike:', error)
    );
  }
}