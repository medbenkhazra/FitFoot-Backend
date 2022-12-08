import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IComplexe, NewComplexe } from '../complexe.model';

export type PartialUpdateComplexe = Partial<IComplexe> & Pick<IComplexe, 'id'>;

export type EntityResponseType = HttpResponse<IComplexe>;
export type EntityArrayResponseType = HttpResponse<IComplexe[]>;

@Injectable({ providedIn: 'root' })
export class ComplexeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/complexes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(complexe: NewComplexe): Observable<EntityResponseType> {
    return this.http.post<IComplexe>(this.resourceUrl, complexe, { observe: 'response' });
  }

  update(complexe: IComplexe): Observable<EntityResponseType> {
    return this.http.put<IComplexe>(`${this.resourceUrl}/${this.getComplexeIdentifier(complexe)}`, complexe, { observe: 'response' });
  }

  partialUpdate(complexe: PartialUpdateComplexe): Observable<EntityResponseType> {
    return this.http.patch<IComplexe>(`${this.resourceUrl}/${this.getComplexeIdentifier(complexe)}`, complexe, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IComplexe>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IComplexe[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getComplexeIdentifier(complexe: Pick<IComplexe, 'id'>): number {
    return complexe.id;
  }

  compareComplexe(o1: Pick<IComplexe, 'id'> | null, o2: Pick<IComplexe, 'id'> | null): boolean {
    return o1 && o2 ? this.getComplexeIdentifier(o1) === this.getComplexeIdentifier(o2) : o1 === o2;
  }

  addComplexeToCollectionIfMissing<Type extends Pick<IComplexe, 'id'>>(
    complexeCollection: Type[],
    ...complexesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const complexes: Type[] = complexesToCheck.filter(isPresent);
    if (complexes.length > 0) {
      const complexeCollectionIdentifiers = complexeCollection.map(complexeItem => this.getComplexeIdentifier(complexeItem)!);
      const complexesToAdd = complexes.filter(complexeItem => {
        const complexeIdentifier = this.getComplexeIdentifier(complexeItem);
        if (complexeCollectionIdentifiers.includes(complexeIdentifier)) {
          return false;
        }
        complexeCollectionIdentifiers.push(complexeIdentifier);
        return true;
      });
      return [...complexesToAdd, ...complexeCollection];
    }
    return complexeCollection;
  }
}
