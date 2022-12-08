import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { QuartierFormService, QuartierFormGroup } from './quartier-form.service';
import { IQuartier } from '../quartier.model';
import { QuartierService } from '../service/quartier.service';
import { IVille } from 'app/entities/ville/ville.model';
import { VilleService } from 'app/entities/ville/service/ville.service';

@Component({
  selector: 'jhi-quartier-update',
  templateUrl: './quartier-update.component.html',
})
export class QuartierUpdateComponent implements OnInit {
  isSaving = false;
  quartier: IQuartier | null = null;

  villesSharedCollection: IVille[] = [];

  editForm: QuartierFormGroup = this.quartierFormService.createQuartierFormGroup();

  constructor(
    protected quartierService: QuartierService,
    protected quartierFormService: QuartierFormService,
    protected villeService: VilleService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareVille = (o1: IVille | null, o2: IVille | null): boolean => this.villeService.compareVille(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quartier }) => {
      this.quartier = quartier;
      if (quartier) {
        this.updateForm(quartier);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const quartier = this.quartierFormService.getQuartier(this.editForm);
    if (quartier.id !== null) {
      this.subscribeToSaveResponse(this.quartierService.update(quartier));
    } else {
      this.subscribeToSaveResponse(this.quartierService.create(quartier));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQuartier>>): void {
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

  protected updateForm(quartier: IQuartier): void {
    this.quartier = quartier;
    this.quartierFormService.resetForm(this.editForm, quartier);

    this.villesSharedCollection = this.villeService.addVilleToCollectionIfMissing<IVille>(this.villesSharedCollection, quartier.ville);
  }

  protected loadRelationshipsOptions(): void {
    this.villeService
      .query()
      .pipe(map((res: HttpResponse<IVille[]>) => res.body ?? []))
      .pipe(map((villes: IVille[]) => this.villeService.addVilleToCollectionIfMissing<IVille>(villes, this.quartier?.ville)))
      .subscribe((villes: IVille[]) => (this.villesSharedCollection = villes));
  }
}
