import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEquipe } from '../equipe.model';
import { EquipeService } from '../service/equipe.service';

@Injectable({ providedIn: 'root' })
export class EquipeRoutingResolveService implements Resolve<IEquipe | null> {
  constructor(protected service: EquipeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEquipe | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((equipe: HttpResponse<IEquipe>) => {
          if (equipe.body) {
            return of(equipe.body);
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
