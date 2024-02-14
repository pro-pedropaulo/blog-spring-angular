import { Component } from '@angular/core';
import { PostService } from '../../services/post/post.service';
import { Post } from '../../model/post.model';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; 
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { QuillModule } from 'ngx-quill';
import { AuthService } from '../../services/auth/auth-service.service';
import { SuccessModalComponent } from '../../modals/success-modal/success-modal.component';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-add-post',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, ReactiveFormsModule,
     MatSelectModule, MatIconModule, MatProgressSpinnerModule,  QuillModule],
  providers: [PostService, AuthService],
  templateUrl: './add-post.component.html',
  styleUrl: './add-post.component.scss'
})
export class AddPostComponent {

    constructor(private postService: PostService,
        private dialog: MatDialog,
        private router: Router,
        ) {}

    selectedImage: File | null = null;
    selectedImages: File[] = [];
    previewImage: string | ArrayBuffer | null = null;
    imagePreviews: string[] = [];
    isLoading = false;

    editorOptions = {
        modules: {
          toolbar: [
            ['bold', 'italic', 'underline', 'strike'], 
            ['link'] 
          ]
        }
      };
      
  postType = 'post'; 
    postData = {
        title: '',
        content: '',
        imageUrl: ''
    };

    
    onTypeChange() {
        // Lógica para lidar com a mudança de tipo
    }

    onImageSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input?.files && input.files[0]) {
            const file = input.files[0];
            this.selectedImage = file;
    
            const reader = new FileReader();
            reader.onload = (e: ProgressEvent<FileReader>) => {
                this.previewImage = e.target!.result;
            };
            reader.readAsDataURL(file);
        } else {
            this.selectedImage = null;
            this.previewImage = null;
        }
    }

    onImagesSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input.files) {
            const newFiles = Array.from(input.files);
            this.selectedImages = [...this.selectedImages, ...newFiles];
    
            newFiles.forEach(file => {
                const reader = new FileReader();
                reader.onload = (e: ProgressEvent<FileReader>) => {
                    if (!this.imagePreviews.includes(e.target!.result as string)) {
                        this.imagePreviews.push(e.target!.result as string);
                    }
                };
                reader.readAsDataURL(file);
            });
        }
    }
    

    removeSelectedImage(index: number) {
        this.selectedImages.splice(index, 1);
        this.imagePreviews.splice(index, 1);
    }

    removePreviewImage() {
        this.selectedImage = null;
        this.previewImage = null;
    }
    
    async onSubmit() {
        this.isLoading = true;
        try {
            let imageUrlsArray: string[] = [];
            if (this.postType === 'post' && this.selectedImage) {
                imageUrlsArray = await this.postService.uploadImages(this.selectedImage);
            } else if (this.postType === 'album' && this.selectedImages.length > 0) {
                imageUrlsArray = await this.postService.uploadImages(this.selectedImages);
            }
    
            const postToSubmit: Post = {
                title: this.postData.title,
                content: this.postData.content,
                imageUrl: this.postType === 'post' ? imageUrlsArray[0] : '', 
                imageUrls: this.postType === 'album' ? imageUrlsArray : [] 
            };
    
            this.postService.createPost(postToSubmit).subscribe({
                next: (response) => {
                    this.isLoading = false;
                    const dialogRef = this.dialog.open(SuccessModalComponent, { data: { title: this.postType === 'post' ? 'Post criado com sucesso!' : 'Álbum criado com sucesso!' } });
                    setTimeout(() => {
                        dialogRef.close();
                        this.router.navigate(['']);
                    }, 1500);
                },
                error: (error) => {
                    console.error('Erro ao criar post', error);
                    this.isLoading = false;
                }
            });
        } catch (error) {
            console.error('Erro ao fazer upload da(s) imagem(ns):', error);
            this.isLoading = false;
        }
    }
}    
                    
    

