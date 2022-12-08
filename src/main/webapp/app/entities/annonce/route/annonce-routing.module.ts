import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AnnonceComponent } from '../list/annonce.component';
import { AnnonceDetailComponent } from '../detail/annonce-detail.component';
import { AnnonceUpdateComponent } from '../update/annonce-update.component';
import { AnnonceRoutingResolveService } from './annonce-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const annonceRoute: Routes = [
  {
    path: '',
    component: AnnonceComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AnnonceDetailComponent,
    resolve: {
      annonce: AnnonceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AnnonceUpdateComponent,
    resolve: {
      annonce: AnnonceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AnnonceUpdateComponent,
    resolve: {
      annonce: AnnonceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(annonceRoute)],
  exports: [RouterModule],
})
export class AnnonceRoutingModule {}
