import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { QuartierComponent } from './list/quartier.component';
import { QuartierDetailComponent } from './detail/quartier-detail.component';
import { QuartierUpdateComponent } from './update/quartier-update.component';
import { QuartierDeleteDialogComponent } from './delete/quartier-delete-dialog.component';
import { QuartierRoutingModule } from './route/quartier-routing.module';

@NgModule({
  imports: [SharedModule, QuartierRoutingModule],
  declarations: [QuartierComponent, QuartierDetailComponent, QuartierUpdateComponent, QuartierDeleteDialogComponent],
})
export class QuartierModule {}
