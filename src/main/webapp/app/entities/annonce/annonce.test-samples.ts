import dayjs from 'dayjs/esm';

import { STATUS } from 'app/entities/enumerations/status.model';

import { IAnnonce, NewAnnonce } from './annonce.model';

export const sampleWithRequiredData: IAnnonce = {
  id: 9279,
};

export const sampleWithPartialData: IAnnonce = {
  id: 32840,
  duree: 45701,
  validation: false,
  nombreParEquipe: 52373,
};

export const sampleWithFullData: IAnnonce = {
  id: 83912,
  description: 'Product interactive',
  heureDebut: dayjs('2022-12-05T17:20'),
  heureFin: dayjs('2022-12-05T04:38'),
  duree: 17161,
  validation: true,
  nombreParEquipe: 3388,
  status: STATUS['ENCOURS'],
};

export const sampleWithNewData: NewAnnonce = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
