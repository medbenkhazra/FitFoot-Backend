import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAnnonce, NewAnnonce } from '../annonce.model';

export type PartialUpdateAnnonce = Partial<IAnnonce> & Pick<IAnnonce, 'id'>;

type RestOf<T extends IAnnonce | NewAnnonce> = Omit<T, 'heureDebut' | 'heureFin'> & {
  heureDebut?: string | null;
  heureFin?: string | null;
};

export type RestAnnonce = RestOf<IAnnonce>;

export type NewRestAnnonce = RestOf<NewAnnonce>;

export type PartialUpdateRestAnnonce = RestOf<PartialUpdateAnnonce>;

export type EntityResponseType = HttpResponse<IAnnonce>;
export type EntityArrayResponseType = HttpResponse<IAnnonce[]>;

@Injectable({ providedIn: 'root' })
export class AnnonceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/annonces');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(annonce: NewAnnonce): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(annonce);
    return this.http
      .post<RestAnnonce>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(annonce: IAnnonce): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(annonce);
    return this.http
      .put<RestAnnonce>(`${this.resourceUrl}/${this.getAnnonceIdentifier(annonce)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(annonce: PartialUpdateAnnonce): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(annonce);
    return this.http
      .patch<RestAnnonce>(`${this.resourceUrl}/${this.getAnnonceIdentifier(annonce)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestAnnonce>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAnnonce[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAnnonceIdentifier(annonce: Pick<IAnnonce, 'id'>): number {
    return annonce.id;
  }

  compareAnnonce(o1: Pick<IAnnonce, 'id'> | null, o2: Pick<IAnnonce, 'id'> | null): boolean {
    return o1 && o2 ? this.getAnnonceIdentifier(o1) === this.getAnnonceIdentifier(o2) : o1 === o2;
  }

  addAnnonceToCollectionIfMissing<Type extends Pick<IAnnonce, 'id'>>(
    annonceCollection: Type[],
    ...annoncesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const annonces: Type[] = annoncesToCheck.filter(isPresent);
    if (annonces.length > 0) {
      const annonceCollectionIdentifiers = annonceCollection.map(annonceItem => this.getAnnonceIdentifier(annonceItem)!);
      const annoncesToAdd = annonces.filter(annonceItem => {
        const annonceIdentifier = this.getAnnonceIdentifier(annonceItem);
        if (annonceCollectionIdentifiers.includes(annonceIdentifier)) {
          return false;
        }
        annonceCollectionIdentifiers.push(annonceIdentifier);
        return true;
      });
      return [...annoncesToAdd, ...annonceCollection];
    }
    return annonceCollection;
  }

  protected convertDateFromClient<T extends IAnnonce | NewAnnonce | PartialUpdateAnnonce>(annonce: T): RestOf<T> {
    return {
      ...annonce,
      heureDebut: annonce.heureDebut?.toJSON() ?? null,
      heureFin: annonce.heureFin?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restAnnonce: RestAnnonce): IAnnonce {
    return {
      ...restAnnonce,
      heureDebut: restAnnonce.heureDebut ? dayjs(restAnnonce.heureDebut) : undefined,
      heureFin: restAnnonce.heureFin ? dayjs(restAnnonce.heureFin) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestAnnonce>): HttpResponse<IAnnonce> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestAnnonce[]>): HttpResponse<IAnnonce[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
