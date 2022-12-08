import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { JoueurComponent } from './list/joueur.component';
import { JoueurDetailComponent } from './detail/joueur-detail.component';
import { JoueurUpdateComponent } from './update/joueur-update.component';
import { JoueurDeleteDialogComponent } from './delete/joueur-delete-dialog.component';
import { JoueurRoutingModule } from './route/joueur-routing.module';

@NgModule({
  imports: [SharedModule, JoueurRoutingModule],
  declarations: [JoueurComponent, JoueurDetailComponent, JoueurUpdateComponent, JoueurDeleteDialogComponent],
})
export class JoueurModule {}
