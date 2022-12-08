import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEquipe, NewEquipe } from '../equipe.model';

export type PartialUpdateEquipe = Partial<IEquipe> & Pick<IEquipe, 'id'>;

export type EntityResponseType = HttpResponse<IEquipe>;
export type EntityArrayResponseType = HttpResponse<IEquipe[]>;

@Injectable({ providedIn: 'root' })
export class EquipeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/equipes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(equipe: NewEquipe): Observable<EntityResponseType> {
    return this.http.post<IEquipe>(this.resourceUrl, equipe, { observe: 'response' });
  }

  update(equipe: IEquipe): Observable<EntityResponseType> {
    return this.http.put<IEquipe>(`${this.resourceUrl}/${this.getEquipeIdentifier(equipe)}`, equipe, { observe: 'response' });
  }

  partialUpdate(equipe: PartialUpdateEquipe): Observable<EntityResponseType> {
    return this.http.patch<IEquipe>(`${this.resourceUrl}/${this.getEquipeIdentifier(equipe)}`, equipe, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEquipe>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEquipe[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEquipeIdentifier(equipe: Pick<IEquipe, 'id'>): number {
    return equipe.id;
  }

  compareEquipe(o1: Pick<IEquipe, 'id'> | null, o2: Pick<IEquipe, 'id'> | null): boolean {
    return o1 && o2 ? this.getEquipeIdentifier(o1) === this.getEquipeIdentifier(o2) : o1 === o2;
  }

  addEquipeToCollectionIfMissing<Type extends Pick<IEquipe, 'id'>>(
    equipeCollection: Type[],
    ...equipesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const equipes: Type[] = equipesToCheck.filter(isPresent);
    if (equipes.length > 0) {
      const equipeCollectionIdentifiers = equipeCollection.map(equipeItem => this.getEquipeIdentifier(equipeItem)!);
      const equipesToAdd = equipes.filter(equipeItem => {
        const equipeIdentifier = this.getEquipeIdentifier(equipeItem);
        if (equipeCollectionIdentifiers.includes(equipeIdentifier)) {
          return false;
        }
        equipeCollectionIdentifiers.push(equipeIdentifier);
        return true;
      });
      return [...equipesToAdd, ...equipeCollection];
    }
    return equipeCollection;
  }
}
