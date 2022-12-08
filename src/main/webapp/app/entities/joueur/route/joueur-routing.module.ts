import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { JoueurComponent } from '../list/joueur.component';
import { JoueurDetailComponent } from '../detail/joueur-detail.component';
import { JoueurUpdateComponent } from '../update/joueur-update.component';
import { JoueurRoutingResolveService } from './joueur-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const joueurRoute: Routes = [
  {
    path: '',
    component: JoueurComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: JoueurDetailComponent,
    resolve: {
      joueur: JoueurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: JoueurUpdateComponent,
    resolve: {
      joueur: JoueurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: JoueurUpdateComponent,
    resolve: {
      joueur: JoueurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(joueurRoute)],
  exports: [RouterModule],
})
export class JoueurRoutingModule {}
