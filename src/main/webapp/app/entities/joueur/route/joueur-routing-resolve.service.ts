import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IJoueur } from '../joueur.model';
import { JoueurService } from '../service/joueur.service';

@Injectable({ providedIn: 'root' })
export class JoueurRoutingResolveService implements Resolve<IJoueur | null> {
  constructor(protected service: JoueurService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IJoueur | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((joueur: HttpResponse<IJoueur>) => {
          if (joueur.body) {
            return of(joueur.body);
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
