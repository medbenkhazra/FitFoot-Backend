import dayjs from 'dayjs/esm';

import { IReservation, NewReservation } from './reservation.model';

export const sampleWithRequiredData: IReservation = {
  id: 52422,
};

export const sampleWithPartialData: IReservation = {
  id: 93887,
  heureFin: dayjs('2022-12-05T06:52'),
};

export const sampleWithFullData: IReservation = {
  id: 88349,
  date: dayjs('2022-12-05'),
  heureDebut: dayjs('2022-12-05T13:28'),
  heureFin: dayjs('2022-12-05T03:51'),
};

export const sampleWithNewData: NewReservation = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
