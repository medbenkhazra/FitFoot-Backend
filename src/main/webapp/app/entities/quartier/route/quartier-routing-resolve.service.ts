import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IQuartier } from '../quartier.model';
import { QuartierService } from '../service/quartier.service';

@Injectable({ providedIn: 'root' })
export class QuartierRoutingResolveService implements Resolve<IQuartier | null> {
  constructor(protected service: QuartierService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IQuartier | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((quartier: HttpResponse<IQuartier>) => {
          if (quartier.body) {
            return of(quartier.body);
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
