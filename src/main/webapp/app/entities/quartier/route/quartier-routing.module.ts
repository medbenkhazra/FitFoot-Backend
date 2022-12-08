import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { QuartierComponent } from '../list/quartier.component';
import { QuartierDetailComponent } from '../detail/quartier-detail.component';
import { QuartierUpdateComponent } from '../update/quartier-update.component';
import { QuartierRoutingResolveService } from './quartier-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const quartierRoute: Routes = [
  {
    path: '',
    component: QuartierComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QuartierDetailComponent,
    resolve: {
      quartier: QuartierRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QuartierUpdateComponent,
    resolve: {
      quartier: QuartierRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QuartierUpdateComponent,
    resolve: {
      quartier: QuartierRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(quartierRoute)],
  exports: [RouterModule],
})
export class QuartierRoutingModule {}
