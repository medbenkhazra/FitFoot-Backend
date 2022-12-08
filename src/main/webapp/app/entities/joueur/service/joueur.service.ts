import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IJoueur, NewJoueur } from '../joueur.model';

export type PartialUpdateJoueur = Partial<IJoueur> & Pick<IJoueur, 'id'>;

type RestOf<T extends IJoueur | NewJoueur> = Omit<T, 'birthDay'> & {
  birthDay?: string | null;
};

export type RestJoueur = RestOf<IJoueur>;

export type NewRestJoueur = RestOf<NewJoueur>;

export type PartialUpdateRestJoueur = RestOf<PartialUpdateJoueur>;

export type EntityResponseType = HttpResponse<IJoueur>;
export type EntityArrayResponseType = HttpResponse<IJoueur[]>;

@Injectable({ providedIn: 'root' })
export class JoueurService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/joueurs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(joueur: NewJoueur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(joueur);
    return this.http
      .post<RestJoueur>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(joueur: IJoueur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(joueur);
    return this.http
      .put<RestJoueur>(`${this.resourceUrl}/${this.getJoueurIdentifier(joueur)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(joueur: PartialUpdateJoueur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(joueur);
    return this.http
      .patch<RestJoueur>(`${this.resourceUrl}/${this.getJoueurIdentifier(joueur)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestJoueur>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestJoueur[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getJoueurIdentifier(joueur: Pick<IJoueur, 'id'>): number {
    return joueur.id;
  }

  compareJoueur(o1: Pick<IJoueur, 'id'> | null, o2: Pick<IJoueur, 'id'> | null): boolean {
    return o1 && o2 ? this.getJoueurIdentifier(o1) === this.getJoueurIdentifier(o2) : o1 === o2;
  }

  addJoueurToCollectionIfMissing<Type extends Pick<IJoueur, 'id'>>(
    joueurCollection: Type[],
    ...joueursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const joueurs: Type[] = joueursToCheck.filter(isPresent);
    if (joueurs.length > 0) {
      const joueurCollectionIdentifiers = joueurCollection.map(joueurItem => this.getJoueurIdentifier(joueurItem)!);
      const joueursToAdd = joueurs.filter(joueurItem => {
        const joueurIdentifier = this.getJoueurIdentifier(joueurItem);
        if (joueurCollectionIdentifiers.includes(joueurIdentifier)) {
          return false;
        }
        joueurCollectionIdentifiers.push(joueurIdentifier);
        return true;
      });
      return [...joueursToAdd, ...joueurCollection];
    }
    return joueurCollection;
  }

  protected convertDateFromClient<T extends IJoueur | NewJoueur | PartialUpdateJoueur>(joueur: T): RestOf<T> {
    return {
      ...joueur,
      birthDay: joueur.birthDay?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restJoueur: RestJoueur): IJoueur {
    return {
      ...restJoueur,
      birthDay: restJoueur.birthDay ? dayjs(restJoueur.birthDay) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestJoueur>): HttpResponse<IJoueur> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestJoueur[]>): HttpResponse<IJoueur[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
