// Angular - reaction.model.ts
export class Reaction {
    constructor(
      public id?: number,
      public userId?: number,
      public postId?: number,
      public reactionLike?: boolean, // Modificado de isLike para reactionLike
      public username?: string  
    ) {}
  }
  