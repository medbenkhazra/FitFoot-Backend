import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TerrainComponent } from '../list/terrain.component';
import { TerrainDetailComponent } from '../detail/terrain-detail.component';
import { TerrainUpdateComponent } from '../update/terrain-update.component';
import { TerrainRoutingResolveService } from './terrain-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const terrainRoute: Routes = [
  {
    path: '',
    component: TerrainComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TerrainDetailComponent,
    resolve: {
      terrain: TerrainRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TerrainUpdateComponent,
    resolve: {
      terrain: TerrainRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TerrainUpdateComponent,
    resolve: {
      terrain: TerrainRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(terrainRoute)],
  exports: [RouterModule],
})
export class TerrainRoutingModule {}
