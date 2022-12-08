import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { VilleComponent } from '../list/ville.component';
import { VilleDetailComponent } from '../detail/ville-detail.component';
import { VilleUpdateComponent } from '../update/ville-update.component';
import { VilleRoutingResolveService } from './ville-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const villeRoute: Routes = [
  {
    path: '',
    component: VilleComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VilleDetailComponent,
    resolve: {
      ville: VilleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VilleUpdateComponent,
    resolve: {
      ville: VilleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VilleUpdateComponent,
    resolve: {
      ville: VilleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(villeRoute)],
  exports: [RouterModule],
})
export class VilleRoutingModule {}
