import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { EquipeFormService, EquipeFormGroup } from './equipe-form.service';
import { IEquipe } from '../equipe.model';
import { EquipeService } from '../service/equipe.service';

@Component({
  selector: 'jhi-equipe-update',
  templateUrl: './equipe-update.component.html',
})
export class EquipeUpdateComponent implements OnInit {
  isSaving = false;
  equipe: IEquipe | null = null;

  editForm: EquipeFormGroup = this.equipeFormService.createEquipeFormGroup();

  constructor(
    protected equipeService: EquipeService,
    protected equipeFormService: EquipeFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ equipe }) => {
      this.equipe = equipe;
      if (equipe) {
        this.updateForm(equipe);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const equipe = this.equipeFormService.getEquipe(this.editForm);
    if (equipe.id !== null) {
      this.subscribeToSaveResponse(this.equipeService.update(equipe));
    } else {
      this.subscribeToSaveResponse(this.equipeService.create(equipe));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEquipe>>): void {
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

  protected updateForm(equipe: IEquipe): void {
    this.equipe = equipe;
    this.equipeFormService.resetForm(this.editForm, equipe);
  }
}
