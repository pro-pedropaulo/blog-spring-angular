export class Reaction {
    constructor(
      public id?: number,
      public userId?: number,
      public postId?: number,
      public reactionLike?: boolean, 
      public username?: string  
    ) {}
  }
  