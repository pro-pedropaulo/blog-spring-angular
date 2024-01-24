import { Component, Inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth-service.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CommentService } from '../../services/comments/comments.service';
import { Comment } from '../../model/comments.model';
import { ActivatedRoute } from '@angular/router';
import { PostService } from '../../services/post/post.service';
import { MatIconModule} from '@angular/material/icon';


@Component({
  selector: 'app-comments',
  standalone: true,
  imports: [MatCardModule, MatInputModule, MatButtonModule, MatIconModule, ReactiveFormsModule,
    HttpClientModule, MatListModule, MatDialogModule, FormsModule, CommonModule],
  providers: [AuthService, CommentService, PostService],
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
    private authService: AuthService,
    private route: ActivatedRoute,
    private postService: PostService
  ) { }

  ngOnInit(): void {
    this.postId = +this.route?.snapshot?.paramMap?.get('id')!;
    if (this.postId) {
      this.loadCommentsByPostId(this.postId);
    }
    this.checkIfAuthor();
    console.log('Usuário logado:', this.authService.getLoggedInUsername());
  }

  checkIfAuthor(): void {
    // Supondo que você tenha uma função para buscar os detalhes do post
    this.postService.getPostsById(this.postId).subscribe(post => {
      console.log('Post:', post);
      this.isAuthor = this.authService.getLoggedInUsername() === post.app_user?.username;
    });
  }

  deleteComment(commentId: number): void {
    // Chama o serviço para excluir o comentário
    this.commentService.deleteComment(commentId).subscribe(() => {
      // Recarregar os comentários
      this.loadCommentsByPostId(this.postId);
    });
  }
  
  loadCommentsByPostId(postId: number): void {
    this.commentService.getCommentsByPostId(postId).subscribe({
      next: (comments) => {
        this.comments = comments;
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
    const newComment = new Comment(undefined, this.postId, this.newCommentContent, { username: loggedInUsername });

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

