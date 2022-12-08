import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITerrain, NewTerrain } from '../terrain.model';

export type PartialUpdateTerrain = Partial<ITerrain> & Pick<ITerrain, 'id'>;

export type EntityResponseType = HttpResponse<ITerrain>;
export type EntityArrayResponseType = HttpResponse<ITerrain[]>;

@Injectable({ providedIn: 'root' })
export class TerrainService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/terrains');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(terrain: NewTerrain): Observable<EntityResponseType> {
    return this.http.post<ITerrain>(this.resourceUrl, terrain, { observe: 'response' });
  }

  update(terrain: ITerrain): Observable<EntityResponseType> {
    return this.http.put<ITerrain>(`${this.resourceUrl}/${this.getTerrainIdentifier(terrain)}`, terrain, { observe: 'response' });
  }

  partialUpdate(terrain: PartialUpdateTerrain): Observable<EntityResponseType> {
    return this.http.patch<ITerrain>(`${this.resourceUrl}/${this.getTerrainIdentifier(terrain)}`, terrain, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITerrain>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITerrain[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTerrainIdentifier(terrain: Pick<ITerrain, 'id'>): number {
    return terrain.id;
  }

  compareTerrain(o1: Pick<ITerrain, 'id'> | null, o2: Pick<ITerrain, 'id'> | null): boolean {
    return o1 && o2 ? this.getTerrainIdentifier(o1) === this.getTerrainIdentifier(o2) : o1 === o2;
  }

  addTerrainToCollectionIfMissing<Type extends Pick<ITerrain, 'id'>>(
    terrainCollection: Type[],
    ...terrainsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const terrains: Type[] = terrainsToCheck.filter(isPresent);
    if (terrains.length > 0) {
      const terrainCollectionIdentifiers = terrainCollection.map(terrainItem => this.getTerrainIdentifier(terrainItem)!);
      const terrainsToAdd = terrains.filter(terrainItem => {
        const terrainIdentifier = this.getTerrainIdentifier(terrainItem);
        if (terrainCollectionIdentifiers.includes(terrainIdentifier)) {
          return false;
        }
        terrainCollectionIdentifiers.push(terrainIdentifier);
        return true;
      });
      return [...terrainsToAdd, ...terrainCollection];
    }
    return terrainCollection;
  }
}
