import { IEquipe, NewEquipe } from './equipe.model';

export const sampleWithRequiredData: IEquipe = {
  id: 65850,
};

export const sampleWithPartialData: IEquipe = {
  id: 20388,
};

export const sampleWithFullData: IEquipe = {
  id: 36161,
};

export const sampleWithNewData: NewEquipe = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
