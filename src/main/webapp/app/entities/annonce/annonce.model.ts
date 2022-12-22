import dayjs from 'dayjs/esm';
import { IEquipe } from 'app/entities/equipe/equipe.model';
import { ITerrain } from 'app/entities/terrain/terrain.model';
import { IJoueur } from 'app/entities/joueur/joueur.model';
import { STATUS } from 'app/entities/enumerations/status.model';

export interface IAnnonce {
  id: number;
  description?: string | null;
  heureDebut?: dayjs.Dayjs | null;
  heureFin?: dayjs.Dayjs | null;
  duree?: number | null;
  validation?: boolean | null;
  nombreParEquipe?: number | null;
  status?: STATUS | null;
  equipe?: Pick<IEquipe, 'id'> | null;
  terrain?: Pick<ITerrain, 'id'> | null;
  responsable?: Pick<IJoueur, 'id'> | null;
}

export type NewAnnonce = Omit<IAnnonce, 'id'> & { id: null };
