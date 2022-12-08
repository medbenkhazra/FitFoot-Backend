import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProprietaire } from '../proprietaire.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-proprietaire-detail',
  templateUrl: './proprietaire-detail.component.html',
})
export class ProprietaireDetailComponent implements OnInit {
  proprietaire: IProprietaire | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ proprietaire }) => {
      this.proprietaire = proprietaire;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
