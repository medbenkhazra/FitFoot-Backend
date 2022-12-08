import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IQuartier, NewQuartier } from '../quartier.model';

export type PartialUpdateQuartier = Partial<IQuartier> & Pick<IQuartier, 'id'>;

export type EntityResponseType = HttpResponse<IQuartier>;
export type EntityArrayResponseType = HttpResponse<IQuartier[]>;

@Injectable({ providedIn: 'root' })
export class QuartierService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/quartiers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(quartier: NewQuartier): Observable<EntityResponseType> {
    return this.http.post<IQuartier>(this.resourceUrl, quartier, { observe: 'response' });
  }

  update(quartier: IQuartier): Observable<EntityResponseType> {
    return this.http.put<IQuartier>(`${this.resourceUrl}/${this.getQuartierIdentifier(quartier)}`, quartier, { observe: 'response' });
  }

  partialUpdate(quartier: PartialUpdateQuartier): Observable<EntityResponseType> {
    return this.http.patch<IQuartier>(`${this.resourceUrl}/${this.getQuartierIdentifier(quartier)}`, quartier, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IQuartier>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IQuartier[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getQuartierIdentifier(quartier: Pick<IQuartier, 'id'>): number {
    return quartier.id;
  }

  compareQuartier(o1: Pick<IQuartier, 'id'> | null, o2: Pick<IQuartier, 'id'> | null): boolean {
    return o1 && o2 ? this.getQuartierIdentifier(o1) === this.getQuartierIdentifier(o2) : o1 === o2;
  }

  addQuartierToCollectionIfMissing<Type extends Pick<IQuartier, 'id'>>(
    quartierCollection: Type[],
    ...quartiersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const quartiers: Type[] = quartiersToCheck.filter(isPresent);
    if (quartiers.length > 0) {
      const quartierCollectionIdentifiers = quartierCollection.map(quartierItem => this.getQuartierIdentifier(quartierItem)!);
      const quartiersToAdd = quartiers.filter(quartierItem => {
        const quartierIdentifier = this.getQuartierIdentifier(quartierItem);
        if (quartierCollectionIdentifiers.includes(quartierIdentifier)) {
          return false;
        }
        quartierCollectionIdentifiers.push(quartierIdentifier);
        return true;
      });
      return [...quartiersToAdd, ...quartierCollection];
    }
    return quartierCollection;
  }
}
