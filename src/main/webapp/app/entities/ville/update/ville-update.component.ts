import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { VilleFormService, VilleFormGroup } from './ville-form.service';
import { IVille } from '../ville.model';
import { VilleService } from '../service/ville.service';

@Component({
  selector: 'jhi-ville-update',
  templateUrl: './ville-update.component.html',
})
export class VilleUpdateComponent implements OnInit {
  isSaving = false;
  ville: IVille | null = null;

  editForm: VilleFormGroup = this.villeFormService.createVilleFormGroup();

  constructor(
    protected villeService: VilleService,
    protected villeFormService: VilleFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ville }) => {
      this.ville = ville;
      if (ville) {
        this.updateForm(ville);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ville = this.villeFormService.getVille(this.editForm);
    if (ville.id !== null) {
      this.subscribeToSaveResponse(this.villeService.update(ville));
    } else {
      this.subscribeToSaveResponse(this.villeService.create(ville));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVille>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ville: IVille): void {
    this.ville = ville;
    this.villeFormService.resetForm(this.editForm, ville);
  }
}
