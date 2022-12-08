import { IQuartier } from 'app/entities/quartier/quartier.model';
import { IProprietaire } from 'app/entities/proprietaire/proprietaire.model';

export interface IComplexe {
  id: number;
  nom?: string | null;
  longitude?: number | null;
  latitude?: number | null;
  quartier?: Pick<IQuartier, 'id'> | null;
  proprietaire?: Pick<IProprietaire, 'id'> | null;
}

export type NewComplexe = Omit<IComplexe, 'id'> & { id: null };
