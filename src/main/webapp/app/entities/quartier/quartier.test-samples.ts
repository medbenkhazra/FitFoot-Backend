import { IQuartier, NewQuartier } from './quartier.model';

export const sampleWithRequiredData: IQuartier = {
  id: 36622,
};

export const sampleWithPartialData: IQuartier = {
  id: 96063,
};

export const sampleWithFullData: IQuartier = {
  id: 24714,
  nom: 'Mouse Soft USB',
};

export const sampleWithNewData: NewQuartier = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
