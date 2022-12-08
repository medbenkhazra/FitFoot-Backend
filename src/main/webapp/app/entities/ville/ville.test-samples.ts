import { IVille, NewVille } from './ville.model';

export const sampleWithRequiredData: IVille = {
  id: 83175,
};

export const sampleWithPartialData: IVille = {
  id: 11134,
};

export const sampleWithFullData: IVille = {
  id: 58950,
  nom: 'Awesome',
};

export const sampleWithNewData: NewVille = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
