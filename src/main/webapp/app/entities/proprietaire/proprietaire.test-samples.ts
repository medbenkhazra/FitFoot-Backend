import { IProprietaire, NewProprietaire } from './proprietaire.model';

export const sampleWithRequiredData: IProprietaire = {
  id: 49243,
};

export const sampleWithPartialData: IProprietaire = {
  id: 83210,
  cin: 'Wisconsin Function-based',
  rib: 'area Cotton',
  numTel: 'Wooden intangible',
};

export const sampleWithFullData: IProprietaire = {
  id: 64082,
  avatar: '../fake-data/blob/hipster.png',
  avatarContentType: 'unknown',
  cin: 'transmitting RAM',
  rib: 'sexy matrix',
  numTel: 'Intranet synthesizing connect',
};

export const sampleWithNewData: NewProprietaire = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
