import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EquipeComponent } from '../list/equipe.component';
import { EquipeDetailComponent } from '../detail/equipe-detail.component';
import { EquipeUpdateComponent } from '../update/equipe-update.component';
import { EquipeRoutingResolveService } from './equipe-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const equipeRoute: Routes = [
  {
    path: '',
    component: EquipeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EquipeDetailComponent,
    resolve: {
      equipe: EquipeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EquipeUpdateComponent,
    resolve: {
      equipe: EquipeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EquipeUpdateComponent,
    resolve: {
      equipe: EquipeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(equipeRoute)],
  exports: [RouterModule],
})
export class EquipeRoutingModule {}
