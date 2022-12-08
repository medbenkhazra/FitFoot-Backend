export interface IVille {
  id: number;
  nom?: string | null;
}

export type NewVille = Omit<IVille, 'id'> & { id: null };
