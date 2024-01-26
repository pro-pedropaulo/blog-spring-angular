import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PostUpdateService {
  private postUpdateSubject = new BehaviorSubject<{ postId: number, newCommentCount: number } | null>(null);
  postUpdateAction$ = this.postUpdateSubject.asObservable();

  updatePostComments(postId: number, newCommentCount: number) {
    this.postUpdateSubject.next({ postId, newCommentCount });
  }
}
