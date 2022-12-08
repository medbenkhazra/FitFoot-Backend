import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReservation, NewReservation } from '../reservation.model';

export type PartialUpdateReservation = Partial<IReservation> & Pick<IReservation, 'id'>;

type RestOf<T extends IReservation | NewReservation> = Omit<T, 'date' | 'heureDebut' | 'heureFin'> & {
  date?: string | null;
  heureDebut?: string | null;
  heureFin?: string | null;
};

export type RestReservation = RestOf<IReservation>;

export type NewRestReservation = RestOf<NewReservation>;

export type PartialUpdateRestReservation = RestOf<PartialUpdateReservation>;

export type EntityResponseType = HttpResponse<IReservation>;
export type EntityArrayResponseType = HttpResponse<IReservation[]>;

@Injectable({ providedIn: 'root' })
export class ReservationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reservations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(reservation: NewReservation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservation);
    return this.http
      .post<RestReservation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(reservation: IReservation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservation);
    return this.http
      .put<RestReservation>(`${this.resourceUrl}/${this.getReservationIdentifier(reservation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(reservation: PartialUpdateReservation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservation);
    return this.http
      .patch<RestReservation>(`${this.resourceUrl}/${this.getReservationIdentifier(reservation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestReservation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReservation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReservationIdentifier(reservation: Pick<IReservation, 'id'>): number {
    return reservation.id;
  }

  compareReservation(o1: Pick<IReservation, 'id'> | null, o2: Pick<IReservation, 'id'> | null): boolean {
    return o1 && o2 ? this.getReservationIdentifier(o1) === this.getReservationIdentifier(o2) : o1 === o2;
  }

  addReservationToCollectionIfMissing<Type extends Pick<IReservation, 'id'>>(
    reservationCollection: Type[],
    ...reservationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const reservations: Type[] = reservationsToCheck.filter(isPresent);
    if (reservations.length > 0) {
      const reservationCollectionIdentifiers = reservationCollection.map(
        reservationItem => this.getReservationIdentifier(reservationItem)!
      );
      const reservationsToAdd = reservations.filter(reservationItem => {
        const reservationIdentifier = this.getReservationIdentifier(reservationItem);
        if (reservationCollectionIdentifiers.includes(reservationIdentifier)) {
          return false;
        }
        reservationCollectionIdentifiers.push(reservationIdentifier);
        return true;
      });
      return [...reservationsToAdd, ...reservationCollection];
    }
    return reservationCollection;
  }

  protected convertDateFromClient<T extends IReservation | NewReservation | PartialUpdateReservation>(reservation: T): RestOf<T> {
    return {
      ...reservation,
      date: reservation.date?.format(DATE_FORMAT) ?? null,
      heureDebut: reservation.heureDebut?.toJSON() ?? null,
      heureFin: reservation.heureFin?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReservation: RestReservation): IReservation {
    return {
      ...restReservation,
      date: restReservation.date ? dayjs(restReservation.date) : undefined,
      heureDebut: restReservation.heureDebut ? dayjs(restReservation.heureDebut) : undefined,
      heureFin: restReservation.heureFin ? dayjs(restReservation.heureFin) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReservation>): HttpResponse<IReservation> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReservation[]>): HttpResponse<IReservation[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
