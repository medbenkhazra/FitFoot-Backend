import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProprietaire, NewProprietaire } from '../proprietaire.model';

export type PartialUpdateProprietaire = Partial<IProprietaire> & Pick<IProprietaire, 'id'>;

export type EntityResponseType = HttpResponse<IProprietaire>;
export type EntityArrayResponseType = HttpResponse<IProprietaire[]>;

@Injectable({ providedIn: 'root' })
export class ProprietaireService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/proprietaires');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(proprietaire: NewProprietaire): Observable<EntityResponseType> {
    return this.http.post<IProprietaire>(this.resourceUrl, proprietaire, { observe: 'response' });
  }

  update(proprietaire: IProprietaire): Observable<EntityResponseType> {
    return this.http.put<IProprietaire>(`${this.resourceUrl}/${this.getProprietaireIdentifier(proprietaire)}`, proprietaire, {
      observe: 'response',
    });
  }

  partialUpdate(proprietaire: PartialUpdateProprietaire): Observable<EntityResponseType> {
    return this.http.patch<IProprietaire>(`${this.resourceUrl}/${this.getProprietaireIdentifier(proprietaire)}`, proprietaire, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProprietaire>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProprietaire[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProprietaireIdentifier(proprietaire: Pick<IProprietaire, 'id'>): number {
    return proprietaire.id;
  }

  compareProprietaire(o1: Pick<IProprietaire, 'id'> | null, o2: Pick<IProprietaire, 'id'> | null): boolean {
    return o1 && o2 ? this.getProprietaireIdentifier(o1) === this.getProprietaireIdentifier(o2) : o1 === o2;
  }

  addProprietaireToCollectionIfMissing<Type extends Pick<IProprietaire, 'id'>>(
    proprietaireCollection: Type[],
    ...proprietairesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const proprietaires: Type[] = proprietairesToCheck.filter(isPresent);
    if (proprietaires.length > 0) {
      const proprietaireCollectionIdentifiers = proprietaireCollection.map(
        proprietaireItem => this.getProprietaireIdentifier(proprietaireItem)!
      );
      const proprietairesToAdd = proprietaires.filter(proprietaireItem => {
        const proprietaireIdentifier = this.getProprietaireIdentifier(proprietaireItem);
        if (proprietaireCollectionIdentifiers.includes(proprietaireIdentifier)) {
          return false;
        }
        proprietaireCollectionIdentifiers.push(proprietaireIdentifier);
        return true;
      });
      return [...proprietairesToAdd, ...proprietaireCollection];
    }
    return proprietaireCollection;
  }
}
