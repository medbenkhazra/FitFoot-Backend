import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TerrainComponent } from './list/terrain.component';
import { TerrainDetailComponent } from './detail/terrain-detail.component';
import { TerrainUpdateComponent } from './update/terrain-update.component';
import { TerrainDeleteDialogComponent } from './delete/terrain-delete-dialog.component';
import { TerrainRoutingModule } from './route/terrain-routing.module';

@NgModule({
  imports: [SharedModule, TerrainRoutingModule],
  declarations: [TerrainComponent, TerrainDetailComponent, TerrainUpdateComponent, TerrainDeleteDialogComponent],
})
export class TerrainModule {}
