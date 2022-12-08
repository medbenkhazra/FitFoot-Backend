import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProprietaireFormService, ProprietaireFormGroup } from './proprietaire-form.service';
import { IProprietaire } from '../proprietaire.model';
import { ProprietaireService } from '../service/proprietaire.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-proprietaire-update',
  templateUrl: './proprietaire-update.component.html',
})
export class ProprietaireUpdateComponent implements OnInit {
  isSaving = false;
  proprietaire: IProprietaire | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: ProprietaireFormGroup = this.proprietaireFormService.createProprietaireFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected proprietaireService: ProprietaireService,
    protected proprietaireFormService: ProprietaireFormService,
    protected userService: UserService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ proprietaire }) => {
      this.proprietaire = proprietaire;
      if (proprietaire) {
        this.updateForm(proprietaire);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('fitFootApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const proprietaire = this.proprietaireFormService.getProprietaire(this.editForm);
    if (proprietaire.id !== null) {
      this.subscribeToSaveResponse(this.proprietaireService.update(proprietaire));
    } else {
      this.subscribeToSaveResponse(this.proprietaireService.create(proprietaire));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProprietaire>>): void {
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

  protected updateForm(proprietaire: IProprietaire): void {
    this.proprietaire = proprietaire;
    this.proprietaireFormService.resetForm(this.editForm, proprietaire);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, proprietaire.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.proprietaire?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
