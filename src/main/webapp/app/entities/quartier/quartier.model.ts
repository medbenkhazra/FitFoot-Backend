import { IVille } from 'app/entities/ville/ville.model';

export interface IQuartier {
  id: number;
  nom?: string | null;
  ville?: Pick<IVille, 'id'> | null;
}

export type NewQuartier = Omit<IQuartier, 'id'> & { id: null };
