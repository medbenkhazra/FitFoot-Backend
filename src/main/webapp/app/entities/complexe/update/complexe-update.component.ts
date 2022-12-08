import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ComplexeFormService, ComplexeFormGroup } from './complexe-form.service';
import { IComplexe } from '../complexe.model';
import { ComplexeService } from '../service/complexe.service';
import { IQuartier } from 'app/entities/quartier/quartier.model';
import { QuartierService } from 'app/entities/quartier/service/quartier.service';
import { IProprietaire } from 'app/entities/proprietaire/proprietaire.model';
import { ProprietaireService } from 'app/entities/proprietaire/service/proprietaire.service';

@Component({
  selector: 'jhi-complexe-update',
  templateUrl: './complexe-update.component.html',
})
export class ComplexeUpdateComponent implements OnInit {
  isSaving = false;
  complexe: IComplexe | null = null;

  quartiersSharedCollection: IQuartier[] = [];
  proprietairesSharedCollection: IProprietaire[] = [];

  editForm: ComplexeFormGroup = this.complexeFormService.createComplexeFormGroup();

  constructor(
    protected complexeService: ComplexeService,
    protected complexeFormService: ComplexeFormService,
    protected quartierService: QuartierService,
    protected proprietaireService: ProprietaireService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareQuartier = (o1: IQuartier | null, o2: IQuartier | null): boolean => this.quartierService.compareQuartier(o1, o2);

  compareProprietaire = (o1: IProprietaire | null, o2: IProprietaire | null): boolean =>
    this.proprietaireService.compareProprietaire(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ complexe }) => {
      this.complexe = complexe;
      if (complexe) {
        this.updateForm(complexe);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const complexe = this.complexeFormService.getComplexe(this.editForm);
    if (complexe.id !== null) {
      this.subscribeToSaveResponse(this.complexeService.update(complexe));
    } else {
      this.subscribeToSaveResponse(this.complexeService.create(complexe));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IComplexe>>): void {
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

  protected updateForm(complexe: IComplexe): void {
    this.complexe = complexe;
    this.complexeFormService.resetForm(this.editForm, complexe);

    this.quartiersSharedCollection = this.quartierService.addQuartierToCollectionIfMissing<IQuartier>(
      this.quartiersSharedCollection,
      complexe.quartier
    );
    this.proprietairesSharedCollection = this.proprietaireService.addProprietaireToCollectionIfMissing<IProprietaire>(
      this.proprietairesSharedCollection,
      complexe.proprietaire
    );
  }

  protected loadRelationshipsOptions(): void {
    this.quartierService
      .query()
      .pipe(map((res: HttpResponse<IQuartier[]>) => res.body ?? []))
      .pipe(
        map((quartiers: IQuartier[]) =>
          this.quartierService.addQuartierToCollectionIfMissing<IQuartier>(quartiers, this.complexe?.quartier)
        )
      )
      .subscribe((quartiers: IQuartier[]) => (this.quartiersSharedCollection = quartiers));

    this.proprietaireService
      .query()
      .pipe(map((res: HttpResponse<IProprietaire[]>) => res.body ?? []))
      .pipe(
        map((proprietaires: IProprietaire[]) =>
          this.proprietaireService.addProprietaireToCollectionIfMissing<IProprietaire>(proprietaires, this.complexe?.proprietaire)
        )
      )
      .subscribe((proprietaires: IProprietaire[]) => (this.proprietairesSharedCollection = proprietaires));
  }
}
