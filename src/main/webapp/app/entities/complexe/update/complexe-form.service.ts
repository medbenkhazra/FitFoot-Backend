import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IComplexe, NewComplexe } from '../complexe.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IComplexe for edit and NewComplexeFormGroupInput for create.
 */
type ComplexeFormGroupInput = IComplexe | PartialWithRequiredKeyOf<NewComplexe>;

type ComplexeFormDefaults = Pick<NewComplexe, 'id'>;

type ComplexeFormGroupContent = {
  id: FormControl<IComplexe['id'] | NewComplexe['id']>;
  nom: FormControl<IComplexe['nom']>;
  longitude: FormControl<IComplexe['longitude']>;
  latitude: FormControl<IComplexe['latitude']>;
  quartier: FormControl<IComplexe['quartier']>;
  proprietaire: FormControl<IComplexe['proprietaire']>;
};

export type ComplexeFormGroup = FormGroup<ComplexeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ComplexeFormService {
  createComplexeFormGroup(complexe: ComplexeFormGroupInput = { id: null }): ComplexeFormGroup {
    const complexeRawValue = {
      ...this.getFormDefaults(),
      ...complexe,
    };
    return new FormGroup<ComplexeFormGroupContent>({
      id: new FormControl(
        { value: complexeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nom: new FormControl(complexeRawValue.nom),
      longitude: new FormControl(complexeRawValue.longitude),
      latitude: new FormControl(complexeRawValue.latitude),
      quartier: new FormControl(complexeRawValue.quartier),
      proprietaire: new FormControl(complexeRawValue.proprietaire),
    });
  }

  getComplexe(form: ComplexeFormGroup): IComplexe | NewComplexe {
    return form.getRawValue() as IComplexe | NewComplexe;
  }

  resetForm(form: ComplexeFormGroup, complexe: ComplexeFormGroupInput): void {
    const complexeRawValue = { ...this.getFormDefaults(), ...complexe };
    form.reset(
      {
        ...complexeRawValue,
        id: { value: complexeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ComplexeFormDefaults {
    return {
      id: null,
    };
  }
}
