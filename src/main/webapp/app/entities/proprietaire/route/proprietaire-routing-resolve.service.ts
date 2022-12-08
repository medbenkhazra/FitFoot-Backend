import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProprietaire } from '../proprietaire.model';
import { ProprietaireService } from '../service/proprietaire.service';

@Injectable({ providedIn: 'root' })
export class ProprietaireRoutingResolveService implements Resolve<IProprietaire | null> {
  constructor(protected service: ProprietaireService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProprietaire | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((proprietaire: HttpResponse<IProprietaire>) => {
          if (proprietaire.body) {
            return of(proprietaire.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
