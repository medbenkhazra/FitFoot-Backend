import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAnnonce } from '../annonce.model';
import { AnnonceService } from '../service/annonce.service';

@Injectable({ providedIn: 'root' })
export class AnnonceRoutingResolveService implements Resolve<IAnnonce | null> {
  constructor(protected service: AnnonceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAnnonce | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((annonce: HttpResponse<IAnnonce>) => {
          if (annonce.body) {
            return of(annonce.body);
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
