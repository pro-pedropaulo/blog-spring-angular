export class Post {
  constructor(
    public id?: number,
    public title?: string,
    public content?: string,
    public comments?: any[],
    public createdDate?: Date,
    public app_user?: { username: string },
    public imageUrl?: string,
    public imageUrls?: string[],
    public likeCount?: number,     
    public dislikeCount?: number, 
    public likes?: Set<string>,
    public dislikes?: Set<string>
  ) {}
  }