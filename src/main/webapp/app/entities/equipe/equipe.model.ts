import { IJoueur } from 'app/entities/joueur/joueur.model';

export interface IEquipe {
  id: number;
  joueurs?: Pick<IJoueur, 'id'>[] | null;
}

export type NewEquipe = Omit<IEquipe, 'id'> & { id: null };
