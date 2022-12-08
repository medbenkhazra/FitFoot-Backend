import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProprietaireComponent } from '../list/proprietaire.component';
import { ProprietaireDetailComponent } from '../detail/proprietaire-detail.component';
import { ProprietaireUpdateComponent } from '../update/proprietaire-update.component';
import { ProprietaireRoutingResolveService } from './proprietaire-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const proprietaireRoute: Routes = [
  {
    path: '',
    component: ProprietaireComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProprietaireDetailComponent,
    resolve: {
      proprietaire: ProprietaireRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProprietaireUpdateComponent,
    resolve: {
      proprietaire: ProprietaireRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProprietaireUpdateComponent,
    resolve: {
      proprietaire: ProprietaireRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(proprietaireRoute)],
  exports: [RouterModule],
})
export class ProprietaireRoutingModule {}
