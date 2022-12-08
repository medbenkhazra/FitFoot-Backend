import { IComplexe, NewComplexe } from './complexe.model';

export const sampleWithRequiredData: IComplexe = {
  id: 50987,
};

export const sampleWithPartialData: IComplexe = {
  id: 93527,
};

export const sampleWithFullData: IComplexe = {
  id: 76373,
  nom: 'Computers interfaces',
  longitude: 54622,
  latitude: 73969,
};

export const sampleWithNewData: NewComplexe = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
