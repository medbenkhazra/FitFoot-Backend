import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IComplexe } from '../complexe.model';
import { ComplexeService } from '../service/complexe.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './complexe-delete-dialog.component.html',
})
export class ComplexeDeleteDialogComponent {
  complexe?: IComplexe;

  constructor(protected complexeService: ComplexeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.complexeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
