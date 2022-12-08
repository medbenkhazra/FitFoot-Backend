import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ReservationFormService, ReservationFormGroup } from './reservation-form.service';
import { IReservation } from '../reservation.model';
import { ReservationService } from '../service/reservation.service';
import { ITerrain } from 'app/entities/terrain/terrain.model';
import { TerrainService } from 'app/entities/terrain/service/terrain.service';

@Component({
  selector: 'jhi-reservation-update',
  templateUrl: './reservation-update.component.html',
})
export class ReservationUpdateComponent implements OnInit {
  isSaving = false;
  reservation: IReservation | null = null;

  terrainsSharedCollection: ITerrain[] = [];

  editForm: ReservationFormGroup = this.reservationFormService.createReservationFormGroup();

  constructor(
    protected reservationService: ReservationService,
    protected reservationFormService: ReservationFormService,
    protected terrainService: TerrainService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTerrain = (o1: ITerrain | null, o2: ITerrain | null): boolean => this.terrainService.compareTerrain(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reservation }) => {
      this.reservation = reservation;
      if (reservation) {
        this.updateForm(reservation);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reservation = this.reservationFormService.getReservation(this.editForm);
    if (reservation.id !== null) {
      this.subscribeToSaveResponse(this.reservationService.update(reservation));
    } else {
      this.subscribeToSaveResponse(this.reservationService.create(reservation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReservation>>): void {
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

  protected updateForm(reservation: IReservation): void {
    this.reservation = reservation;
    this.reservationFormService.resetForm(this.editForm, reservation);

    this.terrainsSharedCollection = this.terrainService.addTerrainToCollectionIfMissing<ITerrain>(
      this.terrainsSharedCollection,
      reservation.terrain
    );
  }

  protected loadRelationshipsOptions(): void {
    this.terrainService
      .query()
      .pipe(map((res: HttpResponse<ITerrain[]>) => res.body ?? []))
      .pipe(
        map((terrains: ITerrain[]) => this.terrainService.addTerrainToCollectionIfMissing<ITerrain>(terrains, this.reservation?.terrain))
      )
      .subscribe((terrains: ITerrain[]) => (this.terrainsSharedCollection = terrains));
  }
}
