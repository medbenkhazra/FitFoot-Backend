import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEquipe, NewEquipe } from '../equipe.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEquipe for edit and NewEquipeFormGroupInput for create.
 */
type EquipeFormGroupInput = IEquipe | PartialWithRequiredKeyOf<NewEquipe>;

type EquipeFormDefaults = Pick<NewEquipe, 'id' | 'joueurs'>;

type EquipeFormGroupContent = {
  id: FormControl<IEquipe['id'] | NewEquipe['id']>;
  annonce: FormControl<IEquipe['annonce']>;
  joueurs: FormControl<IEquipe['joueurs']>;
};

export type EquipeFormGroup = FormGroup<EquipeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EquipeFormService {
  createEquipeFormGroup(equipe: EquipeFormGroupInput = { id: null }): EquipeFormGroup {
    const equipeRawValue = {
      ...this.getFormDefaults(),
      ...equipe,
    };
    return new FormGroup<EquipeFormGroupContent>({
      id: new FormControl(
        { value: equipeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      annonce: new FormControl(equipeRawValue.annonce),
      joueurs: new FormControl(equipeRawValue.joueurs ?? []),
    });
  }

  getEquipe(form: EquipeFormGroup): IEquipe | NewEquipe {
    return form.getRawValue() as IEquipe | NewEquipe;
  }

  resetForm(form: EquipeFormGroup, equipe: EquipeFormGroupInput): void {
    const equipeRawValue = { ...this.getFormDefaults(), ...equipe };
    form.reset(
      {
        ...equipeRawValue,
        id: { value: equipeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EquipeFormDefaults {
    return {
      id: null,
      joueurs: [],
    };
  }
}
