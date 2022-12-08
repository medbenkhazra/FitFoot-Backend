import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVille, NewVille } from '../ville.model';

export type PartialUpdateVille = Partial<IVille> & Pick<IVille, 'id'>;

export type EntityResponseType = HttpResponse<IVille>;
export type EntityArrayResponseType = HttpResponse<IVille[]>;

@Injectable({ providedIn: 'root' })
export class VilleService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/villes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ville: NewVille): Observable<EntityResponseType> {
    return this.http.post<IVille>(this.resourceUrl, ville, { observe: 'response' });
  }

  update(ville: IVille): Observable<EntityResponseType> {
    return this.http.put<IVille>(`${this.resourceUrl}/${this.getVilleIdentifier(ville)}`, ville, { observe: 'response' });
  }

  partialUpdate(ville: PartialUpdateVille): Observable<EntityResponseType> {
    return this.http.patch<IVille>(`${this.resourceUrl}/${this.getVilleIdentifier(ville)}`, ville, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVille>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVille[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getVilleIdentifier(ville: Pick<IVille, 'id'>): number {
    return ville.id;
  }

  compareVille(o1: Pick<IVille, 'id'> | null, o2: Pick<IVille, 'id'> | null): boolean {
    return o1 && o2 ? this.getVilleIdentifier(o1) === this.getVilleIdentifier(o2) : o1 === o2;
  }

  addVilleToCollectionIfMissing<Type extends Pick<IVille, 'id'>>(
    villeCollection: Type[],
    ...villesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const villes: Type[] = villesToCheck.filter(isPresent);
    if (villes.length > 0) {
      const villeCollectionIdentifiers = villeCollection.map(villeItem => this.getVilleIdentifier(villeItem)!);
      const villesToAdd = villes.filter(villeItem => {
        const villeIdentifier = this.getVilleIdentifier(villeItem);
        if (villeCollectionIdentifiers.includes(villeIdentifier)) {
          return false;
        }
        villeCollectionIdentifiers.push(villeIdentifier);
        return true;
      });
      return [...villesToAdd, ...villeCollection];
    }
    return villeCollection;
  }
}
