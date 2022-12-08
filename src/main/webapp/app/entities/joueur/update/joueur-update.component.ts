import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { JoueurFormService, JoueurFormGroup } from './joueur-form.service';
import { IJoueur } from '../joueur.model';
import { JoueurService } from '../service/joueur.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IEquipe } from 'app/entities/equipe/equipe.model';
import { EquipeService } from 'app/entities/equipe/service/equipe.service';
import { IQuartier } from 'app/entities/quartier/quartier.model';
import { QuartierService } from 'app/entities/quartier/service/quartier.service';
import { GENDER } from 'app/entities/enumerations/gender.model';

@Component({
  selector: 'jhi-joueur-update',
  templateUrl: './joueur-update.component.html',
})
export class JoueurUpdateComponent implements OnInit {
  isSaving = false;
  joueur: IJoueur | null = null;
  gENDERValues = Object.keys(GENDER);

  usersSharedCollection: IUser[] = [];
  equipesSharedCollection: IEquipe[] = [];
  quartiersSharedCollection: IQuartier[] = [];

  editForm: JoueurFormGroup = this.joueurFormService.createJoueurFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected joueurService: JoueurService,
    protected joueurFormService: JoueurFormService,
    protected userService: UserService,
    protected equipeService: EquipeService,
    protected quartierService: QuartierService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareEquipe = (o1: IEquipe | null, o2: IEquipe | null): boolean => this.equipeService.compareEquipe(o1, o2);

  compareQuartier = (o1: IQuartier | null, o2: IQuartier | null): boolean => this.quartierService.compareQuartier(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ joueur }) => {
      this.joueur = joueur;
      if (joueur) {
        this.updateForm(joueur);
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
    const joueur = this.joueurFormService.getJoueur(this.editForm);
    if (joueur.id !== null) {
      this.subscribeToSaveResponse(this.joueurService.update(joueur));
    } else {
      this.subscribeToSaveResponse(this.joueurService.create(joueur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJoueur>>): void {
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

  protected updateForm(joueur: IJoueur): void {
    this.joueur = joueur;
    this.joueurFormService.resetForm(this.editForm, joueur);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, joueur.user);
    this.equipesSharedCollection = this.equipeService.addEquipeToCollectionIfMissing<IEquipe>(
      this.equipesSharedCollection,
      ...(joueur.equipes ?? [])
    );
    this.quartiersSharedCollection = this.quartierService.addQuartierToCollectionIfMissing<IQuartier>(
      this.quartiersSharedCollection,
      joueur.quartier
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.joueur?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.equipeService
      .query()
      .pipe(map((res: HttpResponse<IEquipe[]>) => res.body ?? []))
      .pipe(
        map((equipes: IEquipe[]) => this.equipeService.addEquipeToCollectionIfMissing<IEquipe>(equipes, ...(this.joueur?.equipes ?? [])))
      )
      .subscribe((equipes: IEquipe[]) => (this.equipesSharedCollection = equipes));

    this.quartierService
      .query()
      .pipe(map((res: HttpResponse<IQuartier[]>) => res.body ?? []))
      .pipe(
        map((quartiers: IQuartier[]) => this.quartierService.addQuartierToCollectionIfMissing<IQuartier>(quartiers, this.joueur?.quartier))
      )
      .subscribe((quartiers: IQuartier[]) => (this.quartiersSharedCollection = quartiers));
  }
}
