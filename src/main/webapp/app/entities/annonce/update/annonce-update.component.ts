import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AnnonceFormService, AnnonceFormGroup } from './annonce-form.service';
import { IAnnonce } from '../annonce.model';
import { AnnonceService } from '../service/annonce.service';
import { ITerrain } from 'app/entities/terrain/terrain.model';
import { TerrainService } from 'app/entities/terrain/service/terrain.service';
import { IJoueur } from 'app/entities/joueur/joueur.model';
import { JoueurService } from 'app/entities/joueur/service/joueur.service';
import { STATUS } from 'app/entities/enumerations/status.model';

@Component({
  selector: 'jhi-annonce-update',
  templateUrl: './annonce-update.component.html',
})
export class AnnonceUpdateComponent implements OnInit {
  isSaving = false;
  annonce: IAnnonce | null = null;
  sTATUSValues = Object.keys(STATUS);

  terrainsCollection: ITerrain[] = [];
  joueursSharedCollection: IJoueur[] = [];

  editForm: AnnonceFormGroup = this.annonceFormService.createAnnonceFormGroup();

  constructor(
    protected annonceService: AnnonceService,
    protected annonceFormService: AnnonceFormService,
    protected terrainService: TerrainService,
    protected joueurService: JoueurService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTerrain = (o1: ITerrain | null, o2: ITerrain | null): boolean => this.terrainService.compareTerrain(o1, o2);

  compareJoueur = (o1: IJoueur | null, o2: IJoueur | null): boolean => this.joueurService.compareJoueur(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ annonce }) => {
      this.annonce = annonce;
      if (annonce) {
        this.updateForm(annonce);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const annonce = this.annonceFormService.getAnnonce(this.editForm);
    if (annonce.id !== null) {
      this.subscribeToSaveResponse(this.annonceService.update(annonce));
    } else {
      this.subscribeToSaveResponse(this.annonceService.create(annonce));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAnnonce>>): void {
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

  protected updateForm(annonce: IAnnonce): void {
    this.annonce = annonce;
    this.annonceFormService.resetForm(this.editForm, annonce);

    this.terrainsCollection = this.terrainService.addTerrainToCollectionIfMissing<ITerrain>(this.terrainsCollection, annonce.terrain);
    this.joueursSharedCollection = this.joueurService.addJoueurToCollectionIfMissing<IJoueur>(
      this.joueursSharedCollection,
      annonce.responsable
    );
  }

  protected loadRelationshipsOptions(): void {
    this.terrainService
      .query({ filter: 'annonce-is-null' })
      .pipe(map((res: HttpResponse<ITerrain[]>) => res.body ?? []))
      .pipe(map((terrains: ITerrain[]) => this.terrainService.addTerrainToCollectionIfMissing<ITerrain>(terrains, this.annonce?.terrain)))
      .subscribe((terrains: ITerrain[]) => (this.terrainsCollection = terrains));

    this.joueurService
      .query()
      .pipe(map((res: HttpResponse<IJoueur[]>) => res.body ?? []))
      .pipe(map((joueurs: IJoueur[]) => this.joueurService.addJoueurToCollectionIfMissing<IJoueur>(joueurs, this.annonce?.responsable)))
      .subscribe((joueurs: IJoueur[]) => (this.joueursSharedCollection = joueurs));
  }
}
