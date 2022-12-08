import { IAnnonce } from 'app/entities/annonce/annonce.model';
import { IJoueur } from 'app/entities/joueur/joueur.model';

export interface IEquipe {
  id: number;
  annonce?: Pick<IAnnonce, 'id'> | null;
  joueurs?: Pick<IJoueur, 'id'>[] | null;
}

export type NewEquipe = Omit<IEquipe, 'id'> & { id: null };
