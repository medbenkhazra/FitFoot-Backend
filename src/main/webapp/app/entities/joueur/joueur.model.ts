import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IEquipe } from 'app/entities/equipe/equipe.model';
import { IQuartier } from 'app/entities/quartier/quartier.model';
import { GENDER } from 'app/entities/enumerations/gender.model';

export interface IJoueur {
  id: number;
  birthDay?: dayjs.Dayjs | null;
  gender?: GENDER | null;
  avatar?: string | null;
  avatarContentType?: string | null;
  user?: Pick<IUser, 'id'> | null;
  equipes?: Pick<IEquipe, 'id'>[] | null;
  quartier?: Pick<IQuartier, 'id'> | null;
}

export type NewJoueur = Omit<IJoueur, 'id'> & { id: null };
