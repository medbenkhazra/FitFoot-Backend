import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IComplexe } from '../complexe.model';
import { ComplexeService } from '../service/complexe.service';

@Injectable({ providedIn: 'root' })
export class ComplexeRoutingResolveService implements Resolve<IComplexe | null> {
  constructor(protected service: ComplexeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IComplexe | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((complexe: HttpResponse<IComplexe>) => {
          if (complexe.body) {
            return of(complexe.body);
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
