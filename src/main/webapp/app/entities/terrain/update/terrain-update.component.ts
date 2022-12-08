import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TerrainFormService, TerrainFormGroup } from './terrain-form.service';
import { ITerrain } from '../terrain.model';
import { TerrainService } from '../service/terrain.service';
import { IComplexe } from 'app/entities/complexe/complexe.model';
import { ComplexeService } from 'app/entities/complexe/service/complexe.service';

@Component({
  selector: 'jhi-terrain-update',
  templateUrl: './terrain-update.component.html',
})
export class TerrainUpdateComponent implements OnInit {
  isSaving = false;
  terrain: ITerrain | null = null;

  complexesSharedCollection: IComplexe[] = [];

  editForm: TerrainFormGroup = this.terrainFormService.createTerrainFormGroup();

  constructor(
    protected terrainService: TerrainService,
    protected terrainFormService: TerrainFormService,
    protected complexeService: ComplexeService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareComplexe = (o1: IComplexe | null, o2: IComplexe | null): boolean => this.complexeService.compareComplexe(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ terrain }) => {
      this.terrain = terrain;
      if (terrain) {
        this.updateForm(terrain);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const terrain = this.terrainFormService.getTerrain(this.editForm);
    if (terrain.id !== null) {
      this.subscribeToSaveResponse(this.terrainService.update(terrain));
    } else {
      this.subscribeToSaveResponse(this.terrainService.create(terrain));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITerrain>>): void {
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

  protected updateForm(terrain: ITerrain): void {
    this.terrain = terrain;
    this.terrainFormService.resetForm(this.editForm, terrain);

    this.complexesSharedCollection = this.complexeService.addComplexeToCollectionIfMissing<IComplexe>(
      this.complexesSharedCollection,
      terrain.complexe
    );
  }

  protected loadRelationshipsOptions(): void {
    this.complexeService
      .query()
      .pipe(map((res: HttpResponse<IComplexe[]>) => res.body ?? []))
      .pipe(
        map((complexes: IComplexe[]) => this.complexeService.addComplexeToCollectionIfMissing<IComplexe>(complexes, this.terrain?.complexe))
      )
      .subscribe((complexes: IComplexe[]) => (this.complexesSharedCollection = complexes));
  }
}
