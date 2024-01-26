import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Reaction } from '../../model/reaction.model';
import { AuthService } from '../auth/auth-service.service';

@Injectable({
  providedIn: 'root'
})
export class ReactionService {
  private apiUrl = 'http://localhost:8080/posts';

  constructor(private http: HttpClient, private authService: AuthService) {}

reactToPost(postId: number, isLike?: boolean): Observable<any> {
  const username = this.authService.getLoggedInUsername(); 
  const reaction = new Reaction();
  reaction.username = username || '';
  reaction.reactionLike = isLike; 
  return this.http.post(`${this.apiUrl}/${postId}/react`, reaction);
}
}
