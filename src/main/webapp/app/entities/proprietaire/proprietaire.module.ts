import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProprietaireComponent } from './list/proprietaire.component';
import { ProprietaireDetailComponent } from './detail/proprietaire-detail.component';
import { ProprietaireUpdateComponent } from './update/proprietaire-update.component';
import { ProprietaireDeleteDialogComponent } from './delete/proprietaire-delete-dialog.component';
import { ProprietaireRoutingModule } from './route/proprietaire-routing.module';

@NgModule({
  imports: [SharedModule, ProprietaireRoutingModule],
  declarations: [ProprietaireComponent, ProprietaireDetailComponent, ProprietaireUpdateComponent, ProprietaireDeleteDialogComponent],
})
export class ProprietaireModule {}
