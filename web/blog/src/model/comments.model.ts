export class Comment {
    constructor(
      public id?: number,
      public postId?: number,
      public username?: string,
      public app_user?: { username?: string },
      public content?: string,
      public createdAt?: Date
    ) {}
  }
  