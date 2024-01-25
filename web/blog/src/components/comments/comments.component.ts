import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth-service.service';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CommentService } from '../../services/comments/comments.service';
import { Comment } from '../../model/comments.model';
import { ActivatedRoute } from '@angular/router';
import { PostService } from '../../services/post/post.service';
import { MatIconModule} from '@angular/material/icon';
import { ConfirmDialogComponent } from '../../modals/confirm-dialog/confirm-dialog.component';


@Component({
  selector: 'app-comments',
  standalone: true,
  imports: [MatCardModule, MatInputModule, MatButtonModule, MatIconModule, ReactiveFormsModule,
    HttpClientModule, MatListModule, MatDialogModule, FormsModule, CommonModule],
  providers: [AuthService, CommentService, PostService, ConfirmDialogComponent],
  templateUrl: './comments.component.html',
  styleUrl: './comments.component.scss'
})
export class CommentsComponent {
  comments: Comment[] = [];
  newCommentContent: string = '';
  postId: number = 0;
  isAuthor: boolean = false;

  constructor(
    private commentService: CommentService,
    public authService: AuthService,
    private route: ActivatedRoute,
    private postService: PostService,
    public dialog: MatDialog,
    private changeDetectorRef: ChangeDetectorRef,
  ) { }

  ngOnInit(): void {
    this.postId = +this.route?.snapshot?.paramMap?.get('id')!;
    if (this.postId) {
      this.loadCommentsByPostId(this.postId);
    }
    this.checkIfAuthor();
  }

  checkIfAuthor(): void {
    this.postService.getPostsById(this.postId).subscribe(post => {
      console.log('Post:', post);
      this.isAuthor = this.authService.getLoggedInUsername() === post.app_user?.username;
    });
  }

  isCommentAuthor(commentUsername: any): boolean {
    const loggedInUsername = this.authService.getLoggedInUsername();
    return loggedInUsername === commentUsername;
  }
  
  deleteComment(commentId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '250px',
      data: { message: "Tem certeza que deseja excluir este comentário?" }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.commentService.deleteComment(commentId).subscribe({
          next: () => {
            this.comments = this.comments.filter(comment => comment.id !== commentId);
            this.changeDetectorRef.detectChanges();
          },
          error: (err) => {
            console.error('Erro ao excluir comentário:', err);
          }
        });
      }
    });
  }
  
loadCommentsByPostId(postId: number): void {
  this.commentService.getCommentsByPostId(postId).subscribe({
    next: (comments) => {
      this.comments = [...comments]; 
    },
    error: (err) => {
      console.error('Erro ao carregar comentários:', err);
    }
  });
}

postComment(): void {
  if (this.newCommentContent.trim() === '') {
    return;
  }

  const loggedInUsername = this.authService.getLoggedInUsername() || 'Visitante';
  const newComment = new Comment(
    undefined,
    this.postId,
    loggedInUsername,
    { username: loggedInUsername }, 
    this.newCommentContent
  );

  this.commentService.createComment(newComment).subscribe({
    next: (comment) => {
      this.comments.push(comment);
      this.newCommentContent = '';
    },
    error: (err) => {
      console.error('Erro ao postar comentário:', err);
    }
  });
}

}

