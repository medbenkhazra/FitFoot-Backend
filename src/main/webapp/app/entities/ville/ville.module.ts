import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { VilleComponent } from './list/ville.component';
import { VilleDetailComponent } from './detail/ville-detail.component';
import { VilleUpdateComponent } from './update/ville-update.component';
import { VilleDeleteDialogComponent } from './delete/ville-delete-dialog.component';
import { VilleRoutingModule } from './route/ville-routing.module';

@NgModule({
  imports: [SharedModule, VilleRoutingModule],
  declarations: [VilleComponent, VilleDetailComponent, VilleUpdateComponent, VilleDeleteDialogComponent],
})
export class VilleModule {}
