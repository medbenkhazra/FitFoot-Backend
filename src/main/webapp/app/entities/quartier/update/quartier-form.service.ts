import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IQuartier, NewQuartier } from '../quartier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IQuartier for edit and NewQuartierFormGroupInput for create.
 */
type QuartierFormGroupInput = IQuartier | PartialWithRequiredKeyOf<NewQuartier>;

type QuartierFormDefaults = Pick<NewQuartier, 'id'>;

type QuartierFormGroupContent = {
  id: FormControl<IQuartier['id'] | NewQuartier['id']>;
  nom: FormControl<IQuartier['nom']>;
  ville: FormControl<IQuartier['ville']>;
};

export type QuartierFormGroup = FormGroup<QuartierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class QuartierFormService {
  createQuartierFormGroup(quartier: QuartierFormGroupInput = { id: null }): QuartierFormGroup {
    const quartierRawValue = {
      ...this.getFormDefaults(),
      ...quartier,
    };
    return new FormGroup<QuartierFormGroupContent>({
      id: new FormControl(
        { value: quartierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nom: new FormControl(quartierRawValue.nom),
      ville: new FormControl(quartierRawValue.ville),
    });
  }

  getQuartier(form: QuartierFormGroup): IQuartier | NewQuartier {
    return form.getRawValue() as IQuartier | NewQuartier;
  }

  resetForm(form: QuartierFormGroup, quartier: QuartierFormGroupInput): void {
    const quartierRawValue = { ...this.getFormDefaults(), ...quartier };
    form.reset(
      {
        ...quartierRawValue,
        id: { value: quartierRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): QuartierFormDefaults {
    return {
      id: null,
    };
  }
}
