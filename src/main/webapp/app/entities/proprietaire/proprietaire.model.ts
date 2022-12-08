import { IUser } from 'app/entities/user/user.model';

export interface IProprietaire {
  id: number;
  avatar?: string | null;
  avatarContentType?: string | null;
  cin?: string | null;
  rib?: string | null;
  numTel?: string | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewProprietaire = Omit<IProprietaire, 'id'> & { id: null };
