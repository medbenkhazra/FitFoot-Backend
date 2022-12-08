import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ComplexeComponent } from '../list/complexe.component';
import { ComplexeDetailComponent } from '../detail/complexe-detail.component';
import { ComplexeUpdateComponent } from '../update/complexe-update.component';
import { ComplexeRoutingResolveService } from './complexe-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const complexeRoute: Routes = [
  {
    path: '',
    component: ComplexeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ComplexeDetailComponent,
    resolve: {
      complexe: ComplexeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ComplexeUpdateComponent,
    resolve: {
      complexe: ComplexeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ComplexeUpdateComponent,
    resolve: {
      complexe: ComplexeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(complexeRoute)],
  exports: [RouterModule],
})
export class ComplexeRoutingModule {}
