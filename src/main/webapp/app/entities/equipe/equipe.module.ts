import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EquipeComponent } from './list/equipe.component';
import { EquipeDetailComponent } from './detail/equipe-detail.component';
import { EquipeUpdateComponent } from './update/equipe-update.component';
import { EquipeDeleteDialogComponent } from './delete/equipe-delete-dialog.component';
import { EquipeRoutingModule } from './route/equipe-routing.module';

@NgModule({
  imports: [SharedModule, EquipeRoutingModule],
  declarations: [EquipeComponent, EquipeDetailComponent, EquipeUpdateComponent, EquipeDeleteDialogComponent],
})
export class EquipeModule {}
