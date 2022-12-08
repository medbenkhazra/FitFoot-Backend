import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ComplexeComponent } from './list/complexe.component';
import { ComplexeDetailComponent } from './detail/complexe-detail.component';
import { ComplexeUpdateComponent } from './update/complexe-update.component';
import { ComplexeDeleteDialogComponent } from './delete/complexe-delete-dialog.component';
import { ComplexeRoutingModule } from './route/complexe-routing.module';

@NgModule({
  imports: [SharedModule, ComplexeRoutingModule],
  declarations: [ComplexeComponent, ComplexeDetailComponent, ComplexeUpdateComponent, ComplexeDeleteDialogComponent],
})
export class ComplexeModule {}
