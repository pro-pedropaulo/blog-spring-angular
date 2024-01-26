import { ChangeDetectorRef, Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
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
import {MatTooltipModule} from '@angular/material/tooltip';
import { ConfirmDialogComponent } from '../../modals/confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { TranslateDatePipe } from '../../pipes/translate-date.pipe';
import { TranslateMonthPipe } from '../../pipes/translate-month.pipe';
import { ReactionService } from '../../services/reactions/reaction.service';
import { FooterComponent } from '../footer/footer.component';
import { LoginPopupComponent } from '../../modals/login-popup/login-popup.component';
import { Subscription } from 'rxjs/internal/Subscription';
import { PostUpdateService } from '../../services/util-services/post-update.service';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, HttpClientModule,
    MatCardModule, MatInputModule, MatButtonModule, MatIconModule,
    NgbModule, TranslateDatePipe, TranslateMonthPipe, FooterComponent, MatTooltipModule],
  providers: [PostService, AuthService, ReactionService, LoginPopupComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  constructor(
    private router: Router,
    private postService: PostService,
    private sanitizer: DomSanitizer,
    private authService: AuthService,
    private dialog: MatDialog,
    private reactionService: ReactionService,
    private cdRef: ChangeDetectorRef,
    private postUpdateService: PostUpdateService
  ) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.reloadPosts();
      }
    });
  }

  postsToShow: number = 5;
  postsToLoad: number = 5;
  initialPostsToShow: number = 5; 
  hasMorePosts: boolean = true;
  postsMap: Map<number, Post> = new Map();
  excludedPosts = new Set<number>();
  private postUpdateSubscription!: Subscription;

  ngOnInit(): void {
    this.reloadPosts();
    this.subscribeToPostUpdates();
  }

  ngOnDestroy() {
    if (this.postUpdateSubscription) {
      this.postUpdateSubscription.unsubscribe();
    }
  }

  subscribeToPostUpdates() {
    this.postUpdateSubscription = this.postUpdateService.postUpdateAction$.subscribe(update => {
      if (update) {
        const post = this.postsMap.get(update.postId);
        if (post) {
          post.comments = new Array(update.newCommentCount).fill(null);
          this.cdRef.detectChanges();
        }
      }
    });
  }
  
  reloadPosts(): void {
    this.postService.getAllPosts().subscribe({
      next: (data) => {
        const filteredAndSortedData = data
          .filter(post => !this.excludedPosts.has(post.id!))
          .sort((a, b) => {
            const dateA = a.createdDate ? new Date(a.createdDate).getTime() : 0;
            const dateB = b.createdDate ? new Date(b.createdDate).getTime() : 0;
            return dateB - dateA;
          });
  
        this.postsMap.clear();
        filteredAndSortedData.forEach(post => {
          this.postsMap.set(post.id!, post);
        });
  
        this.postsToShow = this.initialPostsToShow;
        this.hasMorePosts = this.postsToShow < this.postsMap.size;
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao buscar posts:', err);
      }
    });
  }  
  
  sanitizeHtml(post: Post): SafeHtml {
    let content = post.content || '';
    return this.sanitizer.bypassSecurityTrustHtml(content);
  }
  
  isLoggedIn(): boolean {
    return this.authService.getToken() != null;
  }

  navigateToAddPost(): void {
    this.router.navigate(['/add-post']).then(() => {
      location.reload();
    });
  }
  
  loadPosts(loadMore: boolean = false): void {
    this.postService.getAllPosts().subscribe({
      next: (data) => {
        data.forEach(post => {
          this.postsMap.set(post.id!, post);
        });

        if (loadMore) {
          this.postsToShow += this.postsToLoad;
        }

        this.hasMorePosts = this.postsToShow < this.postsMap.size;
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao buscar posts:', err);
      }
    });
  }

  get posts(): Post[] {
    return Array.from(this.postsMap.values()).slice(0, this.postsToShow);
  }

  deletePostConfirmed(postId: number): void {
    this.postService.deletePost(postId).subscribe({
      next: () => {
        this.excludedPosts.add(postId);
        this.postsMap.delete(postId);
        this.updatePostsToShow();
      },
      error: (err) => {
        console.error('Erro ao excluir post:', err);
      }
    });
  }
  
  updatePostsToShow(): void {
    this.postsToShow = Math.max(0, this.postsToShow - 1);
    this.cdRef.detectChanges();
  }
  
  loadMorePosts(): void {
    this.postsToShow += this.postsToLoad;
    this.postService.getAllPosts().subscribe({
      next: (data) => {
        data.forEach(post => {
          if (!this.excludedPosts.has(post.id!)) {
            this.postsMap.set(post.id!, post);
          }
        });
  
        this.hasMorePosts = this.postsToShow < this.postsMap.size;
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar mais posts:', err);
      }
    });
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
  
  isPostAuthor(post: Post): boolean {
    const loggedInUsername = this.authService.getLoggedInUsername(); 
    return post.app_user?.username === loggedInUsername;
  }

  getLoggedInUsername(): string | null {
    return this.authService.getLoggedInUsername();
  }

  navigateToComments(postId: number): void {
    this.router.navigate(['/comments', postId]).then(() => {
      location.reload();
    });
  }
  
  handleReaction(post: Post, isLike: boolean): void {
    if (!this.isLoggedIn()) {
      this.showLoginPopup();
      return;
    }
  
    this.reactionService.reactToPost(post.id!, isLike).subscribe(
      updatedPost => {
        const index = this.posts.findIndex(p => p.id === updatedPost.id);
        if (index !== -1) {
          this.postsMap.set(updatedPost.id, updatedPost);
          this.updatePosts();
        }
      },
      error => console.error('Erro ao processar reação:', error)
    );
  }
  
  updatePosts(): void {
    this.cdRef.detectChanges();
  }
  
  showLoginPopup(): void {
    this.dialog.open(LoginPopupComponent, {
      width: '300px',
      position: { top: '20px', right: '20px' }
    });
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