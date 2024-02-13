import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, throwError } from "rxjs";
import { Post } from "../../model/post.model";
import { AuthService } from "../auth/auth-service.service";

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient, private authService: AuthService) {}

createPost(post: Post): Observable<Post> {
  const token = this.authService.getToken();
  if (!token) {
    console.error('Authentication token not found');
    return throwError(() => new Error('Authentication token not found'));
  }

  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });
  return this.http.post<Post>(`${this.apiUrl}/posts`, post, { headers });
}

getAllPosts(): Observable<Post[]> {
  return this.http.get<Post[]>(`${this.apiUrl}/posts`);
}

getPostsById(postId: number): Observable<Post> {
  return this.http.get<Post>(`${this.apiUrl}/posts/${postId}`);
}

deletePost(postId: number): Observable<any> {
  const token = this.authService.getToken();
  if (!token) {
    console.error('Authentication token not found');
    return throwError(() => new Error('Authentication token not found'));
  }

  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });
  return this.http.delete(`${this.apiUrl}/posts/${postId}`, { headers });
}

async uploadImages(files: File | File[]): Promise<string[]> {
  const formData = new FormData();

  if (Array.isArray(files)) {
      files.forEach(file => {
          formData.append('files', file);
      });
  } else {
      formData.append('files', files);
  }

  try {
      const response = await this.http.post<any>('http://localhost:8080/posts/upload-images', formData).toPromise();
      return response.imageUrls;
  } catch (error) {
      console.error('Erro ao fazer upload das imagens:', error);
      throw error;
  }
}


likePost(postId: number): Observable<Post> {
  const token = this.authService.getToken();
  if (!token) {
    console.error('Authentication token not found');
    return throwError(() => new Error('Authentication token not found'));
  }

  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  return this.http.post<Post>(`${this.apiUrl}/posts/${postId}/like`, {}, { headers });
}


dislikePost(postId: number): Observable<Post> {
  return this.http.post<Post>(`${this.apiUrl}/posts/${postId}/dislike`, {});
}

}
