import { IComplexe } from 'app/entities/complexe/complexe.model';

export interface ITerrain {
  id: number;
  nom?: string | null;
  capaciteParEquipe?: number | null;
  complexe?: Pick<IComplexe, 'id'> | null;
}

export type NewTerrain = Omit<ITerrain, 'id'> & { id: null };
