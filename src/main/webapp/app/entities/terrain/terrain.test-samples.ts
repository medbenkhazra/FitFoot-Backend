import { ITerrain, NewTerrain } from './terrain.model';

export const sampleWithRequiredData: ITerrain = {
  id: 82,
};

export const sampleWithPartialData: ITerrain = {
  id: 87031,
  nom: 'Horizontal',
};

export const sampleWithFullData: ITerrain = {
  id: 83535,
  nom: 'Designer',
  capaciteParEquipe: 78679,
};

export const sampleWithNewData: NewTerrain = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
