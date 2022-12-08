import dayjs from 'dayjs/esm';
import { ITerrain } from 'app/entities/terrain/terrain.model';

export interface IReservation {
  id: number;
  date?: dayjs.Dayjs | null;
  heureDebut?: dayjs.Dayjs | null;
  heureFin?: dayjs.Dayjs | null;
  terrain?: Pick<ITerrain, 'id'> | null;
}

export type NewReservation = Omit<IReservation, 'id'> & { id: null };
