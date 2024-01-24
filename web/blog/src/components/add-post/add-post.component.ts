import { Component } from '@angular/core';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { PostService } from '../../services/post/post.service';
import { Post } from '../../model/post.model';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // Add this import
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { QuillModule } from 'ngx-quill';
import { AuthService } from '../../services/auth/auth-service.service';

@Component({
  selector: 'app-add-post',
  standalone: true,
  imports: [CKEditorModule, FormsModule, CommonModule, HttpClientModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, ReactiveFormsModule,
     MatSelectModule, QuillModule],
  providers: [PostService, AuthService],
  templateUrl: './add-post.component.html',
  styleUrl: './add-post.component.scss'
})
export class AddPostComponent {

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


    constructor(private postService: PostService) {}
    selectedImage: File | null = null;
    selectedImages: File[] = [];

    

    onTypeChange() {
        // Lógica para lidar com a mudança de tipo
    }

    onImageSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length) {
            this.selectedImage = input.files[0];
        } else {
            this.selectedImage = null;
        }
    }

    onImagesSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length) {
            this.selectedImages = Array.from(input.files);
        } else {
            this.selectedImages = [];
        }
    }
    
    

    async onSubmit() {
        if (this.postType === 'post') {
            try {
                let imageUrl = '';
                if (this.selectedImage) {
                    console.log('Fazendo upload da imagem...');
                    imageUrl = await this.postService.uploadImage(this.selectedImage);
                    console.log('Upload da imagem concluído!');
                }
    
                const postToSubmit: Post = {
                    title: this.postData.title,
                    content: this.postData.content,
                    imageUrl: imageUrl
                };
    
                this.postService.createPost(postToSubmit).subscribe({
                    next: (response) => {
                        console.log('Post criado com sucesso!', response);
                        // Redirecionar ou limpar formulário
                    },
                    error: (error) => {
                        console.error('Erro ao criar post', error);
                    }
                });
            } catch (error) {
                console.error('Erro ao fazer upload da imagem:', error);
            }
        }
            else if (this.postType === 'album') {
                try {
                    let imageUrlsArray: string[] = [];
                    if (this.selectedImages.length > 0) {
                        console.log('Fazendo upload das imagens...');
                        imageUrlsArray = await this.postService.uploadMultipleImages(this.selectedImages);
                        console.log('Upload das imagens concluído!', imageUrlsArray);
                    }
            
                    // Criar um objeto Post para o álbum
                    const albumToSubmit: Post = {
                        title: this.postData.title,
                        content: this.postData.content, // Você pode querer adicionar uma descrição ou outro conteúdo aqui
                        imageUrls: imageUrlsArray // Atribuindo o array de URLs de imagem
                    };
            
                    // Realizar a chamada para criar o post tipo álbum
                    this.postService.createPost(albumToSubmit).subscribe({
                        next: (response) => {
                            console.log('Álbum criado com sucesso!', response);
                            // Aqui você pode redirecionar o usuário ou limpar o formulário
                            // Por exemplo, resetar o formulário ou redirecionar para outra página
                        },
                        error: (error) => {
                            console.error('Erro ao criar álbum', error);
                            // Tratamento de erros, como exibir uma mensagem de erro ao usuário
                        }
                    });
                } catch (error) {
                    console.error('Erro ao fazer upload das imagens:', error);
                    // Tratamento de erro para o caso de falha no upload das imagens
                }
            }
        }
    }
                    
    

