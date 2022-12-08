import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProprietaire, NewProprietaire } from '../proprietaire.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProprietaire for edit and NewProprietaireFormGroupInput for create.
 */
type ProprietaireFormGroupInput = IProprietaire | PartialWithRequiredKeyOf<NewProprietaire>;

type ProprietaireFormDefaults = Pick<NewProprietaire, 'id'>;

type ProprietaireFormGroupContent = {
  id: FormControl<IProprietaire['id'] | NewProprietaire['id']>;
  avatar: FormControl<IProprietaire['avatar']>;
  avatarContentType: FormControl<IProprietaire['avatarContentType']>;
  cin: FormControl<IProprietaire['cin']>;
  rib: FormControl<IProprietaire['rib']>;
  numTel: FormControl<IProprietaire['numTel']>;
  user: FormControl<IProprietaire['user']>;
};

export type ProprietaireFormGroup = FormGroup<ProprietaireFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProprietaireFormService {
  createProprietaireFormGroup(proprietaire: ProprietaireFormGroupInput = { id: null }): ProprietaireFormGroup {
    const proprietaireRawValue = {
      ...this.getFormDefaults(),
      ...proprietaire,
    };
    return new FormGroup<ProprietaireFormGroupContent>({
      id: new FormControl(
        { value: proprietaireRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      avatar: new FormControl(proprietaireRawValue.avatar),
      avatarContentType: new FormControl(proprietaireRawValue.avatarContentType),
      cin: new FormControl(proprietaireRawValue.cin),
      rib: new FormControl(proprietaireRawValue.rib),
      numTel: new FormControl(proprietaireRawValue.numTel),
      user: new FormControl(proprietaireRawValue.user),
    });
  }

  getProprietaire(form: ProprietaireFormGroup): IProprietaire | NewProprietaire {
    return form.getRawValue() as IProprietaire | NewProprietaire;
  }

  resetForm(form: ProprietaireFormGroup, proprietaire: ProprietaireFormGroupInput): void {
    const proprietaireRawValue = { ...this.getFormDefaults(), ...proprietaire };
    form.reset(
      {
        ...proprietaireRawValue,
        id: { value: proprietaireRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProprietaireFormDefaults {
    return {
      id: null,
    };
  }
}
