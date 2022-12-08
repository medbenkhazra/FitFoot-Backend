import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IJoueur, NewJoueur } from '../joueur.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IJoueur for edit and NewJoueurFormGroupInput for create.
 */
type JoueurFormGroupInput = IJoueur | PartialWithRequiredKeyOf<NewJoueur>;

type JoueurFormDefaults = Pick<NewJoueur, 'id' | 'equipes'>;

type JoueurFormGroupContent = {
  id: FormControl<IJoueur['id'] | NewJoueur['id']>;
  birthDay: FormControl<IJoueur['birthDay']>;
  gender: FormControl<IJoueur['gender']>;
  avatar: FormControl<IJoueur['avatar']>;
  avatarContentType: FormControl<IJoueur['avatarContentType']>;
  user: FormControl<IJoueur['user']>;
  equipes: FormControl<IJoueur['equipes']>;
  quartier: FormControl<IJoueur['quartier']>;
};

export type JoueurFormGroup = FormGroup<JoueurFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class JoueurFormService {
  createJoueurFormGroup(joueur: JoueurFormGroupInput = { id: null }): JoueurFormGroup {
    const joueurRawValue = {
      ...this.getFormDefaults(),
      ...joueur,
    };
    return new FormGroup<JoueurFormGroupContent>({
      id: new FormControl(
        { value: joueurRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      birthDay: new FormControl(joueurRawValue.birthDay),
      gender: new FormControl(joueurRawValue.gender),
      avatar: new FormControl(joueurRawValue.avatar),
      avatarContentType: new FormControl(joueurRawValue.avatarContentType),
      user: new FormControl(joueurRawValue.user),
      equipes: new FormControl(joueurRawValue.equipes ?? []),
      quartier: new FormControl(joueurRawValue.quartier),
    });
  }

  getJoueur(form: JoueurFormGroup): IJoueur | NewJoueur {
    return form.getRawValue() as IJoueur | NewJoueur;
  }

  resetForm(form: JoueurFormGroup, joueur: JoueurFormGroupInput): void {
    const joueurRawValue = { ...this.getFormDefaults(), ...joueur };
    form.reset(
      {
        ...joueurRawValue,
        id: { value: joueurRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): JoueurFormDefaults {
    return {
      id: null,
      equipes: [],
    };
  }
}
