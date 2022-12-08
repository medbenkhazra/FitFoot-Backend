import dayjs from 'dayjs/esm';

import { GENDER } from 'app/entities/enumerations/gender.model';

import { IJoueur, NewJoueur } from './joueur.model';

export const sampleWithRequiredData: IJoueur = {
  id: 35154,
};

export const sampleWithPartialData: IJoueur = {
  id: 43798,
  birthDay: dayjs('2022-12-05'),
  gender: GENDER['MALE'],
};

export const sampleWithFullData: IJoueur = {
  id: 54267,
  birthDay: dayjs('2022-12-04'),
  gender: GENDER['FEMALE'],
  avatar: '../fake-data/blob/hipster.png',
  avatarContentType: 'unknown',
};

export const sampleWithNewData: NewJoueur = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
