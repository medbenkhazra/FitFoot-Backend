import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProprietaire } from '../proprietaire.model';
import { ProprietaireService } from '../service/proprietaire.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './proprietaire-delete-dialog.component.html',
})
export class ProprietaireDeleteDialogComponent {
  proprietaire?: IProprietaire;

  constructor(protected proprietaireService: ProprietaireService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.proprietaireService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
