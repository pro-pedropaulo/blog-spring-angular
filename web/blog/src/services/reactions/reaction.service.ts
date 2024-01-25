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

// Angular - reaction.service.ts
reactToPost(postId: number, isLike?: boolean): Observable<any> {
  const username = this.authService.getLoggedInUsername(); // Supondo que você tenha esse método
  const reaction = new Reaction();
  reaction.username = username || '';
  reaction.reactionLike = isLike; // Garantir que isLike está definido corretamente
  console.log('reaction', reaction);
  return this.http.post(`${this.apiUrl}/${postId}/react`, reaction);
}


}
